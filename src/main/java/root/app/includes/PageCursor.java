package root.app.includes;


/**
 * The PageCursor class is a utility for managing pagination in applications, allowing for easy navigation through
 * paginated results by maintaining an offset and limit. It provides methods to get the next and previous pages, as well
 * as to build SQL fragments for LIMIT and OFFSET based on its current state.
 */

public class PageCursor {
    private int offset;
    private int limit;

    /**
     * Initializes a new PageCursor with default values of offset 0 and limit Integer.MAX_VALUE, allowing for pagination
     * of results starting from the beginning with no upper limit on the number of results returned.
     */
    public PageCursor() {
        this(0, Integer.MAX_VALUE);
    }

    /**
     * Initializes a new PageCursor with the specified offset and limit values, allowing for pagination of results
     * starting from the specified offset and returning up to the specified limit of results.
     *
     * @param offset
     * @param limit
     */
    public PageCursor(int offset, int limit) {
        this.setOffset(offset);
        this.setLimit(limit);
    }

    /**
     * Returns the current offset value, which indicates the starting point for pagination of results.
     *
     * @return The current offset value.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets the offset value, which indicates the starting point for pagination of results.
     *
     * @param offset The new offset value to be set.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Returns the current limit value, which indicates the maximum number of results to be returned for pagination.
     *
     * @return The current limit value.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Sets the limit for the number of results to be returned.
     *
     * @param limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }


    /**
     * Returns a new PageCursor representing the next page of results by increasing the offset by the limit.
     *
     * @return A new PageCursor with the offset increased by the limit.
     */
    public PageCursor next() {
        return new PageCursor(this.offset + this.limit, this.limit);
    }

    /**
     * Returns a new PageCursor representing the next page of results, ensuring that the offset does not exceed the
     * specified maximum offset.
     *
     * @param maxOffset The maximum offset to ensure that the next page does not go beyond this limit.
     * @return A new PageCursor with the offset increased by the limit, but not exceeding maxOffset.
     */

    public PageCursor next(int maxOffset) {
        return new PageCursor(Math.min(maxOffset, this.offset + this.limit), this.limit);
    }

    /**
     * Returns a new PageCursor representing the previous page of results, ensuring that the offset does not go below
     * zero.
     *
     * @return A new PageCursor with the offset decreased by the limit, but not less than zero.
     */
    public PageCursor previous() {
        int newOffset = Math.max(0, this.offset - this.limit);
        return new PageCursor(newOffset, this.limit);
    }


    /**
     * Builds a SQL fragment for LIMIT and OFFSET based on the current state of the PageCursor.
     *
     * @return A string containing the SQL fragment for LIMIT and OFFSET, which can be used in SQL queries.
     */
    public String buildLimitOffsetSql() {
        String s = "";
        if (limit > 0) {
            s += "LIMIT " + this.limit;
        }
        if (offset > 0) {
            if (!s.isEmpty()) s += " ";
            s += "OFFSET " + this.offset;
        }
        return s;
    }

    public static PageCursor decode(String cursorStr) {
        return PageCursorEncoder.decodeCursor(cursorStr);
    }
    public static String encode(PageCursor cursor) {
        return PageCursorEncoder.encodeCursor(cursor);
    }

    /**
     * Returns a string representation of the PageCursor, including its offset and limit values.
     *
     * @return
     */
    @Override
    public String toString() {
        return "PageCursor{" +
            "offset=" + offset +
            ", limit=" + limit +
            '}';
    }
}