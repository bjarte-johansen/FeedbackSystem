package root.database;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

class PooledConnection {
    static Connection wrap(Connection real, Queue<Connection> pool) {
        InvocationHandler h = (proxy, method, args) -> {

            if (method.getName().equals("close")) {
                pool.offer((Connection) proxy);  // return to pool
                return null;
            }

            return method.invoke(real, args);
        };

        return (Connection) Proxy.newProxyInstance(
            Connection.class.getClassLoader(),
            new Class[]{Connection.class},
            h
        );
    }
}

public class DataSource {
    private static final String CONN_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String CONN_USERNAME = "postgres";
    private static final String CONN_PASSWORD = "postgres";

    private static final Queue<Connection> pool = new ArrayDeque<>();

    public static Connection getConnection() throws SQLException {
        if(pool.isEmpty()) {
            return PooledConnection.wrap(DriverManager.getConnection(CONN_URL, CONN_USERNAME, CONN_PASSWORD), pool);
        }
        return pool.poll();
    }
}
