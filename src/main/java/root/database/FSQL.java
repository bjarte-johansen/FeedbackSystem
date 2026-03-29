package root.database;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

import static root.common.utils.Preconditions.checkArgument;


public class FSQL {
    public static final boolean DEBUG_SQL = true;


    // make it easier to create array of objects (fex for conditions and other purposes)
    public static Object[] makeArr(Object... args) {
        return args == null ? new Object[0] : args;
    }
    //static String[] strarr(String... args) { return args; }

    // make linked hash map from array of objects (fex for key,value,key,value)
    @SuppressWarnings("unchecked")
    private static <K, V> LinkedHashMap<K, V> makeOrderedMap(Object... args) {
        LinkedHashMap<K, V> m = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i += 2)
            m.put((K) args[i], (V) args[i + 1]);
        return m;
    }

    // make it easier to create ordered map of column names and values (fex for insert/update)
    public static LinkedHashMap<String, Object> linkedNameValueMap(Object... args) {
        return FSQL.<String, Object>makeOrderedMap(args);
    }

    /*
    static String join(String delim, String... values) {
        values = (values == null) ? new String[0] : values;
        return String.join(delim, values);
    }
     */


    /*****************************************************/
    /* statement binding and execution logic             */
    /*****************************************************/
    public static void bindNull(PreparedStatement ps, int index) throws SQLException {
        ps.setNull(index, java.sql.Types.NULL);
    }

    public static void bind(PreparedStatement ps, int index, Object v) throws SQLException {
        switch (v) {
            case Long l -> ps.setLong(index, l);
            case Integer i -> ps.setInt(index, i);
            case String s -> ps.setString(index, s);
            case Boolean b -> ps.setBoolean(index, b);
            case Double d -> ps.setDouble(index, d);
            case Instant t -> ps.setTimestamp(index, java.sql.Timestamp.from(t));
            default -> ps.setObject(index, v);
        }
    }


    public static void bind(PreparedStatement ps, LinkedHashMap<String, Object> map) throws SQLException {
        int i = 1;
        for (Object v : map.values()) {
            bind(ps, i++, v);
        }
    }

    public static void bind(PreparedStatement ps, Object[] values, int step) throws SQLException {
        checkArgument(step > 0, "Step must be positive");

        int param = 1;
        for (int i = 0; i < values.length; i += step) {
            bind(ps, param++, values[i]);
        }
    }


    /*****************************************************/
    /*S QL clause building logic                        */
    /*****************************************************/


