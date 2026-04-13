package root.database;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.regex.Matcher;

public class FSQLQueryInterpolator {

    /**
     * Interpolates the given SQL query by replacing each '?' placeholder with the corresponding argument value.
     * @param sql The SQL query containing '?' placeholders.
     * @param args The arguments to replace the placeholders. Each argument will be converted to a string representation suitable for SQL.
     * @return The interpolated SQL query with all placeholders replaced by their corresponding argument values.
     */
    static public String interpolate(String sql, Object... args) {
        args = (args == null) ? new Object[0] : args;
        for (Object arg : args) {
            String value;
            if (arg == null) {
                value = "NULL";
            } else if (arg instanceof String || arg instanceof Character) {
                value = "'" + arg.toString().replace("'", "''") + "'";
            } else if (arg instanceof Boolean) {
                value = ((Boolean) arg) ? "TRUE" : "FALSE";
            } else if (arg instanceof Instant) {
                value = "'" + Timestamp.from((Instant) arg).toString() + "'";
            } else {
                value = arg.toString();
            }
            //sql = sql.replaceFirst("\\?", value);
            sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(value));
        }
        return sql;

    }
}
