package root.database;

import root.includes.logger.Logger;

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


public class CustomDataSource{
    /**
     * This class wraps a real Connection and intercepts the close() method to return the connection to the pool
     * instead of actually closing it.
     */
    static class PooledConnection {
        public static Connection wrap(Connection real, CustomDataSource ds) {
            InvocationHandler h = (proxy, method, args) -> {
                if (method.getName().equals("close")) {
                    ds.releaseConnection((Connection) proxy);
                    return null;
                }

                if(method.getName().equals("unwrap") && args.length == 1 && args[0].equals(Connection.class)) {
                    Class<?> t = (Class<?>) args[0];

                    if(t.isInstance(real)) {
                        return real;
                    }

                    return real.unwrap(t);
                }

                return method.invoke(real, args);
            };

            return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                h);
        }
    }

    //private static final CustomDataSource INSTANCE = new CustomDataSource();

    // store total connection count (eg, used = total - available)
    private final AtomicInteger TOTAL_CONNECTION_COUNT = new AtomicInteger(0);
    
    // for simplicity, we use a fixed max connection count, but it can be made configurable if needed
    private int MAX_TOTAL_CONNECTION_COUNT = 30;
    
    // actual connection pool
    private final BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(MAX_TOTAL_CONNECTION_COUNT);

    // enable to log pool messages
    private static final boolean LOG_MESSAGES_TO_CONSOLE = false;

    // connection parameters for creating new connections when pool is empty or we need more.
    private DataSourceConnectionParams currentDataSourceParams = null;


    public CustomDataSource(DataSourceConnectionParams currentDataSourceParams) {
        checkNotNull(currentDataSourceParams, "DataSourceConnectionParams cannot be null");

        this.currentDataSourceParams = currentDataSourceParams;
    }

    /*
     * usage statistics and logging
     */

    public int getTotalCount(){
        return TOTAL_CONNECTION_COUNT.get();
    }
    public int getAvailableCount(){
        return connectionPool.size();
    }
    public int getUsedCount(){
        return getTotalCount() - getAvailableCount();
    }


    /*
    logging
     */
    private void logMessage(String msg) {
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
     * @throws SQLException
     */

    public void warm(int numberOfConnections) {
        checkArgument(currentDataSourceParams != null, "DataSource has no default datasource parameters.");

        MAX_TOTAL_CONNECTION_COUNT = (int) Math.min(MAX_TOTAL_CONNECTION_COUNT, numberOfConnections);

        for(int i=0; i < numberOfConnections; i++) {
            logMessage("Initializing connection pool, creating connection " + (i+1) + "/" + numberOfConnections);

            Connection conn = createConnection(currentDataSourceParams);
            releaseConnection(conn);
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

    private Connection createConnection(DataSourceConnectionParams params) {
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
            throw new RuntimeException("An error occurred while creating connection", e);
        }
    }

    private void discardConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            logMessage("Error closing connection: " + e.getMessage());
            throw new RuntimeException("An error occurred while discarding connection", e);
        } finally {
            TOTAL_CONNECTION_COUNT.decrementAndGet();
        }
    }


    /**
     * Returns a connection to the pool. This method is called when a connection is closed (via the PooledConnection proxy).
     * The method unwraps the connection to get the real connection object, resets the schema to the default value, and adds
     * it back to the pool for reuse.
     *
     * @param conn
     * @throws SQLException
     */

    public void releaseConnection(Connection conn) {
        try {
            // unwrap the connection to get the real connection object
            conn = conn.unwrap(Connection.class);

            // return the connection to the pool
            connectionPool.offer(conn);

            logMessage("Connection -->> Pool, available connections: " + getAvailableCount() + "/" + getTotalCount());
        }catch(SQLException e) {
            throw new RuntimeException("An error occurred while releasing connection", e);
        }
    }

   /*
    public entry points
     */

    private boolean isAlive(Connection conn) {
        try {
            return conn != null && !conn.isClosed() && conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    public Connection getConnection() {
        Connection conn = getConnection(currentDataSourceParams);

        if(!isAlive(conn)) {
            throw new RuntimeException("Failed to create connection, DriverManager returned null");
        }

        return conn;
    }

    private Connection getConnection(DataSourceConnectionParams params) {
        checkArgument(currentDataSourceParams != null, "DataSource has no default datasource parameters.");

        Connection conn;

        while(true){
            conn = connectionPool.poll();

            if (conn == null) {
                conn = createConnection(params);
            }

            try{
                if(!isAlive(conn)) {
                    discardConnection(conn);
                    continue;
                }

                logMessage("Connection <<-- Pool, available connections: " + getAvailableCount() + "/" + getTotalCount());

                return PooledConnection.wrap(conn, this);
            }catch(Exception e) {
                discardConnection(conn);
            }
        }
    }
}