package root.database;

import root.logger.Logger;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static root.common.utils.Preconditions.checkArgument;
import static root.common.utils.Preconditions.checkNotNull;


public class DataSource {
    static class PooledConnection {
        static Connection wrap(Connection real) {
            return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> {

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
                });
        }
    }

    private static final AtomicInteger TOTAL_CONNECTION_COUNT = new AtomicInteger(0);
    private static int MAX_TOTAL_CONNECTION_COUNT = 10;

    private static final boolean LOG_POOL_CHANGES_TO_CONSOLE = false;

    public static final DataSourceConnectionParams TEST = new DataSourceConnectionParams(
        "jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905?currentSchema=test","h184905", "pass"
    );
    public static final DataSourceConnectionParams PROD = new DataSourceConnectionParams(
        "jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905?currentSchema=public","h184905", "pass"
    );

    private static DataSourceConnectionParams currentDataSourceParams = TEST;

    private static final BlockingQueue<Connection> pool = new ArrayBlockingQueue<>(MAX_TOTAL_CONNECTION_COUNT);

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
        return pool.size();
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
            Connection conn = DriverManager.getConnection(params.url, params.username, params.password);

            logMessage("Connection Created, available connections: " + getAvailableCount() + "/" + getTotalCount());

            return conn;
        }catch(SQLException e) {
            TOTAL_CONNECTION_COUNT.decrementAndGet();
            throw e;
        }
    }

    private static Connection dequeueConnection(DataSourceConnectionParams params) throws SQLException {
        Connection conn = pool.poll();

        if(conn == null) {
            conn = createConnection(params);
        }

        logMessage("Connection <<-- Pool, available connections: " + getAvailableCount() + "/" + getTotalCount());

        return PooledConnection.wrap(conn);
    }

    private static void enqueueConnection(Connection conn) throws SQLException {
        conn = conn.unwrap(Connection.class);
        pool.add(conn);  // return to pool

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

    public static Connection getConnection(DataSourceConnectionParams params) throws SQLException {
        return dequeueConnection(params);
    }
}
