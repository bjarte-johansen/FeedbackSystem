package root.app;

import root.app.includes.PageCursor;
import root.models.Review;

public class ReviewQueryOptions {
    public static final int OPTION_ORDER_BY_ID_ASC = 1;
    public static final int OPTION_ORDER_BY_ID_DESC = 2;

    public static final int OPTION_ORDER_BY_CREATED_AT_ASC = 3;
    public static final int OPTION_ORDER_BY_CREATED_AT_DESC = 4;

    public static final int OPTION_ORDER_BY_SCORE_ASC = 8;
    public static final int OPTION_ORDER_BY_SCORE_DESC = 9;

    public static final int OPTION_ORDER_BY_PENDING_FIRST = 20;
    public static final int OPTION_ORDER_BY_APPROVED_FIRST = 21;
    public static final int OPTION_ORDER_BY_REJECTED_FIRST = 22;

    private PageCursor pageCursor;
    private int orderByEnum;
    private int statusEnum;
    private int filterScoreMin;
    private int filterScoreMax;

    /**
     * Default constructor for the ReviewQueryOptions class. Initializes a new instance of the ReviewQueryOptions class
     * with default values. The page cursor is initialized to a new instance of the PageCursor class, and the status
     * enum and order by enum are set to -1, indicating that they are not specified. This constructor allows for the
     * creation of a ReviewQueryOptions object with default settings, which can be modified later using the setter
     * methods provided in the class.
     */
    public ReviewQueryOptions() {
        this(new PageCursor(), -1, -1);
    }

    /**
     * Parameterized constructor for the ReviewQueryOptions class. Initializes a new instance of the ReviewQueryOptions
     * class with the specified page cursor, status enum, and order by enum values. This constructor allows for the
     * creation of a ReviewQueryOptions object with specific settings for pagination, filtering by review status, and
     * sorting order. The provided values are assigned to the corresponding properties of the class, allowing for
     * customized query options to be set when creating an instance of the ReviewQueryOptions class.
     *
     * @param pageCursor
     * @param statusEnum
     * @param orderByEnum
     */
    public ReviewQueryOptions(PageCursor pageCursor, Integer statusEnum, Integer orderByEnum) {
        this.pageCursor = pageCursor;
        this.statusEnum = statusEnum;
        this.orderByEnum = orderByEnum;
        this.filterScoreMin = -1;
        this.filterScoreMax = -1;
    }



    /*
    pagination
     */

    /**
     * Returns the page cursor associated with the review query options.
     *
     * @return
     */
    public PageCursor getPageCursor() {
        return pageCursor;
    }

    /**
     * Sets the page cursor for the review query options.
     *
     * @param pageCursor
     */
    public void setPageCursor(PageCursor pageCursor) {
        this.pageCursor = pageCursor;
    }



    /*
    order by
     */

    /**
     * Returns the order by enum value associated with the review query options. This value indicates the sorting order
     * for the reviews when querying the database, and it can be used to determine how the results should be ordered
     * based on different criteria such as review ID, creation date, or review status.
     *
     * @return
     */
    public int getOrderByEnum() {
        return orderByEnum;
    }

    /**
     * Sets the order by enum value for the review query options. This value determines the sorting order for the
     * reviews when querying the database. The method accepts an integer value that corresponds to a specific sorting
     * option, such as sorting by review ID in ascending or descending order, sorting by creation date, or sorting by
     * review status (pending, approved, rejected). By setting this value, developers can control how the review results
     * are ordered when retrieved from the database, allowing for customized sorting based on different criteria.
     *
     * @param orderByEnum
     */

    public void setOrderByEnum(int orderByEnum) {
        this.orderByEnum = orderByEnum;
    }



    /*
    filter by status
     */

    /**
     * Returns the status enum value associated with the review query options. This value indicates the review status
     * filter for the query, allowing developers to specify which reviews to include in the results based on their
     * status (e.g., approved, pending, rejected). The method returns an integer value that corresponds to a specific
     * review status or a combination of statuses, enabling flexible filtering of reviews when querying the database.
     *
     * @return
     */
    public int getStatusEnum() {
        return statusEnum;
    }

