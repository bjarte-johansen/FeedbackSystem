package root.database;

import root.logger.Logger;

import javax.xml.transform.Result;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

public class FSQL {
    public static final boolean DEBUG_SQL = true;


    // make it easier to create array of objects (fex for conditions and other purposes)
    static Object[] makeArr(Object... args) {
        return args == null ? new Object[0] : args;
    }
    //static String[] strarr(String... args) { return args; }

    // make linked hash map from array of objects (fex for key,value,key,value)
    static <K,V> LinkedHashMap<K,V> makeOrderedMap(Object... args) {
        LinkedHashMap<K,V> m = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i += 2)
            m.put((K) args[i], (V) args[i+1]);
        return m;
    }

    // make it easier to create ordered map of column names and values (fex for insert/update)
    static LinkedHashMap<String, Object> linkedMap(Object... args) {
        return FSQL.<String, Object>makeOrderedMap(args);
    }

    // make it easier to create ordered map of column names and values (fex for insert/update)
    public static LinkedHashMap<String, Object> linkedNameValueMap(Object... args) {
        return FSQL.<String, Object>makeOrderedMap(args);
    }

    /*
    static String join(String delim, String... values) {
        values = values == null ? new String[0] : values;
        return String.join(delim, values);
    }
     */



    /*****************************************************/
    /* statement binding and execution logic             */
    /*****************************************************/

    static void bind(PreparedStatement ps, int index, Object v) throws SQLException {
        switch (v) {
            case Long l       -> ps.setLong(index, l);
            case Integer i    -> ps.setInt(index, i);
            case String s     -> ps.setString(index, s);
            case Boolean b    -> ps.setBoolean(index, b);
            case Double d     -> ps.setDouble(index, d);
            case Instant t    -> ps.setTimestamp(index, java.sql.Timestamp.from(t));
            default           -> ps.setObject(index, v);
        }
    }


    static void bind(PreparedStatement ps, LinkedHashMap<String, Object> map) throws SQLException {
        int i = 1;
        for (Object v : map.values()) {
            bind(ps, i++, v);
        }
    }


    /*****************************************************/
    /*S QL clause building logic                        */
    /*****************************************************/

