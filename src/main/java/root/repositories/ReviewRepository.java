package root.repositories;

import root.ProxyRepository;
import root.models.IReview;
import root.models.QueryOptions;
import root.models.Review;

import java.util.List;
import java.util.Optional;

/**
 * JdbcReviewRepository is a repository class that implements the review repository interface. It provides methods to
 * perform CRUD operations on reviews in the database using JDBC.
 */

public interface ReviewRepository extends ProxyRepository<Review, Long> {
    IReview save(IReview review) throws Exception;
    IReview create(IReview review) throws Exception;

    IReview update(IReview review) throws Exception;

    void deleteByTenantIdAndReviewId(long tenantId, long reviewId) throws Exception;

    Optional<Review> findById(long tenantId, long reviewId) throws Exception;

    List<IReview> findByExternalId(long tenantId, String externalId, Long externalIdHash, QueryOptions queryOptions) throws Exception;
    List<Review> findByAuthorIdAndExternalId(long authorId, String path, QueryOptions queryOptions) throws Exception;
    List<Review> findByAuthorIdAndExternalId(long authorId, String path) throws Exception;
    List<Review> findByScoreAndExternalId(int score, String externalId) throws Exception;

    long countByTenantIdAndExternalId(long tenantId, String externalId, Long externalIdHash) throws Exception;

    List<Review> findByTenantId(long tenantId) throws Exception;
}