    /**
     * Sets the status enum value for the review query options. This value determines the review status filter for the
     * query, allowing developers to specify which reviews to include in the results based on their status (e.g.,
     * approved, pending, rejected). The method accepts an integer value that corresponds to a specific review status or
     * a combination of statuses, enabling flexible filtering of reviews when querying the database. By setting this
     * value, developers can control which reviews are included in the results based on their status, allowing for
     * customized filtering of reviews when retrieving data from the database.
     *
     * @param statusEnum
     */
    public void setStatusEnum(int statusEnum) {
        this.statusEnum = statusEnum;
    }



    /*
    filter by score range
     */


    /**
     * Returns the minimum score filter for the review query options. This value allows developers to specify a lower
     * limit for the review scores to be included in the query results.
     *
     * @return
     */
    public int getFilterScoreMin() {
        return filterScoreMin;
    }


    /**
     * Sets the minimum score filter for the review query options. This value allows developers to specify a lower limit
     * for the review scores to be included in the query results.
     *
     * @param filterScoreMin
     */
    public void setFilterScoreMin(int filterScoreMin) {
        this.filterScoreMin = filterScoreMin;
    }


    /**
     * Returns the maximum score filter for the review query options. This value allows developers to specify an upper
     * limit for the review scores to be included in the query results.
     *
     * @return
     */
    public int getFilterScoreMax() {
        return filterScoreMax;
    }


    /**
     * Sets the maximum score filter for the review query options. This value allows developers to specify an upper
     * limit for the review scores to be included in the query results.
     *
     * @param filterScoreMax
     */
    public void setFilterScoreMax(int filterScoreMax) {
        this.filterScoreMax = filterScoreMax;
    }


    /**
     * Checks if a score filter is applied in the review query options. This method returns true if either the minimum
     * score filter or the maximum score filter is set to a value other than -1, indicating that a score filter is
     * active and should be applied when querying the database for reviews.
     *
     * @return
     */
    public boolean hasScoreFilter() {
        return filterScoreMin != -1 || filterScoreMax != -1;
    }


    /*
    utility
     */

    /**
     * Builds the SQL ORDER BY clause based on the orderByEnum value. This method translates the enum value into a
     * corresponding SQL ORDER BY clause that can be used in a SQL query to sort the results accordingly.
     *
     * @return
     */
    public String buildOrderBySql() {
        // TODO: by status should by id DESC?
        switch (orderByEnum) {
            case OPTION_ORDER_BY_ID_ASC:
                return "id ASC";
            case OPTION_ORDER_BY_ID_DESC:
                return "id DESC";
            case OPTION_ORDER_BY_CREATED_AT_ASC:
                return "createdAt ASC";
            case OPTION_ORDER_BY_CREATED_AT_DESC:
                return "createdAt DESC";

            case OPTION_ORDER_BY_SCORE_ASC:
                return "score ASC";
            case OPTION_ORDER_BY_SCORE_DESC:
                return "score DESC";

            case OPTION_ORDER_BY_PENDING_FIRST:
                return "status = " + Review.REVIEW_STATUS_PENDING + " DESC, id DESC";
            case OPTION_ORDER_BY_APPROVED_FIRST:
                return "status = " + Review.REVIEW_STATUS_APPROVED + " DESC, id DESC";
            case OPTION_ORDER_BY_REJECTED_FIRST:
                return "status = " + Review.REVIEW_STATUS_REJECTED + " DESC, id DESC";
            default:
                return "id ASC"; // default order
        }
    }


    /**
     * Returns a string representation of the ReviewQueryOptions object, including the page cursor, order by enum, and
     * status enum values. This method is useful for debugging and logging purposes, allowing developers to easily see
     * the current state of the ReviewQueryOptions object when it is printed or logged.
     *
     * @return
     */
    @Override
    public String toString() {
        return "ReviewQueryOptions{" +
            "cursor=" + pageCursor.toString() +
            ", orderByEnum=" + orderByEnum +
            ", statusEnum=" + statusEnum +
            '}';
    }
}
