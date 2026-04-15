package root.repositories;

import org.springframework.stereotype.Repository;
import root.app.ReviewQueryOptions;
import root.database.FSQLQuery;
import root.includes.NumericRangeRecord;
import root.includes.WhereExpressionList;
import root.models.Review;

import java.sql.ResultSet;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;;

@Repository
public class ReviewRepositoryCustomImpl implements ReviewRepositoryInterface{
    // set to false to override debug flags (they are DEBUG & ENABLE_LOCAL_DEBUG_FLAGS, both must be true for debug logs to print)
    public boolean ENABLE_LOCAL_DEBUG_FLAGS = false;


    /*
    condition accumulators
    , consider moving these methods
     */

    /*
    */

/*
    private void clampLongSafeExprSql(whereExpressionList exprList, String col, long min, long max, String expr, String op, long exprVal) {
        Set<String> allValidComparisonOps = Set.of(">", ">=", "<", "<=", "=", "<>");
        Set<String> allValidArithmeticOps = Set.of("+", "-", "*", "/", "%");
        if(op
        exprList.where("LEAST(?, GREATEST(?, " + expr + " " + op + " ?))", max, min, exprVal);
    }
    private String clampIntSql(String col, int min, int max, String expr) {
        return clampLongSql(col, (long) min, (long) max, expr);
    }
 */

    /*
     * smarter
     */

    private void buildScoreFilterSetConditions(WhereExpressionList whereExpressionList, Set<Integer> scoreFilter) {
        if(scoreFilter != null && !scoreFilter.isEmpty()) {
            whereExpressionList.whereIn("score", scoreFilter);
        }
    }

    private void buildScoreFilterRangeConditions(WhereExpressionList whereExpressionList, NumericRangeRecord<Integer> rangeFilter) {
        if(rangeFilter != null && rangeFilter.isValid()) {
            whereExpressionList.whereBetween("score", rangeFilter.start(), rangeFilter.end());
        }
    }

    private void buildStatusFilterConditions(WhereExpressionList whereExpressionList, Set<Integer> statusFilter) {
        whereExpressionList.whereIn("status", statusFilter, true);
    }

    private void applyReviewQueryOptionFilters(WhereExpressionList whereExpressionList, ReviewQueryOptions options) {
        // filter by status
        buildStatusFilterConditions(whereExpressionList, options.getStatusFilterSet());

        // filter by score range (scoreFilterRange and scoreFilterList are mutually exclusive. If range is valid,
        // it will be used, otherwise filter by list (if any)).
        NumericRangeRecord<Integer> scoreFilterRange = options.getScoreFilterRange();
        buildScoreFilterRangeConditions(whereExpressionList, scoreFilterRange);

        // filter by score list, mutually exclusive with score range filter
        if(scoreFilterRange == null || !scoreFilterRange.isValid()) {
            buildScoreFilterSetConditions(whereExpressionList, options.getScoreFilterSet());
        }

        // add date filter if exists
        if(options.getDateFilterRange() != null) {
            whereExpressionList.whereBetween(
                "created_at",
                options.getDateFilterRange().getStart(),
                options.getDateFilterRange().getEnd().plusDays(1)
                );
        }
    }

    //



    /*
    end of helper methods
     */


    /**
     * Atomically increments the report count for a review, ensuring it stays within the bounds of a signed 16-bit
     * integer.
     * @param reviewId
     */

    @Override
    public void incrementReportVote(long reviewId, int delta) {
        String sql = "UPDATE review SET report_count = LEAST(32767, GREATEST(0, report_count + ?)) WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(1)
            .bind(reviewId)
            .update();
    }


    @Override
    public void incrementLikeVote(long reviewId, int delta) {
        String sql = "UPDATE review SET like_count = LEAST(32767, GREATEST(0, like_count + ?)) WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(delta)
            .bind(reviewId)
            .update();
    }