/*
    static void set(Field f, Object o, Object v) throws IllegalAccessException {
        Class<?> t = f.getType();

        if (v == null) {
            if (!t.isPrimitive()) f.set(o, null);
            return;
        }

        if (t == int.class)             f.setInt(o, ((Number) v).intValue());
        else if (t == long.class)       f.setLong(o, ((Number) v).longValue());
        else if (t == double.class)     f.setDouble(o, ((Number) v).doubleValue());
        else if (t == boolean.class)    f.setBoolean(o, (Boolean) v);
        else if (t == float.class)      f.setFloat(o, ((Number) v).floatValue());
        else if (t == short.class)      f.setShort(o, ((Number) v).shortValue());
        else if (t == byte.class)       f.setByte(o, ((Number) v).byteValue());
        else if (t == char.class)       f.setChar(o, (Character) v);
        else f.set(o, v);
    }

    static Object read(ResultSet rs, int i, Class<?> t) throws SQLException {

        // hottest first
        if (t == String.class)        return rs.getString(i);
        if (t == int.class)           return rs.getInt(i);
        if (t == long.class)          return rs.getLong(i);
        if (t == double.class)        return rs.getDouble(i);
        if (t == boolean.class)       return rs.getBoolean(i);

        // boxed
        if (t == Integer.class)       return rs.getInt(i);
        if (t == Long.class)          return rs.getLong(i);
        if (t == Double.class)        return rs.getDouble(i);
        if (t == Boolean.class)       return rs.getBoolean(i);

        // time (less frequent)
        if (t == java.time.Instant.class)
            return rs.getObject(i, java.sql.Timestamp.class).toInstant();

        if (t == java.time.LocalDate.class)
            return rs.getObject(i, java.time.LocalDate.class);

        if (t == java.time.LocalTime.class)
            return rs.getObject(i, java.time.LocalTime.class);

        // fallback
        return rs.getObject(i);
    }
*/


    /*****************************************************/
    /* Core SQL building and execution logic             */
    /*****************************************************/

    // ChatGPT rewrote our code into this and we modified it. The core logic is from older project + suggestions
    // from chatgpt.
    static String build_where_clause_args(Object[] cond) {
        StringJoiner joiner = new StringJoiner(" AND ");

        int n = cond.length >>> 1;
        for (int i = 0; i < n; i+=2) {
            String expr = String.valueOf(cond[i]);
            joiner.add("(" + expr + " ?" + ")");
        }

        return joiner.toString();
    }

    static String build_where_clause(Object[] cond) {
        String strArgs = build_where_clause_args(cond);

        return strArgs.isEmpty() ? " " : " WHERE (" + strArgs + ") ";
    }


    static String build_set_clause_args(LinkedHashMap<String, Object> updateValues) {
        StringJoiner joiner = new StringJoiner(", ");

        for (String column : updateValues.keySet()) {
            joiner.add(column + " = ?");
        }

        return joiner.toString(); // return number of parameters
    }

    static String repeatWithSeparator(String str, String delimiter, int count) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (int i = 0; i < count; i++) {
            joiner.add(str);
        }
        return joiner.toString();
    }



    public static String createUpdateSql(Connection conn, String tableName, LinkedHashMap<String, Object> updateValues, Object[] cond) throws SQLException {
        // build sql
        StringBuilder sql = new StringBuilder(512)
            .append("UPDATE ")
            .append(tableName)
            .append(" SET ")
            .append(build_set_clause_args(updateValues))
            .append(cond.length > 0 ? build_where_clause(cond) : "");

        return sql.toString();
    }

    public static String createInsertSql(Connection conn, String tableName, LinkedHashMap<String, Object> valueColumns) throws SQLException{
        Set<String> valueColumnNameSet = valueColumns.keySet();
        List<String> columnNameList = valueColumnNameSet.stream().map(k -> quoteIdentifier(conn, k)).toList();

        // build sql
        StringBuilder sql = new StringBuilder(512)
            .append("INSERT INTO ")
            .append(tableName)
            .append(" (")
            .append(String.join(", ", columnNameList))
            .append(") VALUES (")
            .append(String.join(", ", java.util.Collections.nCopies((int) valueColumnNameSet.size(), "?")))
            .append(")");

        return sql.toString();
    }

    static String quoteIdentifier(Connection conn, String identifier) {
        try {
            String quote = conn.getMetaData().getIdentifierQuoteString();
            return quote + identifier + quote;
        }catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //

    static int table_delete_where(Connection conn, String tableName, Object[] cond) throws Exception {
        return FSQLQuery.create(conn, "DELETE FROM " + tableName + " WHERE tenant_id = ?")
            .bind(cond)
            .delete();
    }

    static int table_update_where(Connection conn, String tableName, LinkedHashMap<String, Object> updateValues, Object[] cond) throws Exception {
        StringBuilder sql = new StringBuilder(512);
        sql.append(createUpdateSql(conn, tableName, updateValues, cond));

        // count conditions
        int conditionCount = cond.length >>> 1;

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            // bind set values
            int i = 1;
            for (var e : updateValues.values()) {
                bind(ps, i++, e);
            }

            // bind where values
            for (int j = 0; j <= conditionCount; j += 2) {
                bind(ps, i++, cond[j + 1]);
            }

            if(DEBUG_SQL) Logger.info(ps.toString());

            return ps.executeUpdate();
        }
    }

    static int table_insert_one(Connection conn, String tableName, LinkedHashMap<String, Object> where) throws SQLException, Exception {
        String sql = createInsertSql(conn, tableName, where);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, where);

            if(DEBUG_SQL) Logger.info(ps.toString());

            return ps.executeUpdate();
        }
    }
}
