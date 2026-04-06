package root.repositories;

import org.springframework.stereotype.Repository;
import root.app.ReviewQueryOptions;
import root.database.FSQLQuery;
import root.includes.logger.logger.Logger;
import root.models.Review;
import root.utils.RangeQueryFilterBuilder;

import java.sql.ResultSet;
import java.util.*;

@Repository
public class ReviewRepositoryCustomImpl {
    // This class can be used to implement custom methods for the ReviewRepository if needed.

    void addVoteReport(long reviewId) throws Exception{
        String sql = "UPDATE review SET report_count = LEAST(32767, GREATEST(-32768, report_count + ?)) WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(1)
            .bind(reviewId)
            .update();
    }

    void addVoteUp(long reviewId, int delta) throws Exception{
        String sql = "UPDATE review SET like_count = LEAST(32767, GREATEST(-32768, like_count + ?)) WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(delta)
            .bind(reviewId)
            .update();
    };

    void addVoteDown(long reviewId, int delta) throws Exception{
        String sql = "UPDATE review SET dislike_count = LEAST(32767, GREATEST(-32768, dislike_count + ?)) WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(delta)
            .bind(reviewId)
            .update();
    };

    void updateReviewStatus(long reviewId, int newStatus) throws Exception{
        String sql = "UPDATE review SET status = ? WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(newStatus)
            .bind(reviewId)
            .update();
    };


    LinkedHashMap<Integer, Integer> findReviewScoreStatsByExternalId(String externalId, int filterScoreMin, int filterScoreMax) throws Exception{
        FSQLQuery.ResultSetConsumer<LinkedHashMap<Integer, Integer>> fnReadResultSet = (ResultSet rs) -> {
            LinkedHashMap<Integer, Integer> res = new LinkedHashMap<>();
            while (rs.next()) {
                int score = rs.getInt("score");
                int count = rs.getInt("count");
                res.put(score, count);
            }
            return res;
        };

        var filterRangeSql = RangeQueryFilterBuilder.buildSqlQueryString(
            "score",
            filterScoreMin > -1 ? filterScoreMin : null,
            filterScoreMax > -1 ? filterScoreMax : null,
            true,
            true,
            "(1=1)");

        String sql = "SELECT score, COUNT(*) AS count FROM review WHERE ((external_id = ?) AND (status = ?) AND " + filterRangeSql + ") GROUP BY score";
        return FSQLQuery.create(sql)
            .bind(externalId)
            .bind(Review.REVIEW_STATUS_APPROVED)
            .fetchCallback(fnReadResultSet);
    };

    List<String> findUniqueExternalIds() throws Exception{
        //FSQLQuery.ResultSetConsumer<List<String>> fnReadResultSet = ;

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

    List<Review> findByExternalIdWithPagination(String externalId, ReviewQueryOptions options) throws Exception {
        boolean DEBUG_SQL = false;
        String columnStr = "id, external_id, author_name, score, title, comment, like_count, dislike_count, status, created_at";

        // make condition list
        ArrayList<String> conditionExprList = new ArrayList<>();

        // add external id filter
        if (externalId != null && !externalId.isEmpty()) {
            // add condition for external id
            conditionExprList.add("(external_id = ?)");
        }

        if ((options.getStatusEnum() == Review.REVIEW_STATUS_MATCH_ALL)) {
            // catch all, no need to add any condition
        } else {
            // check bitflags
            if ((options.getStatusEnum() & Review.REVIEW_STATUS_APPROVED) != 0) {
                conditionExprList.add("(status = " + Review.REVIEW_STATUS_APPROVED + ")");
            }
            if ((options.getStatusEnum() & Review.REVIEW_STATUS_PENDING) != 0) {
                conditionExprList.add("(status = " + Review.REVIEW_STATUS_PENDING + ")");
            }
            if ((options.getStatusEnum() & Review.REVIEW_STATUS_REJECTED) != 0) {
                conditionExprList.add("(status = " + Review.REVIEW_STATUS_REJECTED + ")");
            }
        }
/*
        record ScoreFilter(int min, int max) {
            boolean isEmpty() {
                return min() < 0 && max() < 0;
            }
        }
 */

        var filterRangeSql = RangeQueryFilterBuilder.buildSqlQueryString(
            "score",
            options.getFilterScoreMin() > -1 ? options.getFilterScoreMin() : null,
            options.getFilterScoreMax() > -1 ? options.getFilterScoreMax() : null,
            true,
            true,
            ""
        );

        if(filterRangeSql != null && !filterRangeSql.isEmpty()) {
            Logger.log("Adding filter range SQL: " + filterRangeSql);
            conditionExprList.add(filterRangeSql);
        }

/*
        // add score filter, if any
        ScoreFilter filter = new ScoreFilter(options.getFilterScoreMin(), options.getFilterScoreMax());
        if(!filter.isEmpty()) {
            if((filter.min() > -1) && (filter.max() > -1)) {
                // both min and max are set, add a between condition
                conditionExprList.add("(score >= " + filter.min() + " AND score <= " + filter.max() + ")");
            } else if (filter.min() > -1) {
                // at least one filter is set, add a condition group for score
                conditionExprList.add("(score >= " + filter.min() + ")");
            } else {
                // at least one filter is set, add a condition group for score
                conditionExprList.add("(score <= " + filter.max() + ")");
            }
        }

 */

        // make where expr-list sql
        String whereStr = conditionExprList.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditionExprList);

        // make limit/offset sql
        String limitOffsetSql = options.getPageCursor().buildLimitOffsetSql();
        if(!limitOffsetSql.isEmpty()) {
            limitOffsetSql = " " + limitOffsetSql;
        }

        // make order by sql
        String orderBySql = options.buildOrderBySql();
        if(!orderBySql.isEmpty()) {
            orderBySql = " ORDER BY " + orderBySql;
        }

        // make query and execute
        String sql = "SELECT " + columnStr + " FROM review" + whereStr + orderBySql + limitOffsetSql;

        return FSQLQuery.create(sql)
            .debug(DEBUG_SQL)
            .bindIf(externalId != null, externalId)
            .fetchAll(Review.class);
    }

    List<Review> findByAnyExternalIdWithPagination(ReviewQueryOptions options) throws Exception{
        return findByExternalIdWithPagination(null, options);
    }

    int countByExternalIdAndStatus(String externalId, int status) throws Exception{
        String sql = "SELECT COUNT(*) FROM review WHERE (external_id = ?) AND (status = ?)";
        return (int) FSQLQuery.create(sql)
            .bind(externalId)
            .bind(status)
            .selectCount();
    }
}