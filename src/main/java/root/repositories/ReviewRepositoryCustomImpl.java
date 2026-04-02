package root.repositories;

import org.springframework.stereotype.Repository;
import root.database.FSQLQuery;

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
}