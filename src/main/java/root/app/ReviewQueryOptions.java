package root.app;

import root.includes.PageCursor;
import root.includes.ImmutableUnboundedDateRange;
import root.includes.NumericRangeRecord;
import root.models.Review;

import java.time.LocalDate;
import java.util.*;


/**
 * The ReviewQueryOptions class is a data structure that encapsulates various options for querying reviews from the
 * database. It includes properties for pagination (page cursor), sorting order (order by enum), and filtering criteria
 * (status filter list, score filter list, and score filter range). This class provides a convenient way to specify
 * different query options when retrieving reviews, allowing for flexible and customizable queries based on different
 * criteria such as review status, review scores, and sorting preferences..
 * <p>
 * The class includes constants for different sorting options, such as sorting by review ID, creation date, score, and
 * review status (pending, approved, rejected). It also provides getter and setter methods for each property, as well as
 * a utility method to build the SQL ORDER BY clause based on the specified sorting option. Overall, the
 * ReviewQueryOptions class serves as a comprehensive container for various query options that can be used when fetching
 * reviews from the database, allowing developers to easily customize their queries based on specific requirements
 * <p>
 * Note that scoreFilterRange takes presedence over scoreFilterList, as they are mutually exclusive. If scoreFilterRange
 * is set, scoreFilterList will be cleared and ignored when building the query. If you want to use scoreFilterList,
 * please set scoreFilterRange to null.
 */

public class ReviewQueryOptions {

    /*
     * Constants for sorting options:
     *
     * when converting to string using buildOrderBySql(), these constants will be translated to corresponding
     * SQL ORDER BY clauses that determine how the review results are sorted when retrieved from the database.
     *
     * Add "id DESC" as the secondary sorting criteria for all order by options to ensure consistent ordering of
     * reviews with the same primary sorting value, and to provide a deterministic order for reviews when the primary
     * sorting criteria is the same.
     *
     * ALWAYS Use buildOrderBySql() to convert the orderByEnum value to a corresponding SQL ORDER BY clause.
     *
     * Important: remember to update OPTION_ORDER_BY_MAP method if you add new order by options.
     */

    public static final int OPTION_ORDER_NONE = 0;

    public static final int OPTION_ORDER_BY_ID_ASC = 1;
    public static final int OPTION_ORDER_BY_ID_DESC = 2;

    public static final int OPTION_ORDER_BY_CREATED_AT_ASC = 3;
    public static final int OPTION_ORDER_BY_CREATED_AT_DESC = 4;

    public static final int OPTION_ORDER_BY_SCORE_ASC = 8;
    public static final int OPTION_ORDER_BY_SCORE_DESC = 9;

    public static final int OPTION_ORDER_BY_STATUS_PENDING_FIRST = 20;
    public static final int OPTION_ORDER_BY_STATUS_APPROVED_FIRST = 21;
    public static final int OPTION_ORDER_BY_STATUS_REJECTED_FIRST = 22;

    public static final int OPTION_ORDER_BY_LIKE_COUNT_ASC = 23;
    public static final int OPTION_ORDER_BY_LIKE_COUNT_DESC = 24;


    private static final Map<Integer, String> OPTION_ORDER_BY_MAP = Map.ofEntries(
        Map.entry(OPTION_ORDER_NONE, ""),
        Map.entry(OPTION_ORDER_BY_ID_ASC, "id ASC"),
        Map.entry(OPTION_ORDER_BY_ID_DESC, "id DESC"),
        Map.entry(OPTION_ORDER_BY_CREATED_AT_ASC, "created_at ASC"),
        Map.entry(OPTION_ORDER_BY_CREATED_AT_DESC, "created_at DESC"),
        Map.entry(OPTION_ORDER_BY_SCORE_ASC, "score ASC"),
        Map.entry(OPTION_ORDER_BY_SCORE_DESC, "score DESC"),
        Map.entry(OPTION_ORDER_BY_STATUS_PENDING_FIRST, "(status = " + Review.REVIEW_STATUS_PENDING + ") DESC, id DESC"),
        Map.entry(OPTION_ORDER_BY_STATUS_APPROVED_FIRST, "(status = " + Review.REVIEW_STATUS_APPROVED + ") DESC, id DESC"),
        Map.entry(OPTION_ORDER_BY_STATUS_REJECTED_FIRST, "(status = " + Review.REVIEW_STATUS_REJECTED + ") DESC, id DESC"),
        Map.entry(OPTION_ORDER_BY_LIKE_COUNT_DESC, "(like_count) DESC, id DESC"),
        Map.entry(OPTION_ORDER_BY_LIKE_COUNT_ASC, "(like_count) ASC, id ASC")
    );


    // Instance properties
    private PageCursor pageCursor;
    private int orderByEnum;

    private final Set<Integer> statusFilterSet = new HashSet<>();
    private final Set<Integer> scoreFilterSet = new HashSet<>();

    private NumericRangeRecord<Integer> scoreFilterRange = null;
    private ImmutableUnboundedDateRange<LocalDate> dateFilterRange = null;


    /*
    score filter range
     */

    /**
     * Returns the score filter range for the review query options. This value allows developers to specify a range of
     * review scores to be included in the query results. The NumericRangeRecord object contains a minimum and maximum
     * score value, and only reviews with scores that fall within this range will be included in the results when
     * querying the database for reviews.
     *
     * @return
     */

