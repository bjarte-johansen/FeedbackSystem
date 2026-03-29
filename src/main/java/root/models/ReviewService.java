package root.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.interfaces.IReviewService;
import root.repositories.ReviewRepository;

import java.util.List;
import java.util.Optional;

@Service
class ReviewService implements IReviewService {
    @Autowired
    ReviewRepository reviewRepo;


    /**
     * Constructor for IReviewServiceImpl.
     */

    private ReviewService() {
    }


    /**
     * Creates a new review.
     *
     * @param review The review to create.
     * @return The created review.
     * @throws Exception If there is an error creating the review.
     */

    @Override
    public Review create(Review review) throws Exception{
        return reviewRepo.save(review);
    }


    /**
     * Updates an existing review. The review must have a valid ID and tenant ID.
     *
     * @param review The review to update. Must have a valid ID and tenant ID.
     * @return The updated review.
     * @throws Exception If there is an error updating the review, such as if the review does not exist or if the database connection fails.
     */

    @Override
    public Review update(Review review) throws Exception {
        return reviewRepo.save(review);
    }


    /**
     * Deletes a review.
     *
     * @param review The review to delete. Must have a valid ID and tenant ID.
     * @throws Exception If there is an error deleting the review.
     */

    @Override
    public void delete(Review review) throws Exception {
        reviewRepo.delete(review);
    }


    /**
     * Finds a review by its ID and tenant ID. Both the review ID and tenant ID must be valid.
     *
     * @param reviewId The ID of the review to find. Must be a valid ID and tenant ID.
     * @return An Optional containing the found review, or an empty Optional if no review is found with the given ID and
     * tenant ID.
     * @throws Exception If there is an error finding the review, such as if the database connection fails or if the
     * query is invalid.
     */

    @Override
    public Optional<Review> findById(long reviewId) throws Exception {
        return reviewRepo.findById(reviewId).map(review -> (Review) review);
    }


    /**
     * Deletes a review by its ID and tenant ID. Both the review ID and tenant ID must be valid.
     * @param reviewId The ID of the review to delete. Must be a valid ID and tenant ID.
     * @throws Exception If there is an error deleting the review, such as if the database connection fails or if the query is invalid.
     */

    @Override
    public void deleteById(Long reviewId) throws Exception {
        reviewRepo.deleteById(reviewId);
    }


    /**
     * Finds reviews by their external ID and tenant ID. Both the external ID and tenant ID must be valid.
     * @param externalIdHash The hash of the external ID. If null, it will be generated from the external ID. Must be a valid hash or null.     *
     * @param externalId The external ID to search for. Must be a valid external ID and tenant ID.
     * @return A list of reviews that match the given external ID and tenant ID, sorted and paginated according to the provided query options. If no reviews are found, an empty list is returned.
     * @throws Exception If there is an error finding the reviews, such as if the database connection fails or if the query is invalid.
     */

    @Override
    public List<Review> findByExternalIdHashAndExternalId(Long externalIdHash, String externalId) throws Exception {
        if(externalIdHash == null){
            externalIdHash = FNV1A64HashGenerator.generate(externalId);
        }

        return reviewRepo.findByExternalIdHashAndExternalId(externalIdHash, externalId);
    }
}
