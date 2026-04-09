package root.repositories;

import org.springframework.stereotype.Repository;
import root.app.ReviewQueryOptions;
import root.database.FSQLQuery;
import root.includes.NumericRangeRecord;
import root.models.Review;

import java.sql.ResultSet;
import java.util.*;

import static root.common.utils.Preconditions.checkArgument;

@Repository
public class ReviewRepositoryCustomImpl implements ReviewRepositoryInterface{
    /*

     */

    private static <T extends Number> String toCsv(Set<T> set) {
        if (set == null || set.isEmpty()) return "";

        // get rough storage capacity per element based on type of first element (assuming all elements are of the same type)
        Number it = set.iterator().next();
        boolean is_real = ((it instanceof Double) || (it instanceof Float));
        int rough_capacity_per_element = is_real ? 20 : 10;

        // use StringBuilder for efficient string concatenation
        StringBuilder sb = new StringBuilder(set.size() * rough_capacity_per_element);

        boolean first = true;
        for (Number n : set) {
            if (!first) sb.append(',');
            sb.append(n);
            first = false;
        }

        return sb.toString();
    }

    private <T extends Number> String buildInClauseSql(String columnName, List<T> valueList) {
        if(valueList == null || valueList.isEmpty()) {
            return "(1=1)";
        }

        // return result
        return buildInClauseSql(columnName, new HashSet<>(valueList));
    }

    private <T extends Number> String buildInClauseSql(String columnName, Set<T> filterSet) {
        if(filterSet == null || filterSet.isEmpty()) {
            return "(1=1)";
        }

        return "(" + columnName + " IN (" + toCsv(filterSet) + "))";
    }



    /*
    check if a set of integers is contiguous (i.e. forms a continuous range without gaps).
     */

    private static boolean isContiguous(Set<Integer> set) {
        if (set == null || set.isEmpty()) return false;

        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;

        // find min/max in one pass
        for (int v : set) {
            if (v < min) min = v;
            if (v > max) max = v;
        }

        // contiguous ⇔ size == range length
        return set.size() == (max - min + 1);
    }



    /*
    condition accumulators
    , consider moving these methods
     */

    private void buildScoreFilterSetConditions(List<String> conditionExprList, Set<Integer> scoreFilter) {
        if(scoreFilter != null && !scoreFilter.isEmpty()) {
            conditionExprList.add(buildInClauseSql("score", scoreFilter));
        }
    }

    private void buildScoreFilterRangeConditions(List<String> conditionExprList, NumericRangeRecord<Integer> rangeFilter) {
        if(rangeFilter != null && rangeFilter.isValid()) {
            String filterSql = "(score >= " + rangeFilter.min() + " AND score <= " + rangeFilter.max() + ")";
            conditionExprList.add(filterSql);
        }
    }

    private void buildStatusFilterConditions(List<String> conditionExprList, Set<Integer> statusFilter) {
        if (statusFilter != null && !statusFilter.isEmpty()) {
            if(isContiguous(statusFilter)) {
                int min = Collections.min(statusFilter);
                int max = Collections.max(statusFilter);

                String filterSql = "(status >= " + min + " AND status <= " + max + ")";
                conditionExprList.add(filterSql);
            }else {
                conditionExprList.add(buildInClauseSql("status", statusFilter));
            }
        }
    }

    private void applyReviewQueryOptions(List<String> conditionExprList, ReviewQueryOptions options) {
        // filter by status
        Set<Integer> statusFilterSet = options.getStatusFilterSet();
        buildStatusFilterConditions(conditionExprList, statusFilterSet);

        // filter by score range (scoreFilterRange and scoreFilterList are mutually exclusive. If range is valid,
        // it will be used, otherwise filter by list (if any)).
        NumericRangeRecord<Integer> scoreFilterRange = options.getScoreFilterRange();
        buildScoreFilterRangeConditions(conditionExprList, scoreFilterRange);

        // filter by score list, mutually exclusive with score range filter
        if(scoreFilterRange == null || !scoreFilterRange.isValid()) {
            Set<Integer> scoreFilterSet = options.getScoreFilterSet();
            buildScoreFilterSetConditions(conditionExprList, scoreFilterSet);
        }
    }

/*
    private String clampLongSql(String columnName, long min, long max, String valuePlaceholder) {
        return String.format("LEAST(%d, GREATEST(%d, %s))", max, min, valuePlaceholder);
    }
    private String clampIntSql(String columnName, int min, int max, String valuePlaceholder) {
        return clampLongSql(columnName, (long) min, (long) max, valuePlaceholder);
    }
 */





    /*
    end of helper methods
     */


    /**
     * Atomically increments the report count for a review, ensuring it stays within the bounds of a signed 16-bit
     * integer.
     * @param reviewId
     */
    @Override
    public void incrementReportVote(long reviewId, int delta) throws Exception {
        String sql = "UPDATE review SET report_count = LEAST(32767, GREATEST(0, report_count + ?)) WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(1)
            .bind(reviewId)
            .update();
    }