    public NumericRangeRecord<Integer> getScoreFilterRange() {
        return scoreFilterRange;
    }


    /**
     * Sets the score filter range for the review query options. This value allows developers to specify a range of
     * review scores to be included in the query results. The method accepts a NumericRangeRecord object that contains a
     * minimum and maximum score value, and only reviews with scores that fall within this range will be included in the
     * results when querying the database for reviews.
     * <p>
     * Will clear score filter list when setting score filter range, as they are mutually exclusive. If you want to use
     * score filter list, please set score filter range to null.
     *
     * @param range
     */

    public void setScoreFilterRange(NumericRangeRecord<Integer> range) {
        // clear score filter list
        scoreFilterSet.clear();

        // set score filter range
        scoreFilterRange = range;
    }





    /*
    status filter list
     */

    /**
     * Returns the status filter set for the review query options. This set allows developers to specify multiple review
     * status values to be included in the query results. By adding status values to this set (e.g., approved, pending,
     * rejected), developers can filter reviews based on their status when querying the database, allowing for more
     * flexible and customized filtering of reviews in the results. Using a Set instead of a List provides better
     * performance for lookups and ensures that each status value is unique in the filter criteria.
     *
     * @return
     */
    public Set<Integer> getStatusFilterSet() {
        return statusFilterSet;
    }

    public void setStatusFilterSet(Set<Integer> statusFilterSet) {
        this.statusFilterSet.clear();
        this.statusFilterSet.addAll(statusFilterSet);
    }




    /*
    score filter list
     */

    /**
     * Returns the score filter list for the review query options. This list allows developers to specify multiple
     * review score values to be included in the query results. By adding score values to this list, developers can
     * filter reviews based on their scores (e.g., 1 to 5) when querying the database, allowing for more flexible and
     * customized filtering of reviews in the results.
     *
     * @return
     */

    public Set<Integer> getScoreFilterSet() {
        return scoreFilterSet;
    }



    /*
    date filter range
     */

    /**
     * Returns the date filter range for the review query options. This value allows developers to specify a range of
     * review creation dates to be included in the query results.
     * @return
     */

    public ImmutableUnboundedDateRange<LocalDate> getDateFilterRange() {
        return dateFilterRange;
    }


    /**
     * Set the date filter range for the review query options. This value allows developers to specify a range of review
     * creation dates to be included in the query results.
     * @param dateFilterRange
     */
    public void setDateFilterRange(ImmutableUnboundedDateRange<LocalDate> dateFilterRange) {
        this.dateFilterRange = dateFilterRange;
    }



    /*
    review options
     */

    /**
     * Default constructor for the ReviewQueryOptions class. Initializes a new instance of the ReviewQueryOptions class
     * with default values. The page cursor is initialized to a new instance of the PageCursor class, and the status
     * enum and order by enum are set to -1, indicating that they are not specified. This constructor allows for the
     * creation of a ReviewQueryOptions object with default settings, which can be modified later using the setter
     * methods provided in the class.
     */
    public ReviewQueryOptions() {
        this(new PageCursor(), -1);
    }

    /**
     * Parameterized constructor for the ReviewQueryOptions class. Initializes a new instance of the ReviewQueryOptions
     * class with the specified page cursor, status enum, and order by enum values. This constructor allows for the
     * creation of a ReviewQueryOptions object with specific settings for pagination, filtering by review status, and
     * sorting order. The provided values are assigned to the corresponding properties of the class, allowing for
     * customized query options to be set when creating an instance of the ReviewQueryOptions class.
     *
     * @param pageCursor
     * @param orderByEnum
     */

    public ReviewQueryOptions(PageCursor pageCursor, Integer orderByEnum) {
        this.pageCursor = pageCursor;
        this.orderByEnum = orderByEnum;
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
    utility
     */

    /**
     * Builds the SQL ORDER BY clause based on the orderByEnum value. This method translates the enum value into a
     * corresponding SQL ORDER BY clause that can be used in a SQL query to sort the results accordingly.
     *
     * @return
     */

    public String buildOrderBySql() {
        var option = OPTION_ORDER_BY_MAP.get(getOrderByEnum());
        if(option == null){
            // TODO: should be logged
            //throw new IllegalArgumentException(String.format("Unsupported order option %s", getOrderByEnum()));
            return "id DESC";
        }
        return option;
    }


//    /**
//     * Checks if the review query options have any filters applied. This method returns true if any of the filter criteria
//     * (status filter set, score filter set, score filter range, date filter range) are specified, indicating that the
//     * query options include filtering criteria that will be applied when querying the database for reviews. If
//     * no filters are applied, the method returns false, indicating that the query options will not filter the results
//     * based on any specific criteria.
//     */
//
//    public String buildDateRangeFilterSql(String dateFieldName) {
//        if (dateFilterRange == null) return "";
//
//        return String.format("((%s >= '%s') AND (%s <= '%s'))",
//            dateFieldName, dateFilterRange.getStart().toString(),
//            dateFieldName, dateFilterRange.getEnd().toString());
//    }


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
            ", statusFilterSet=" + statusFilterSet +
            ", scoreFilterSet=" + scoreFilterSet +
            ", scoreFilterRange=" + (scoreFilterRange == null ? "null" : scoreFilterRange.toString()) +
            '}';
    }
}