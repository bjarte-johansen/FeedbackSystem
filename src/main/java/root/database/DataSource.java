package root.database;

import root.logger.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static root.common.utils.Preconditions.checkArgument;
import static root.common.utils.Preconditions.checkNotNull;


/**
 * A simple connection pool implementation that manages a pool of database connections. It creates new connections
 * on demand up to a specified maximum limit and reuses them when they are returned to the pool.
 */

public class DataSource {

    /**
     * Functional interface representing a consumer that accepts a database connection and returns a result. This is used
     * as a parameter type for the with() method to allow executing database operations with a managed connection.
     * @param <R>
     */

    public interface ConnectionConsumer<R>{
        R run(Connection connection) throws Exception;
    }


    /**
     * Utility method to execute a database operation with a managed connection. The provided function is executed
     * with a connection that is automatically closed after the operation completes, ensuring proper resource management.
     * @param fn
     * @return
     * @param <R>
     */

    public static <R> R with(ConnectionConsumer<R> fn) {
        try (Connection connection = getConnection()) {
            return fn.run(connection);
        } catch (Exception e) {
            Logger.log("An exception occurred while executing a database operation: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }



    /**
     * This class wraps a real Connection and intercepts the close() method to return the connection to the pool
     * instead of actually closing it.
     */
    static class PooledConnection {
        public static Connection wrap(Connection real) {
            InvocationHandler h = (proxy, method, args) -> {

                if (method.getName().equals("close")) {
                    enqueueConnection((Connection) proxy);
                    return null;
                }

                if(method.getName().equals("unwrap") && args.length == 1 && args[0].equals(Connection.class)) {
                    Class<?> iface = (Class<?>) args[0];

                    if(iface.isInstance(real)) {
                        return real;
                    }

                    return real.unwrap(iface);
                }

                return method.invoke(real, args);
            };

            return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                h);
        }
    }

    // connection pool & settings
    private static final AtomicInteger TOTAL_CONNECTION_COUNT = new AtomicInteger(0);
    private static int MAX_TOTAL_CONNECTION_COUNT = 30;
    private static final BlockingQueue<Connection> CONNECTION_POOL = new ArrayBlockingQueue<>(MAX_TOTAL_CONNECTION_COUNT);


    // enable to log pool messages
    private static final boolean LOG_MESSAGES_TO_CONSOLE = false;


    public static final DataSourceConnectionParams TEST = new DataSourceConnectionParams("jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905?currentSchema=test","h184905", "pass", "test");
    public static final DataSourceConnectionParams PROD = new DataSourceConnectionParams("jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905?currentSchema=public","h184905", "pass", "public");

    // we just use a single set of connection params for simplicity, but it can be extended to support multiple sets
    // of params (fex for different tenants/schemas) if needed
    private static DataSourceConnectionParams currentDataSourceParams = TEST;

    // this field MUST be reset after each connection is returned to the pool, otherwise it will leak to
    // other connections that are checked out from the pool and cause incorrect behavior. It is used to set the
    // schema for the connection when it is checked out from the pool, so that different threads can use different
    // schemas if needed.
    public static ThreadLocal<String> THREAD_LOCAL_SCHEMA = new ThreadLocal<>();


    /*
    public static String getIdentifierQuoteString(Connection conn) {
        try {
            var meta = conn.getMetaData();
            String key = meta.getURL();

            return QUOTE.computeIfAbsent(key, k -> {
                try {
                    return meta.getIdentifierQuoteString().trim();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String quote(Connection conn, Object s) {
        String quoteSym = getIdentifierQuoteString(conn);
        return quoteSym + s.toString() + quoteSym;
    }
    */


    /*
     * usage statistics and logging
     */

    public static int getTotalCount(){
        return TOTAL_CONNECTION_COUNT.get();
    }
    public static int getAvailableCount(){
        return CONNECTION_POOL.size();
    }
    public static int getUsedCount(){
        return getTotalCount() - getAvailableCount();
    }



    /*
    logging
     */
    private static void logMessage(String msg) {
        if(LOG_MESSAGES_TO_CONSOLE) {
            Logger.log(msg);
        }
    }


    /**
     * Pre-populates the connection pool with a specified number of connections. This can be used to warm up the pool
     * at application startup to reduce latency for the first few connection requests. The method creates new
     * connections using the provided parameters and adds them to the pool, up to the maximum total connection limit.
     *
     * @param numberOfConnections
     * @param params
     * @throws SQLException
     */

    public static void warm(int numberOfConnections, DataSourceConnectionParams params) throws SQLException {
        MAX_TOTAL_CONNECTION_COUNT = (int) Math.min(MAX_TOTAL_CONNECTION_COUNT, numberOfConnections);

        for(int i=0; i < numberOfConnections; i++) {
            logMessage("Initializing connection pool, creating connection " + (i+1) + "/" + numberOfConnections);

            Connection conn = createConnection(params);
            enqueueConnection(conn);
        }
    }


    /**
     * Creates a new database connection using the provided parameters. This method is called when a new connection
     * is needed and the pool is empty.
     *
     * @param params
     * @return
     * @throws SQLException
     */

    private static Connection createConnection(DataSourceConnectionParams params) throws SQLException {
        int newCount = TOTAL_CONNECTION_COUNT.incrementAndGet();

        if(newCount > MAX_TOTAL_CONNECTION_COUNT) {
            TOTAL_CONNECTION_COUNT.decrementAndGet();
            throw new RuntimeException("Connection pool limit reached. Max pool size: " + MAX_TOTAL_CONNECTION_COUNT);
        }

        try {
            Connection conn = DriverManager.getConnection(params.url(), params.username(), params.password());
            conn.setSchema(params.defaultSchema());

            logMessage("Connection Created, available connections: " + getAvailableCount() + "/" + getTotalCount());

            return conn;
        }catch(SQLException e) {
            TOTAL_CONNECTION_COUNT.decrementAndGet();
            throw e;
        }
    }

    /**
     * Retrieves a connection from the pool. If the pool is empty, it creates a new connection using the provided parameters.
     * The method also sets the schema for the connection based on the thread-local value before returning it. The returned
     * connection is wrapped in a PooledConnection proxy that intercepts the close() method to return the connection to the pool
     * instead of actually closing it.
     *
     * @param params
     * @return
     * @throws SQLException
     */

    private static Connection dequeueConnection(DataSourceConnectionParams params) throws SQLException {
        Connection conn = CONNECTION_POOL.poll();

        if(conn == null) {
            conn = createConnection(params);
        }

        // set the schema for the connection based on the thread-local value
        String schema = THREAD_LOCAL_SCHEMA.get();
        conn.setSchema((schema != null) ? schema : params.defaultSchema());

        logMessage("Connection <<-- Pool, available connections: " + getAvailableCount() + "/" + getTotalCount());

        return PooledConnection.wrap(conn);
    }


    /**
     * Returns a connection to the pool. This method is called when a connection is closed (via the PooledConnection proxy).
     * The method unwraps the connection to get the real connection object, resets the schema to the default value, and adds
     * it back to the pool for reuse.
     *
     * @param conn
     * @throws SQLException
     */

    private static void enqueueConnection(Connection conn) throws SQLException {
        // unwrap the connection to get the real connection object
        conn = conn.unwrap(Connection.class);

        // set back to default/old schema
        conn.setSchema(currentDataSourceParams.defaultSchema());

        // return the connection to the pool
        CONNECTION_POOL.add(conn);

        logMessage("Connection -->> Pool, available connections: " + getAvailableCount() + "/" + getTotalCount());
    }


    /*
    public entry points
     */

    public static Connection getConnection() throws Exception {
        checkArgument(currentDataSourceParams != null, "DataSource has no default datasource parameters.");

        return getConnection(currentDataSourceParams);
    }

    private static Connection getConnection(DataSourceConnectionParams params) throws SQLException {
        return dequeueConnection(params);
    }
}
