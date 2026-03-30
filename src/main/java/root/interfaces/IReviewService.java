package root.interfaces;

import root.models.Review;

import java.util.List;
import java.util.Optional;

public interface IReviewService {

    /**
     * Saves the review to the database. If the review has an ID, it updates the existing review; otherwise, it creates a new review.
     *
     * @param review The review to save. If the review has an ID, it will be updated; if it does not have an ID, a new review will be created.
     * @throws Exception if there is an error during the save operation, such as a database error or validation error. The specific exceptions thrown may depend on the implementation of the save method and the underlying data access layer.
     */

    default void save(Review review) throws Exception {
        // DEBUG: we match against 0, not null ?!
        if(review.getId() == null) {
            create(review);
        } else {
            update(review);
        }
    }


    /**
     * Creates a new review in the database. The review must not have an ID, and must have a valid tenant ID.
     *
     * @param review The review to create. Must not have an ID, and must have a valid tenant ID.
     * @return The created review with its ID populated.
     */

    Review create(Review review) throws Exception;


    /**
     * Updates the specified review in the database. The review must have a valid ID and tenant ID.
     *
     * @param review The review to update. Must have a valid ID and tenant ID.
     * @return The updated review.
     */

    Review update(Review review) throws Exception;


    /**
     * Deletes the specified review from the database.
     *
     * @param review The review to delete. Must have a valid ID and tenant ID.
     */

    void delete(Review review) throws Exception;


    /**
     * Finds a review by its ID and tenant ID.
     *
     * @param reviewId The ID of the review to find. Must be a valid ID and tenant ID.
     * @return An Optional containing the found review, or an empty Optional if no review was found with the specified
     * ID and tenant ID.
     */

    Optional<Review> findById(long reviewId) throws Exception;


    /**
     * Deletes a review by its ID and tenant ID.
     *
     * @param reviewId The ID of the review to delete. Must be a valid ID and tenant ID.
     */

    void deleteById(Long reviewId) throws Exception;


    /**
     * Finds reviews by external ID and tenant ID, with pagination and sorting options.
     *
     * @param externalId The external ID to search for. Must be a valid external ID and tenant ID.
     * @return A list of reviews matching the specified external ID and tenant ID, according to the provided query
     * options. The list may be empty if no reviews were found.
     */

    List<Review> findByExternalId(String externalId) throws Exception;
}
