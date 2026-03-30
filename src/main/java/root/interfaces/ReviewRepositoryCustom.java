package root.interfaces;

import root.models.IReview;
import root.models.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepositoryCustom {

    /*
     * Counts the number of reviews with the specified external ID and tenant ID.
     */

    long countByExternalId(String externalId) throws Exception;

    List<Review> findByAuthorIdAndExternalId(long authorId, String externalId) throws Exception;
    List<Review> findByScoreAndExternalId(int score, String externalId) throws Exception;
}
