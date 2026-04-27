package root.includes;

public class PageCursorSqlBuilder {
    /**
     * Builds a SQL fragment for LIMIT and OFFSET based on the current state of the PageCursor.
     *
     * @return A string containing the SQL fragment for LIMIT and OFFSET, which can be used in SQL queries.
     */
    public static String build(PageCursor cursor) {
        String s = "";

        if (cursor.getLimit() > 0) {
            s += "LIMIT " + cursor.getLimit();
        }

        if (cursor.getOffset() > 0) {
            if (!s.isEmpty()) s += " ";
            s += "OFFSET " + cursor.getOffset();
        }

        return s;
    }
}
