package root.database;

import java.sql.SQLException;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static root.database.TableNameSanitizer.checkSafeTableName;

public class SqlFactory {

    public static String createWhereClauseSqlArgs(Object[] cond) {
        checkArgument(cond != null && (cond.length & 1) == 0, "Condition array must be non-null and have an even number of elements (expression-value pairs)");

        if (cond.length == 0) return "";

        StringJoiner joiner = new StringJoiner(" AND ");

        for (int i = 0; i < cond.length; i += 2) {
            joiner.add("(" + cond[i] + " ?)");
        }

        return joiner.toString();
    }

    public static String createWhereClauseSql(Object[] cond) {
        String tmp = createWhereClauseSqlArgs(cond);

        return tmp.isEmpty() ? "" : " WHERE (" + tmp + ")";
    }


    //

    public static String createSetClauseSqlArgs(Iterable<String> columnNames) {
        if (columnNames == null)
            return "";

        StringJoiner joiner = new StringJoiner(", ");

        for (String column : columnNames) {
            joiner.add(column + " = ?");
        }

        return (joiner.length() == 0) ? "" : joiner.toString();
    }

    public static String createSetClauseSql(Iterable<String> columnNames) {
        String tmp = createSetClauseSqlArgs(columnNames);

        return tmp.isEmpty() ? "" : " SET " + tmp;
    }

    public static String createSetClauseSqlArgs(String[] columnNames) {
        return createSetClauseSql(columnNames == null ? Collections.emptyList() : Arrays.asList(columnNames));
    }

    public static String createSetClauseSql(LinkedHashMap<String, Object> columnNameValueMap) {
        return createSetClauseSql(columnNameValueMap == null ? Collections.emptyList() : columnNameValueMap.keySet());
    }


    /**
     * Creates a columnlist  string for inserting a new row into a table with specified column values.
     *
     * @param data A collection of column names where the order of columns is preserved. Must be non-null and non-empty
     * @return A string for inserting a new row into the specified table with the given column values.
     * @throws SQLException If an error occurs while accessing the database metadata for quoting identifiers.
     */

    public static String createInsertColumnListSql(Collection<String> data) throws SQLException {
        checkArgument(data != null && !data.isEmpty(), "Number of columns must be greater than zero");
        //if(data == null || data.isEmpty()) throw new IllegalArgumentException("Number of columns must be greater than zero");

        Iterator<String> it = data.iterator();
        StringBuilder sb = new StringBuilder(data.size() * 16);

        sb.append(" (").append(FSQL.quoteIdentifier(it.next()));

        while (it.hasNext()) {
            sb.append(", ").append(FSQL.quoteIdentifier(it.next()));
        }

        sb.append(")");
        return sb.toString();
    }

    public static String createInsertColumnListSql(LinkedHashMap<String, Object> data) throws SQLException {
        // TODO: can use null instead of emptyList
        return createInsertColumnListSql(data == null ? Collections.emptyList() : data.keySet());
    }

    public static String createInsertColumnListSql(String[] columnNames) throws SQLException {
        // TODO: can use null instead of emptyList
        return createInsertColumnListSql(columnNames == null ? Collections.emptyList() : Arrays.asList(columnNames));
    }



    /**
     * Creates a placeholders string for inserting a new row into a table with specified column values. The number of
     * placeholders will match the provided number of columns.
     *
     * @param numColumns The number of columns for which to create placeholders. Must be greater than zero.
     * @return A string of placeholders for inserting a new row into the specified table with given number of columns.
     */

    public static String createParenPlaceholdersSql(int numColumns) {
        checkArgument(numColumns > 0, "Number of columns must be greater than zero");

        return " (" + FSQL.repeatJoined("?", ", ", numColumns) + ")";
    }

    /**
     * @see #createParenPlaceholdersSql(int)
     */
/*
    private static <T extends Collection<?>> requireNonEmpty(Collection<?> c, String msg) {
        if(c == null || c.isEmpty()) {
            throw new IllegalArgumentException(msg);
        }
    }
 */
/*
    public static String createParenPlaceholdersSql(Map<String, Object> data) {
        checkArgument( data != null && !data.isEmpty(), "Data must not be empty");

        return createParenPlaceholdersSql( data.size() );
    }

 */



    /**
     * Creates a SQL query string for counting rows in a table without any conditions.
     *
     * @param tableName The name of the table to count rows from.
     * @return A SQL query string for counting rows in the specified table without any conditions.
     */

    public static String createCountSql(String tableName) {
        return createCountSql(tableName, null);
    }

    /**
     * Creates a SQL query string for counting rows in a table, optionally with conditions.
     *
     * @param tableName The name of the table to count rows from.
     * @param cond An optional array of conditions for the WHERE clause, where even indices are expressions and odd
     * indices are values.
     * @return A SQL query string for counting rows in the specified table with the given conditions.
     */

    public static String createCountSql(String tableName, Object[] cond) {
        checkSafeTableName(tableName);

        return "SELECT COUNT(*) FROM " + tableName + createWhereClauseSql(cond);
    }



    /**
     * @see #createExistsSql(String, Object[])
     */

