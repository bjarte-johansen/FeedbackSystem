package root.repositories.review;

import org.springframework.stereotype.Repository;
import root.includes.ReviewQueryOptions;
import root.includes.PageCursor;
import root.database.FSQLQuery;
import root.includes.NumericRangeRecord;
import root.includes.WhereExpressionList;
import root.models.review.Review;

import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;;

@Repository
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {
    // set to false to override debug flags (they are DEBUG & ENABLE_LOCAL_DEBUG_FLAGS, both must be true for debug logs to print)
    public boolean ENABLE_LOCAL_DEBUG_FLAGS = false;


    /*
    condition accumulators
    , consider moving these methods out of repository
    TODO: do that!!
     */

    /*
     * smarter
     */

    private void buildScoreFilterSetConditions(WhereExpressionList whereExpressionList, Set<Integer> scoreFilter) {
        if (scoreFilter != null && !scoreFilter.isEmpty()) {
            whereExpressionList.whereIn("score", scoreFilter);
        }
    }

    private void buildScoreFilterRangeConditions(WhereExpressionList whereExpressionList, NumericRangeRecord<Integer> rangeFilter) {
        if (rangeFilter != null && rangeFilter.isValid()) {
            whereExpressionList.whereBetween("score", rangeFilter.start(), rangeFilter.end());
        }
    }

    private void buildStatusFilterConditions(WhereExpressionList whereExpressionList, Set<Integer> statusFilter) {
        if (statusFilter != null && !statusFilter.isEmpty()) {
            whereExpressionList.whereIn("status", statusFilter, true);
        }
    }

    private void applyReviewQueryOptionFilters(WhereExpressionList whereExpressionList, ReviewQueryOptions options) {
        // filter by status
        buildStatusFilterConditions(whereExpressionList, options.getStatusFilterSet());

        // filter by score range (scoreFilterRange and scoreFilterList are mutually exclusive.
        // range takes precedence as it is faster
        NumericRangeRecord<Integer> scoreFilterRange = options.getScoreFilterRange();
        buildScoreFilterRangeConditions(whereExpressionList, scoreFilterRange);

        // filter by score list, mutually exclusive with score range filter
        if (scoreFilterRange == null || !scoreFilterRange.isValid()) {
            buildScoreFilterSetConditions(whereExpressionList, options.getScoreFilterSet());
        }

        // filter by number of dates list, mutually exclusive with date range filter
        // filter number of days takes presence if exists
        int numberOfDaysFilter = options.getNumberOfDaysFilter(-1, true);

        if (numberOfDaysFilter >= 0) {
            // filter by number of days
            Instant end = Instant.now();
            Instant start = end.minusSeconds(numberOfDaysFilter * 24L * 3600L);
            whereExpressionList.whereBetween("created_at", start, end);
        } else {
            // add date filter if exists
            var dateRangeFilter = options.getDateFilterRange();

            if ((dateRangeFilter != null) && dateRangeFilter.hasAnyBound()) {
                LocalDate dtStart = dateRangeFilter.getStart();
                LocalDate dtEnd = dateRangeFilter.getEnd();

                Instant start = (dtStart != null) ? dtStart.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
                Instant end = (dtEnd != null) ? dtEnd.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : null;

                whereExpressionList.whereBetween("created_at", start, end);
            }
        }
    }



    /*
    end of helper methods
     */


//    /**
//     * Atomically increments the report count for a review, ensuring it stays within the bounds of a signed 16-bit
//     * integer.
//     *
//     * @param reviewId
//     */
//
//    @Override
//    public void incrementReportVote(long reviewId, int delta) {
//        String sql = "UPDATE review SET report_count = LEAST(32767, GREATEST(0, report_count + ?)) WHERE id = ?";
//
//        FSQLQuery.create(sql)
//            .bind(delta)
//            .bind(reviewId)
//            .update();
//    }


    /**
     * Increments like count for a review, ensuring it stays within the bounds
     */

    @Override
    public void incrementLikeVote(long reviewId, int delta) {
        String sql = "UPDATE review SET like_count = LEAST(32767, GREATEST(0, like_count + ?)) WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(delta)
            .bind(reviewId)
            .update();
    }

    /**
     * Increments dislike count for a review, ensuring it stays within the bounds
     */

    @Override
    public void incrementDislikeVote(long reviewId, int delta) {
        String sql = "UPDATE review SET dislike_count = LEAST(32767, GREATEST(0, dislike_count + ?)) WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(delta)
            .bind(reviewId)
            .update();
    }


    /**
     * Update review status for a given review Status should be one of Review.STATUS_APPROVED, Review.STATUS_PENDING,
     * Review.STATUS_REJECTED, etc.
     *
     * @param reviewId
     * @param newStatus
     * @return
     */
    @Override
    public int updateReviewStatus(long reviewId, int newStatus) {
        String sql = "UPDATE review SET status = ? WHERE id = ?";

        return FSQLQuery.create(sql)
            .bind(newStatus)
            .bind(reviewId)
            .update();
    }


    private WhereExpressionList createCommonExpressionList(WhereExpressionList whereExpressionList, ReviewQueryOptions options) {
        whereExpressionList = whereExpressionList != null ? whereExpressionList : new WhereExpressionList();

        // apply filters from options
        applyReviewQueryOptionFilters(whereExpressionList, options);

        // make where expr-list sql
        String whereStr = whereExpressionList.toSql(true);

        // make limit/offset sql
        String limitOffsetSql = buildPageCursorSql(options.getPageCursor());
        if (limitOffsetSql != null && !limitOffsetSql.isEmpty()) {
            limitOffsetSql = " " + limitOffsetSql;
        }

        // make order by sql
        String orderBySql = options.buildOrderBySql();
        if (orderBySql != null && !orderBySql.isEmpty()) {
            orderBySql = " ORDER BY " + orderBySql;
        }

        return whereExpressionList;
    }

    /**
     * Get review score statistics as a map of (score -> count) for a given externalId. The method takes an optional set
     * of status filters to include in the statistics. The results are ordered by score in ascending order. If
     * statusFilterSet is provided, only reviews with a status in the filter set will be included in the distribution.
     */

    @Override
    public LinkedHashMap<Integer, Integer> findApprovedReviewScoreStatsByExternalId(String externalId) {
        WhereExpressionList whereExpressionList = new WhereExpressionList(20);

        // add condition for external id
        if (externalId != null && !externalId.isEmpty()) {
            whereExpressionList.where("(external_id = ?)", externalId);
        }

        whereExpressionList.where("(status = ?)", Review.REVIEW_STATUS_APPROVED);

        // build sql
        String sql = "SELECT score, COUNT(*) AS count FROM review" + whereExpressionList.toSql(true) + " GROUP BY score";

        return FSQLQuery.create(sql)
            .bind(whereExpressionList.getArguments())
            .fetchCallback((ResultSet rs) -> {
                LinkedHashMap<Integer, Integer> res = new LinkedHashMap<>();
                while (rs.next()) {
                    int score = rs.getInt("score");
                    int count = rs.getInt("count");
                    res.put(score, count);
                }
                return res;
            });
    }


    /**
     * Find distinct list of externalId from reviews, can be used to find what externalIds has reviews in the system.
     * This can be useful for various purposes such as displaying a list of products that have reviews, or for
     * administrative purposes to see which externalIds are being reviewed.
     */

    @Override
    public List<String> findDistinctExternalIdByExternalId() {
        String sql = "SELECT DISTINCT external_id FROM review";

        return FSQLQuery.create(sql)
            .fetchCallback((ResultSet rs) -> {
                List<String> result = new ArrayList<>();
                int colIndex = rs.findColumn("external_id");
                while (rs.next()) {
                    result.add(rs.getString(colIndex));
                }
                return result;
            });
    }


    private String buildPageCursorSql(PageCursor pageCursor) {
        String s = "";

        if (pageCursor.getLimit() > 0) {
            s += "LIMIT " + pageCursor.getLimit();
        }

        if (pageCursor.getOffset() > 0) {
            if (!s.isEmpty()) s += " ";
            s += "OFFSET " + pageCursor.getOffset();
        }

        return s;
    }


    /**
     * @see #findByOptionalExternalIdWithPagination(String, ReviewQueryOptions) Main difference is that this method does
     * not take externalId at all, that is it works regardless of externalId. The actual query construction and
     * execution logic is shared in the private helper method to avoid code duplication.
     */

    @Override
    public List<Review> findByAnyExternalIdWithPagination(ReviewQueryOptions options) {
        return findByOptionalExternalIdWithPagination(null, options);
    }


    /**
     * @see #findByOptionalExternalIdWithPagination(String, ReviewQueryOptions)
     * <p>
     * Main difference is that this method requires a non-null and non-empty externalId, while
     * findByAnyExternalIdWithPagination allows for fetching reviews across all externalIds without filtering by a
     * specific externalId. The actual query construction and execution logic is shared in the private helper method to
     * avoid code duplication.
     */

    @Override
    public List<Review> findByExternalIdWithPagination(String externalId, ReviewQueryOptions options) {
        checkArgument(!(externalId == null || externalId.isEmpty()), "externalId cannot be null or empty");

        return findByOptionalExternalIdWithPagination(externalId, options);
    }


    /**
     * Find list of reviews by optional externalId and a ReviewQueryOptions object that contains various filtering and
     * pagination options. If externalId is provided, the reviews will be filtered by that externalId. If externalId is
     * null or empty, reviews for any externalId will be returned based on the filters in options. The method constructs
     * a dynamic SQL query based on the provided parameters and executes it to fetch the matching reviews from the
     * database.
     */

    public List<Review> findByOptionalExternalIdWithPagination(String externalId, ReviewQueryOptions options) {
        String columnStr = "id, external_id, author_name, score, title, comment, like_count, dislike_count, status, created_at";

        var whereExpressionList = new WhereExpressionList(20);

        // add condition for external id
        if (externalId != null && !externalId.isEmpty()) {
            whereExpressionList.where("(external_id = ?)", externalId);
        }

        // apply filters from options
        applyReviewQueryOptionFilters(whereExpressionList, options);

        // make where expr-list sql
        String whereStr = whereExpressionList.toSql(true);

        // make limit/offset sql
        String limitOffsetSql = buildPageCursorSql(options.getPageCursor());
        if (limitOffsetSql != null && !limitOffsetSql.isEmpty()) {
            limitOffsetSql = " " + limitOffsetSql;
        }

        // make order by sql
        String orderBySql = options.buildOrderBySql();
        if (orderBySql != null && !orderBySql.isEmpty()) {
            orderBySql = " ORDER BY " + orderBySql;
        }

        // make query and execute
        String sql = "SELECT " + columnStr + " FROM review" + whereStr + orderBySql + limitOffsetSql;

        return FSQLQuery.create(sql)
            .bind(whereExpressionList.getArguments())
            .debug(ENABLE_LOCAL_DEBUG_FLAGS)
            .fetchAll(Review.class);
    }


    @Override
    public int countByExternalId(String externalId, ReviewQueryOptions options) {
        WhereExpressionList whereExpressionList = new WhereExpressionList(20);

        // add condition for external id
        checkArgument(externalId != null && !externalId.isEmpty(), "externalId cannot be null or empty");
        whereExpressionList.where("(external_id = ?)", externalId);

        // apply filters from options
        applyReviewQueryOptionFilters(whereExpressionList, options);

        String sql = "SELECT COUNT(*) FROM review " + whereExpressionList.toSql(true);

        return (int) FSQLQuery.create(sql)
            .bind(whereExpressionList.getArguments())
            .selectCount();
    }


    public Map<Integer, Integer> countByExternalIdMapByStatus(String externalId){
        String sql = "SELECT status, COUNT(*) AS count"
            + " FROM review"
            + " WHERE external_id = ?"
            + " GROUP BY status"
            + " ORDER BY status";

        return FSQLQuery.create(sql)
            .bind(externalId)
            .fetchCallback(rs -> {
                Map<Integer, Integer> map = new LinkedHashMap<>();
                while (rs.next()) {
                    int status = rs.getInt("status");
                    int count = rs.getInt("count");
                    map.put(status, count);
                }
                return map;
            });
    }
/*
    @Override
    public int countByAnyExternalId(ReviewQueryOptions options) {
        WhereExpressionList whereExpressionList = new WhereExpressionList(20);

        // apply filters from options
        applyReviewQueryOptionFilters(whereExpressionList, options);

        String sql = "SELECT COUNT(*) FROM review " + whereExpressionList.toSql(true);

        return (int) FSQLQuery.create(sql)
            .bind(whereExpressionList.getArguments())
            .selectCount();
    }

 */
}