//    /**
//     *
//     * Utility method to embed a string within specified "before" and "after" strings.
//     * @param s The string to be embedded.
//     * @param before The string to be placed before the input string.
//     * @param after The string to be placed after the input string.
//     * @return A new string consisting of the "before" string, followed by the input string, followed by the "after" string.
//     *
//     */

    /*
    public static String embed(String s, String before, String after) { return before + s + after; }
    public static String embedParens(String s) { return embed(s, "(", ")"); }
    public static String embedBrackets(String s) { return embed(s, "[", "]"); }
    public static String embedQuotes(String s, String quote) { return embed(s, quote, quote); }
    public static String embedCurly(String s) { return embed(s, "{", "}"); }
    public static String embedAngle(String s) { return embed(s, "<", ">"); }
     */


    /**
     * Utility method to repeat a string with a delimiter for a specified count.
     *
     * @param str The string to be repeated.
     * @param delimiter The delimiter to be used between repetitions.
     * @param count The number of times to repeat the string.
     * @return A single string consisting of the repeated string separated by the delimiter.
     */

    public static String repeatJoined(String str, String delimiter, int count) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < count; i++) {
            if (i > 0) sb.append(delimiter);
            sb.append(str);
        }

        return sb.toString();
    }


    // === Identifier quoting logic ===

    /**
     * Quotes an SQL identifier (such as a table or column name) using the appropriate quoting character for the
     * database, as determined by the connection's metadata.
     *
     * @param identifier The SQL identifier to be quoted (e.g., a table name or column name).
     * @return The quoted SQL identifier, wrapped in the appropriate quoting characters for the database.
     */

    static String quoteIdentifier(String identifier) throws SQLException {
        String q = Bugs.getQuoteSymbol();
        return q + identifier + q;
    }


    /**
     * Quotes a list of SQL identifiers (such as table or column names) using the appropriate quoting character for the
     * database, as determined by the connection's metadata.
     *
     * @param identifiers A list of SQL identifiers to be quoted (e.g., table names or column names).
     * @return A new list of quoted SQL identifiers, where each identifier is wrapped in the appropriate quoting
     * characters for the database.
     */

    static List<String> quotedIdentifiers(List<String> identifiers) throws SQLException {
        int n = identifiers.size();
        String[] arr = new String[n];

        for (int i = 0; i < n; i++) {
            arr[i] = quoteIdentifier(identifiers.get(i));
        }

        return Arrays.asList(arr);
    }


    /**
     * Quotes an array of SQL identifiers (such as table or column names) using the appropriate quoting character for the
     * database, as determined by the connection's metadata.
     *
     * Works in-place, modifying the input array of identifiers to contain the quoted versions.
     *
     * @param identifiers
     * @return
     */

    static String[] quotedIdentifiers(String[] identifiers) throws SQLException {
        int n = identifiers.length;
        String[] arr = new String[n];

        for (int i = 0; i < n; i++) {
            arr[i] = quoteIdentifier(identifiers[i]);
        }

        return arr;
    }

    /*
    static void quoteIdentifiers(String[] identifiers) throws SQLException {
        int n = identifiers.length;

        for (int i = 0; i < n; i++) {
            identifiers[i] = quoteIdentifier(identifiers[i]);
        }
    }

    static void quoteIdentifiers(List<String> input, String[] output) throws SQLException {
        int n = input.size();
        if(output == null || output.length < n)
            throw new IllegalArgumentException("Output array must be non-null and have length at least equal to input size");

        for (int i = 0; i < n; i++) {
            output[i] = quoteIdentifier(input.get(i));
        }
    }
    */


//    /**
//     * Utility method to apply a mapping function to each element of a string array and return a new array with the
//     * mapped values.
//     *
//     * @param input The input array of strings to be mapped.
//     * @param mapper A function that takes a string as input and returns a mapped string as output. This function will
//     * be applied to each element of the input array.
//     * @return A new array of strings where each element is the result of applying the mapper function to the
//     * corresponding element of the input array.
//     */
//
//    @SuppressWarnings("unchecked")
//    static <T> T[] mapArray(T[] input, Function<? super T, ? extends T> mapper) {
//        var output = (T[]) java.lang.reflect.Array.newInstance( input.getClass().getComponentType(), input.length);;
//        for (int i = 0; i < input.length; i++) {
//            output[i] = mapper.apply(input[i]);
//        }
//        return output;
//    }


    // === Core SQL building and execution logic ===

    // build where clause for queries


    // build set clause for update


    //

    /*
    static int table_delete_where(Connection conn, String tableName, Object[] cond) throws Exception {
        checkSafeTableName(tableName);

        return FSQLQuery.create(conn, "DELETE FROM " + tableName + build_where_clause(cond))
            .bindArray(cond)
            .delete();
    }

    static int table_update_where(Connection conn, String tableName, LinkedHashMap<String, Object> data, Object[] cond) throws Exception {
        String sql = createUpdateSql(conn, tableName, data, cond);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ArgumentBinder binder = new ArgumentBinder(ps);
            binder.bind(data.values());
            binder.bindOdd(cond);

            if(DEBUG_SQL) Logger.info(ps.toString());

            return ps.executeUpdate();
        }
    }

    static int table_insert_one(Connection conn, String tableName, LinkedHashMap<String, Object> data) throws SQLException, Exception {
        String sql = createInsertSql(conn, tableName, data);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ArgumentBinder binder = new ArgumentBinder(ps);
            binder.bind(data.values());

            if(DEBUG_SQL) Logger.info(ps.toString());

            return ps.executeUpdate();
        }
    }
    */
}
