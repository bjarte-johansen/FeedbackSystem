package root.repositories;

import org.springframework.stereotype.Repository;
import root.app.ReviewQueryOptions;
import root.database.FSQLQuery;
import root.models.Review;

import java.sql.ResultSet;
import java.util.*;

@Repository
public class ReviewRepositoryCustomImpl {
    // This class can be used to implement custom methods for the ReviewRepository if needed.

    void addVoteUp(long reviewId, int offset) throws Exception{
        String sql = "UPDATE review SET like_count = LEAST(32767, GREATEST(-32768, like_count + ?)) WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(offset)
            .bind(reviewId)
            .update();
    };

    void addVoteDown(long reviewId, int offset) throws Exception{
        String sql = "UPDATE review SET dislike_count = LEAST(32767, GREATEST(-32768, dislike_count + ?)) WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(offset)
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


    LinkedHashMap<Integer, Integer> findReviewScoreStatsByExternalId(String externalId) throws Exception{
        FSQLQuery.ResultSetConsumer<LinkedHashMap<Integer, Integer>> fnReadResultSet = (ResultSet rs) -> {
            LinkedHashMap<Integer, Integer> res = new LinkedHashMap<>();
            while (rs.next()) {
                int score = rs.getInt("score");
                int count = rs.getInt("count");
                res.put(score, count);
            }
            return res;
        };

        String sql = "SELECT score, COUNT(*) AS count FROM review WHERE ((external_id = ?) AND (status = ?)) GROUP BY score";
        return FSQLQuery.create(sql)
            .bind(externalId)
            .bind(Review.REVIEW_STATUS_APPROVED)
            .fetchCallback(fnReadResultSet);
    };

    List<String> findUniqueExternalIds() throws Exception{
        FSQLQuery.ResultSetConsumer<List<String>> fnReadResultSet = (ResultSet rs) -> {
            List<String> result = new ArrayList<>();
            int colIndex = rs.findColumn("external_id");
            while(rs.next()) {
                result.add(rs.getString(colIndex));
            }
            return result;
        };

        String sql = "SELECT DISTINCT external_id FROM review";
        return FSQLQuery.create(sql)
            .fetchCallback(fnReadResultSet);
    }

    List<Review> findByExternalIdWithPagination(String externalId, ReviewQueryOptions options) throws Exception{
        boolean DEBUG_SQL = false;

        Set<Integer> searchableReviewStatusEnums = Set.of(
            Review.REVIEW_STATUS_APPROVED,
            Review.REVIEW_STATUS_PENDING,
            Review.REVIEW_STATUS_REJECTED
        );

        String columnStr = "id, external_id, author_name, score, title, comment, like_count, dislike_count, status, created_at";

        // make condition list
        ArrayList<String> conditionExprList = new ArrayList<>();
        conditionExprList.add("(external_id = ?)");

        if(searchableReviewStatusEnums.contains(getStatusEnum(options))){
            conditionExprList.add("(status = " + options.getStatusEnum() + ")");
        }

        // make where expr-list sql
        String whereStr = String.join(" AND ", conditionExprList);

        // make limit/offset sql
        String limitOffsetSql = options.buildLimitOffsetSql();
        if(!limitOffsetSql.isEmpty()) {
            limitOffsetSql = " " + limitOffsetSql;
        }

        // make order by sql
        String orderBySql = options.buildOrderBySql();
        if(!orderBySql.isEmpty()) {
            orderBySql = " ORDER BY " + orderBySql;
        }

        // make query and execute
        String sql = "SELECT " + columnStr + " FROM review WHERE " + whereStr + orderBySql + limitOffsetSql;

        return FSQLQuery.create(sql)
            .debug(DEBUG_SQL)
            .bindIf(externalId != null, externalId)
            .fetchAll(Review.class);
    }

    List<Review> findByAnyExternalIdWithPagination(ReviewQueryOptions options) throws Exception{
        return findByExternalIdWithPagination(null, options);
    }

    private static int getStatusEnum(ReviewQueryOptions options) {
        return options.getStatusEnum();
    }

    int countByExternalIdAndStatus(String externalId, int status) throws Exception{
        String sql = "SELECT COUNT(*) FROM review WHERE (external_id = ?) AND (status = ?)";
        return (int) FSQLQuery.create(sql)
            .bind(externalId)
            .bind(status)
            .selectCount();
    }
}