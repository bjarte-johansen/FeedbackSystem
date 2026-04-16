package root.database.collectors;

import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FakeConnection {
    public static Connection create() {
        return (Connection) Proxy.newProxyInstance(
            Connection.class.getClassLoader(),
            new Class[]{Connection.class},
            new Handler()
        );
    }

    static class Handler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            switch (method.getName()) {
                case "prepareStatement":
                    String sql = (String) args[0];
                    return FakePreparedStatement.create(sql);
                case "close":
                    return null;
                case "isClosed":
                    return false;
                default:
                    return defaultValue(method.getReturnType());
            }
        }
    }

    static Object defaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        return null;
    }
}