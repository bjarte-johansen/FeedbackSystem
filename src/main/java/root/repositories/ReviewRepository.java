package root.repositories;

import root.ProxyRepository;
import root.models.QueryOptions;
import root.models.Review;

import java.util.List;

/**
 * JdbcReviewRepository is a repository class that implements the review repository interface. It provides methods to
 * perform CRUD operations on reviews in the database using JDBC.
 */

public interface ReviewRepository extends ProxyRepository<Review, Long> {
    //Review save(Review review) throws Exception;
    //Review create(Review review) throws Exception;
    //Review update(Review review) throws Exception;
    //void deleteByTenantIdAndId(long tenantId, long reviewId) throws Exception;

    //Optional<Review> findById(long tenantId, long reviewId) throws Exception;

    List<Review> findByExternalIdHashAndExternalId(Long externalIdHash, String externalId) throws Exception;
    List<Review> findByAuthorIdAndExternalId(long authorId, String path) throws Exception;
    List<Review> findByScoreAndExternalId(int score, String externalId) throws Exception;

    long countByExternalIdHashAndExternalId(Long externalIdHash, String externalId) throws Exception;

    List<Review> findByTenantId(long tenantId) throws Exception;
}
