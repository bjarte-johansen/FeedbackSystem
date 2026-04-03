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
            .bind(1, offset)
            .bind(2, reviewId)
            .update();
    };

    void updateReviewDislikeCount(long reviewId, int offset) throws Exception{
        String sql = "UPDATE review SET dislikeCount = LEAST(32767, GREATEST(-32768, dislikeCount + ?)) WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(1, offset)
            .bind(2, reviewId)
            .update();
    };

    void updateReviewStatus(long reviewId, int newStatus) throws Exception{
        String sql = "UPDATE review SET status = ? WHERE id = ?";

        FSQLQuery.create(sql)
            .bind(newStatus)
            .bind(reviewId)
            .update();
    };

    @FunctionalInterface
    interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }
    static <T, R> Function<T, R> wrap(ThrowingFunction<T, R> fn) {
        return t -> {
            try {
                return fn.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    LinkedHashMap<Integer, Integer> findReviewScoreStatsByExternalId(String externalId) throws Exception{
        Function<ResultSet, LinkedHashMap<Integer, Integer>> fnReadResultSet = (ResultSet rs) -> {
            try {
                LinkedHashMap<Integer, Integer> res = new LinkedHashMap<>();
                while (rs.next()) {
                    int score = rs.getInt("score");
                    int count = rs.getInt("count");
                    res.put(score, count);
                }
                return res;
            }catch(Exception e) {
                throw new RuntimeException(e);
            }
        };

        String sql = "SELECT score, COUNT(*) AS count FROM review WHERE ((external_id = ?) AND (status = ?)) GROUP BY score";
        return FSQLQuery.create(sql)
            .bind(externalId)
            .bind(Review.REVIEW_STATUS_APPROVED)
            .debug()
            .fetchCallback(fnReadResultSet);
    };

    List<String> findUniqueExternalIds() throws Exception{
        String sql = "SELECT DISTINCT external_id FROM review";

        return FSQLQuery.create(sql)
            .fetchCallback(rs -> {
                try {
                    List<String> result = new ArrayList<>();
                    while(rs.next()) {
                        result.add(rs.getString("external_id"));
                    }
                    return result;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }
}