    @Override
    public void incrementLikeVote(long reviewId, int delta) throws Exception {
        String sql = "UPDATE review SET like_count = LEAST(32767, GREATEST(0, like_count + ?)) WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(delta)
            .bind(reviewId)
            .update();
    }

    @Override
    public void incrementDislikeVote(long reviewId, int delta) throws Exception {
        String sql = "UPDATE review SET dislike_count = LEAST(32767, GREATEST(0, dislike_count + ?)) WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(delta)
            .bind(reviewId)
            .update();
    }

    @Override
    public void updateReviewStatus(long reviewId, int newStatus) throws Exception {
        String sql = "UPDATE review SET status = ? WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(newStatus)
            .bind(reviewId)
            .update();
    }


    @Override
    public LinkedHashMap<Integer, Integer> findReviewScoreStatsByExternalId(String externalId, Set<Integer> scoreFilterSet) throws Exception{
        FSQLQuery.ResultSetFunction<LinkedHashMap<Integer, Integer>> fnReadResultSet = (ResultSet rs) -> {
            LinkedHashMap<Integer, Integer> res = new LinkedHashMap<>();
            while (rs.next()) {
                int score = rs.getInt("score");
                int count = rs.getInt("count");
                res.put(score, count);
            }
            return res;
        };

        List<String> conditionExprList = new ArrayList<>(20);
        conditionExprList.add("(external_id = ?)");
        conditionExprList.add("(status = ?)");

        buildScoreFilterSetConditions(conditionExprList, scoreFilterSet);

        // build sql
        String whereStr = conditionExprList.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditionExprList);
        String sql = "SELECT score, COUNT(*) AS count FROM review"
            + whereStr
            + " GROUP BY score";

        return FSQLQuery.create(sql)
            .bind(externalId)
            .bind(Review.REVIEW_STATUS_APPROVED)
            .fetchCallback(fnReadResultSet);
    }

    @Override
    public List<String> findUniqueExternalIds() throws Exception{
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
    public List<Review> findByAnyExternalIdWithPagination(ReviewQueryOptions options) throws Exception{
        return findByOptionalExternalIdWithPagination(null, options);
    }

    @Override
    public List<Review> findByExternalIdWithPagination(String externalId, ReviewQueryOptions options) throws Exception {
        checkArgument(!(externalId == null || externalId.isEmpty()), "externalId cannot be null or empty");

        return findByOptionalExternalIdWithPagination(externalId, options);
    }


    private List<Review> findByOptionalExternalIdWithPagination(String externalId, ReviewQueryOptions options) throws Exception {
        String columnStr = "id, external_id, author_name, score, title, comment, like_count, dislike_count, status, created_at";

        // make condition list
        ArrayList<String> conditionExprList = new ArrayList<>(20);

        // add condition for external id
        if(externalId != null && !externalId.isEmpty()){
            conditionExprList.add("(external_id = ?)");
        }

        // apply filters from options
        applyReviewQueryOptions(conditionExprList, options);


        // make where expr-list sql
        String whereStr = conditionExprList.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditionExprList);

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
            .bindIf(externalId != null, externalId)
            .debug(true)
            .fetchAll(Review.class);
    }



    @Override
    public int countByExternalId(String externalId, ReviewQueryOptions options) throws Exception{
        List<String> conditionExprList = new ArrayList<>(20);

        // add condition for external id
        checkArgument(externalId != null && !externalId.isEmpty(), "externalId cannot be null or empty");
        conditionExprList.add("(external_id = ?)");

        // apply filters from options
        applyReviewQueryOptions(conditionExprList, options);

        String sql = "SELECT COUNT(*) FROM review WHERE " + String.join(" AND ", conditionExprList);

        return (int) FSQLQuery.create(sql)
            .bind(externalId)
            .selectCount();
    }

    @Override
    public int countByAnyExternalId(ReviewQueryOptions options) throws Exception{
        List<String> conditionExprList = new ArrayList<>(20);

        // apply filters from options
        applyReviewQueryOptions(conditionExprList, options);

        String sql = "SELECT COUNT(*) FROM review WHERE " + String.join(" AND ", conditionExprList);

        return (int) FSQLQuery.create(sql)
            .selectCount();
    }

    public int countByExternalIdAndStatus(String externalId, int status) throws Exception{
        checkArgument(externalId != null && !externalId.isEmpty(), "externalId cannot be null or empty");

        String sql = "SELECT COUNT(*) FROM review WHERE (external_id = ?) AND (status = ?)";
        return (int) FSQLQuery.create(sql)
            .bind(externalId)
            .bind(status)
            .selectCount();
    }
}