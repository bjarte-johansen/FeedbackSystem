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
    private static final boolean LOG_POOL_CHANGES_TO_CONSOLE = false;


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

    public static int getTotalCount(){
        return TOTAL_CONNECTION_COUNT.get();
    }
    public static int getAvailableCount(){
        return CONNECTION_POOL.size();
    }
    public static int getUsedCount(){
        return getTotalCount() - getAvailableCount();
    }

    private static void logMessage(String msg) {
        if(LOG_POOL_CHANGES_TO_CONSOLE) {
            Logger.log(msg);
        }
    }

    public static void warmp(int numberOfConnections, DataSourceConnectionParams params) throws SQLException {
        MAX_TOTAL_CONNECTION_COUNT = (int) Math.min(MAX_TOTAL_CONNECTION_COUNT, numberOfConnections);

        for(int i=0; i < numberOfConnections; i++) {
            logMessage("Initializing connection pool, creating connection " + (i+1) + "/" + numberOfConnections);

            Connection conn = createConnection(params);
            enqueueConnection(conn);
        }
    }



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

    private static Connection dequeueConnection(DataSourceConnectionParams params) throws SQLException {
        Connection conn = CONNECTION_POOL.poll();

        if(conn == null) {
            conn = createConnection(params);
        }

        // set the schema for the connection based on the thread-local value
        String schema = THREAD_LOCAL_SCHEMA.get();
        if(schema != null) {
            conn.setSchema(schema);
        }

        logMessage("Connection <<-- Pool, available connections: " + getAvailableCount() + "/" + getTotalCount());

        return PooledConnection.wrap(conn);
    }

    private static void enqueueConnection(Connection conn) throws SQLException {
        // unwrap the connection to get the real connection object
        conn = conn.unwrap(Connection.class);

        // reset schema
        conn.setSchema(currentDataSourceParams.defaultSchema());

        // return the connection to the pool
        CONNECTION_POOL.add(conn);  // return to pool

        logMessage("Connection -->> Pool, available connections: " + getAvailableCount() + "/" + getTotalCount());
    }


    /*
    public entry points
     */

    public static Connection getConnection() throws Exception {
        if(currentDataSourceParams == null) {
            throw new IllegalStateException("DataSource has no default datasource parameters.");
        }

        return getConnection(currentDataSourceParams);
    }

    public static Connection getConnection(String schema) throws Exception {
        if(currentDataSourceParams == null) {
            throw new IllegalStateException("DataSource has no default datasource parameters.");
        }

        var conn = getConnection(currentDataSourceParams);
        conn.setSchema(schema);

        return conn;
    }

    public static Connection getConnection(DataSourceConnectionParams params) throws SQLException {
        return dequeueConnection(params);
    }
}
