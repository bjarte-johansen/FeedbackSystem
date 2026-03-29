package root.interfaces;

import root.models.IReview;
import root.models.QueryOptions;
import root.models.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepositoryCustom {

    /**
     * Saves the review to the database. If the review has an ID, it updates the existing review; otherwise, it creates a new review.
     *
     * @param review
     * @return
     */

    IReview save(IReview review) throws Exception;


    /**
     * Create the review in the database.
     *
     * @param review
     * @return
     */

    IReview create(IReview review) throws Exception;


    /**
     * Update the review in the database. The review must have a valid ID and tenant ID.
     *
     * @param review
     * @return
     */

    IReview update(IReview review) throws Exception;


    /**
     * Finds a review by its ID and tenant ID.
     *
     * @param tenantId
     * @param reviewId
     * @return
     */

    Optional<Review> findById(long tenantId, long reviewId) throws Exception;


    /**
     * Finds reviews by resource ID and tenant ID, with pagination and sorting options.
     *
     * @param tenantId
     * @param externalId
     * @param queryOptions
     * @return
     */

    List<IReview> findByExternalId(long tenantId, String externalId, Long externalIdHash, QueryOptions queryOptions) throws Exception;


    /**
     * Deletes the specified review from the database.
     *
     * @param review
     */

    void delete(IReview review) throws Exception;


    /**
     * Deletes the specified review from the database.
     *
     * @param entities The entities to delete
     */

    <T> void deleteAll(Iterable<? extends T> entities) throws Exception;


    /**
     * Deletes a review by its ID and tenant ID.
     *
     * @param tenantId
     * @param reviewId
     */

    void deleteById(Long tenantId, Long reviewId) throws Exception;

    /**
     * Counts the total number of reviews in the database.
     *
     * @return The total count of reviews.
     */

    long count() throws Exception;


    /*
     * Counts the number of reviews with the specified external ID and tenant ID.
     */

    long countByExternalId(long tenantId, String externalId, Long externalIdHash) throws Exception;

    List<Review> findByAuthorIdAndExternalId(long authorId, String externalId, QueryOptions queryOptions) throws Exception;
    List<Review> findByAuthorIdAndExternalId(long authorId, String externalId) throws Exception;
    List<Review> findByScoreAndExternalId(int score, String externalId) throws Exception;
    List<Review> findAll() throws Exception;

    List<Review> findByTenantId(long tenantId) throws Exception;
}
