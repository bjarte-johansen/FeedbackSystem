package root.utils;

/*
public class RangeQueryFilterBuilder {
    private final String columnName;
    private final Long start;
    private final Long end;
    private final boolean startInclusive;
    private final boolean endInclusive;

    public RangeQueryFilterBuilder(String columnName, Long start, Long end, boolean startInclusive, boolean endInclusive) {
        this.start = start;
        this.end = end;
        this.startInclusive = startInclusive;
        this.endInclusive = endInclusive;
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public Long getFromValue() {
        return start;
    }

    public Long getToValue() {
        return end;
    }

    boolean isEmpty() {
        return start == null && end == null;
    }

    public String toString(){
        return buildSqlQueryString(columnName, start, end, startInclusive, endInclusive);
    }
*/

/**
 * Utility class for building SQL query strings for filtering numeric columns based on range parameters. The class
 * provides methods to construct SQL conditions for both inclusive and exclusive range filters. It also handles cases
 * where the start or end values are null, allowing for open-ended ranges.
 */
public class RangeQueryFilterBuilder {

    /**
     * Builds a SQL query string for filtering a numeric column based on the provided range parameters.
     *
     * @param columnName The name of the column to filter on.
     * @param start A null value indicates no lower bound, while a non-null value specifies the lower limit of the
     * range.
     * @param end A null value indicates no upper bound, while a non-null value specifies the upper limit of the range.
     * @param startInclusive true if the start value should be included in the range, false if it should be exclusive
     * @param endInclusive true if the end value should be included in the range, false if it should be exclusive
     * @return A SQL query string representing the range filter for the specified column, or a default value if both
     * start and end are null.
     */

    public static String buildSqlQueryString(String columnName, Long start, Long end, boolean startInclusive, boolean endInclusive) {
        return buildSqlQueryString(columnName, start, end, startInclusive, endInclusive, "(1=1)"); // default to no filter
    }


    /**
     * Builds a SQL query string for filtering a numeric column based on the provided range parameters.
     *
     * @param columnName The name of the column to filter on.
     * @param start A null value indicates no lower bound, while a non-null value specifies the lower limit of the
     * range.
     * @param end A null value indicates no upper bound, while a non-null value specifies the upper limit of the range.
     * @param startInclusive true if the start value should be included in the range, false if it should be exclusive
     * @param endInclusive true if the end value should be included in the range, false if it should be exclusive
     * @param defaultEmptyValue The default SQL string to return if both start and end are null, indicating no
     * filtering. This allows the caller to specify a custom default value instead of the default "(1=1)".
     * @return A SQL query string representing the range filter for the specified column, or the provided default value if both
     * start and end are null.
     */

    public static String buildSqlQueryString(String columnName, Object start, Object end, boolean startInclusive, boolean endInclusive, String defaultEmptyValue) {
        class NumberToStringHelper {
            private static String toString(Object value) {
                if (!(value instanceof Number)) {
                    throw new IllegalArgumentException("Value must be of numeric type");
                }
                if (value instanceof Double || value instanceof Float)
                    return Double.toString(((Number) value).doubleValue());

                return Long.toString(((Number) value).longValue());
            }
        }

        String startOpStr = startInclusive ? " >= " : " > ";
        String endOpStr = endInclusive ? " <= " : " < ";

        String startValStr = start == null ? null : NumberToStringHelper.toString(start);
        String endValStr = end == null ? null : NumberToStringHelper.toString(end);

        if (start != null && end != null) {
            return "((" + columnName + startOpStr + startValStr + ") AND (" + columnName + endOpStr + endValStr + "))";
        } else if (start != null) {
            return "(" + columnName + startOpStr + startValStr + ")";
        } else if (end != null) {
            return "(" + columnName + endOpStr + endValStr + ")";
        }

        return defaultEmptyValue;
    }
}