    public static String createExistsSql(String tableName) {
        return createExistsSql(tableName, null);
    }

    /**
     * Creates a SQL query string for checking the existence of rows in a table, optionally with conditions.
     *
     * @param tableName The name of the table to check for row existence.
     * @param cond An optional array of conditions for the WHERE clause, where even indices are expressions and odd
     * indices are values.
     * @return A SQL query string for checking the existence of rows in the specified table with the given conditions.
     */

    public static String createExistsSql(String tableName, Object[] cond) {
        checkSafeTableName(tableName);

        return "SELECT EXISTS (SELECT 1 FROM " + tableName + createWhereClauseSql(cond) + ")";
    }



    /**
     * Creates a SQL query string for updating rows in a table, optionally with conditions.
     *
     * @param tableName The name of the table to update rows in.
     * @param data A LinkedHashMap of column names and their corresponding values to be updated, where the order of
     * entries is preserved.
     * @param cond An optional array of conditions for the WHERE clause, where even indices are expressions and odd
     * indices are values.
     * @return A SQL query string for updating rows in the specified table with the given update values and conditions.
     * @throws SQLException If an error occurs while accessing the database metadata for quoting identifiers.
     */

    public static String createUpdateSql(String tableName, LinkedHashMap<String, Object> data, Object[] cond) throws SQLException {
        checkSafeTableName(tableName);

        return "UPDATE " + tableName + createSetClauseSql(data) + createWhereClauseSql(cond);
    }



    public static String createInsertSql(String tableName, List<String> columnNames) throws SQLException {
        checkSafeTableName(tableName);

        if (columnNames == null || columnNames.size() == 0) {
            return "INSERT INTO " + tableName + " DEFAULT VALUES";
        }

        return "INSERT INTO " + tableName +
            createInsertColumnListSql(columnNames)
            + " VALUES "
            + createParenPlaceholdersSql(columnNames.size());
    }



    /**
     * Creates a SQL query string for inserting a new row into a table with specified column values.
     *
     * @param tableName The name of the table to insert a new row into.
     * @param data A LinkedHashMap of column names and their corresponding values to be inserted, where the order of
     * entries is preserved.
     * @return A SQL query string for inserting a new row into the specified table with the given column values.
     * @throws SQLException If an error occurs while accessing the database metadata for quoting identifiers.
     */

    public static String createInsertSql(String tableName, LinkedHashMap<String, Object> data) throws SQLException {
        checkSafeTableName(tableName);

        if (data.isEmpty()) {
            return "INSERT INTO " + tableName + " DEFAULT VALUES";
        }

        return "INSERT INTO " + tableName +
            createInsertColumnListSql(data)
            + " VALUES "
            + createParenPlaceholdersSql(data.size());
    }


    /**
     *
     * @param tableName
     * @param columnNames
     * @return
     * @throws SQLException
     */

    public static String createInsertSql(String tableName, String[] columnNames) throws SQLException {
        checkSafeTableName(tableName);

        if (columnNames == null || columnNames.length == 0) {
            return "INSERT INTO " + tableName + " DEFAULT VALUES";
        }

        return "INSERT INTO " + tableName
            + createInsertColumnListSql(columnNames)
            + " VALUES "
            + createParenPlaceholdersSql(columnNames.length);
    }



    /**
     * @see #createDeleteSql(String, Object[])
     */

    public static String createDeleteSql(String tableName) {
        return createDeleteSql(tableName, null);
    }


    /**
     * Creates a SQL query string for deleting rows from a table, optionally with conditions.
     *
     * @param tableName The name of the table to delete rows from.
     * @param cond An compacted array of conditions for the WHERE clause, ["field op", placeholder, "field op",
     * placeholder, ...], where every indices are expressions and odd indices are values.
     * @return A SQL query string for deleting rows from the specified table with the given conditions.
     */
    public static String createDeleteSql(String tableName, Object[] cond) {
        checkSafeTableName(tableName);

        return "DELETE FROM " + tableName + createWhereClauseSql(cond);
    }





    /**
     * @see #createSelectSql(String, String[], Object[])
     */

    public static String createSelectSql(String tableName, String[] columns) throws SQLException {
        return createSelectSql(tableName, columns, null);
    }

    /**
     * Creates a SQL query string for selecting rows from a table with specified columns and optional conditions.
     *
     * @param tableName The name of the table to select rows from.
     * @param columns An array of column names to be selected. If null or empty, all columns will be selected.
     * @param cond An optional array of conditions for the WHERE clause, where even indices are expressions and odd are values/placeholders.
     * @return A SQL query string for selecting rows from the specified table with the given columns and conditions.
     * @throws SQLException If an error occurs while accessing the database metadata for quoting identifiers.
     */

    public static String createSelectSql(String tableName, String[] columns, Object[] cond) throws SQLException {
        checkSafeTableName(tableName);

        String columnList = (columns == null || columns.length == 0)
            ? "*"
            : String.join(", ", FSQL.quotedIdentifiers(columns));

        return "SELECT " + columnList + " FROM " + tableName + createWhereClauseSql(cond);
    }
}