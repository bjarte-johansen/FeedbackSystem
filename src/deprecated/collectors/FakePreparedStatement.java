package root.database.collectors;

import root.database.QueryLogger;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

public class FakePreparedStatement {

    public static PreparedStatement create(String sql) {
        return (PreparedStatement) Proxy.newProxyInstance(
            PreparedStatement.class.getClassLoader(),
            new Class[]{PreparedStatement.class},
            new Handler(sql)
        );
    }

    static class Handler implements InvocationHandler {

        private final String sql;
        private final Map<Integer, Object> binds = new HashMap<>();

        Handler(String sql) {
            this.sql = sql;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            String name = method.getName();

            // bind parameters
            if (name.startsWith("set") && args.length >= 2) {
                int idx = (int) args[0];
                Object val = args[1];
                binds.put(idx, val);
                return null;
            }

            if (name.equals("executeUpdate")) {
                System.out.println(buildSql());
                return 1;
            }

            if (name.equals("executeQuery")) {
                System.out.println(buildSql());
                return FakeResultSet.empty();
            }

            if (name.equals("close")) return null;

            return defaultValue(method.getReturnType());
        }

        private String buildSql() {
            String result = sql;
            for (Map.Entry<Integer, Object> e : binds.entrySet()) {
                Object v = e.getValue();
                String s = (v == null) ? "NULL"
                    : (v instanceof String ? "'" + v + "'" : v.toString());
                result = result.replaceFirst("\\?", s);
            }
            return result;
        }

        public String toString(){
            return buildSql();
        }
    }

    static Object defaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        return null;
    }
}