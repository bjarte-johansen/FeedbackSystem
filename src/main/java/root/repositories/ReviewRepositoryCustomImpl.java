package root.repositories;

import org.springframework.stereotype.Repository;
import root.app.AppRequestContext;
import root.database.FSQLQuery;
import root.models.Review;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

@Repository
public class ReviewRepositoryCustomImpl {
    // This class can be used to implement custom methods for the ReviewRepository if needed.

    void updateReviewLikeCount(long reviewId, int offset) throws Exception{
        String sql = "UPDATE review SET likeCount = LEAST(32767, GREATEST(-32768, likeCount + ?)) WHERE id = ?";
        
        FSQLQuery.create(sql)
            .bind(offset)
            .bind(reviewId)
            .update();
    };

    void updateReviewDislikeCount(long reviewId, int offset) throws Exception{
        String sql = "UPDATE review SET dislikeCount = LEAST(32767, GREATEST(-32768, dislikeCount + ?)) WHERE id = ?";

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


    List<Review> findByExternalIdWithPagination(String externalId, Long prevId, Long nextId, int limit, String orderBy) throws Exception{
        boolean DEBUG_SQL = false;
        String columns = "id, external_id, author_name, score, title, comment, like_count, dislike_count, status, created_at";

        if(prevId != null){
            long bindingId = (prevId != null) ? (long) prevId : 0;

            String fmt = "SELECT %s FROM"
                + " (SELECT %s FROM review WHERE (external_id = ?) AND (id <= ?) ORDER BY id DESC LIMIT ?)"
                + " ORDER BY id ASC";
            String sql = String.format(fmt, columns, columns);

            return FSQLQuery.create(sql)
                .bind(externalId)
                .bind(bindingId)
                .bind(limit)
                .debug(DEBUG_SQL)
                .fetchAll(Review.class);
        }else {

            long bindingId = (nextId != null) ? (long) nextId : 0;

            String fmt = "SELECT %s FROM review WHERE (external_id = ?) AND (id >= ?) ORDER BY id ASC LIMIT ?";
            String sql = String.format(fmt, columns);

            return FSQLQuery.create(sql)
                .bind(externalId)
                .bind(bindingId)
                .bind(limit)
                .debug(DEBUG_SQL)
                .fetchAll(Review.class);
        }
    };

    int countByExternalIdAndStatus(String externalId, int status) throws Exception{
        String sql = "SELECT COUNT(*) FROM review WHERE (external_id = ?) AND (status = ?)";
        return (int) FSQLQuery.create(sql)
            .bind(externalId)
            .bind(status)
            .selectCount();
    }
}