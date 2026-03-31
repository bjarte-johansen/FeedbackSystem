package root.interfaces;

import root.models.Review;
import java.util.List;

public interface ReviewRepositoryCustom {

    /*
     * Counts the number of reviews with the specified external ID
     */

    long countByExternalId(String externalId) throws Exception;


    /**
     * Finds reviews by author ID and externalId
     *
     * @param authorId The ID of the author of the reviews to find.
     * @param externalId The external ID to search for. Must be a valid external ID
     * @return A list of reviews that match the given author ID, external ID and tenant ID, sorted and paginated according
     */

    List<Review> findByAuthorIdAndExternalId(long authorId, String externalId) throws Exception;


    /**
     * Finds reviews by score and external ID.
     * @param score
     * @param externalId
     * @return
     * @throws Exception
     */

    List<Review> findByScoreAndExternalId(int score, String externalId) throws Exception;
}
