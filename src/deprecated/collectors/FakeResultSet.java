package root.database.collectors;

import java.lang.reflect.*;
import java.sql.*;

public class FakeResultSet {

    public static ResultSet empty() {
        return (ResultSet) Proxy.newProxyInstance(
            ResultSet.class.getClassLoader(),
            new Class[]{ResultSet.class},
            new Handler()
        );
    }

    static class Handler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            if (method.getName().equals("next")) return false;
            return defaultValue(method.getReturnType());
        }
    }

    static Object defaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        return null;
    }
}