    @Override
    public void incrementDislikeVote(long reviewId, int delta) {
        String sql = "UPDATE review SET dislike_count = LEAST(32767, GREATEST(0, dislike_count + ?)) WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(delta)
            .bind(reviewId)
            .update();
    }


    @Override
    public void updateReviewStatus(long reviewId, int newStatus) {
        String sql = "UPDATE review SET status = ? WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(newStatus)
            .bind(reviewId)
            .update();
    }


    @Override
    public LinkedHashMap<Integer, Integer> findReviewScoreStatsByExternalId(String externalId){
        // lambda to read result set and convert to LinkedHashMap<Integer, Integer>
        FSQLQuery.ResultSetFunction<LinkedHashMap<Integer, Integer>> fnReadResultSet = (ResultSet rs) -> {
            LinkedHashMap<Integer, Integer> res = new LinkedHashMap<>();
            while (rs.next()) {
                int score = rs.getInt("score");
                int count = rs.getInt("count");
                res.put(score, count);
            }
            return res;
        };

        WhereExpressionList whereExprList = new WhereExpressionList(20);
        whereExprList.where("(external_id = ?)", externalId);
        whereExprList.where("(status = ?)", Review.REVIEW_STATUS_APPROVED);

        //buildScoreFilterSetConditions(whereExprList, scoreFilterSet);

        // build sql
        String whereStr = whereExprList.toSql(true);
        String sql = "SELECT score, COUNT(*) AS count FROM review" + whereStr + " GROUP BY score";

        try {
            return FSQLQuery.create(sql)
                .bind(whereExprList.getArguments())
                .fetchCallback(fnReadResultSet);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> findDistinctExternalIdByExternalId() {
        String sql = "SELECT DISTINCT external_id FROM review";

        return FSQLQuery.create(sql)
            .fetchCallback((ResultSet rs) -> {
                List<String> result = new ArrayList<>();
                int colIndex = rs.findColumn("external_id");
                while(rs.next()) {
                    result.add(rs.getString(colIndex));
                }
                return result;
            });
    }


    /*
    find with or without external id with pagination and filtering options.
     */

    @Override
    public List<Review> findByAnyExternalIdWithPagination(ReviewQueryOptions options) {
        return findByOptionalExternalIdWithPagination(null, options);
    }

    @Override
    public List<Review> findByExternalIdWithPagination(String externalId, ReviewQueryOptions options) {
        checkArgument(!(externalId == null || externalId.isEmpty()), "externalId cannot be null or empty");

        return findByOptionalExternalIdWithPagination(externalId, options);
    }


    private List<Review> findByOptionalExternalIdWithPagination(String externalId, ReviewQueryOptions options) {
        String columnStr = "id, external_id, author_name, score, title, comment, like_count, dislike_count, status, created_at";

        // make condition list
        WhereExpressionList whereExpressionList = new WhereExpressionList(20);

        // add condition for external id
        if(externalId != null && !externalId.isEmpty()){
            whereExpressionList.where("(external_id = ?)", externalId);
        }

        // apply filters from options
        applyReviewQueryOptionFilters(whereExpressionList, options);

        // make where expr-list sql
        String whereStr = whereExpressionList.toSql(true);

        // make limit/offset sql
        String limitOffsetSql = options.getPageCursor().buildLimitOffsetSql();
        if(limitOffsetSql != null && !limitOffsetSql.isEmpty()) {
            limitOffsetSql = " " + limitOffsetSql;
        }

        // make order by sql
        String orderBySql = options.buildOrderBySql();
        if(orderBySql != null && !orderBySql.isEmpty()) {
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

    /*
    public int countByExternalIdAndStatus(String externalId, int status) {
        checkArgument(externalId != null && !externalId.isEmpty(), "externalId cannot be null or empty");

        String sql = "SELECT COUNT(*) FROM review WHERE (external_id = ?) AND (status = ?)";
        return (int) FSQLQuery.create(sql)
            .bind(externalId)
            .bind(status)
            .selectCount();
    }
     */
}