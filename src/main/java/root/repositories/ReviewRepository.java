package root.repositories;

import root.ProxyRepository;
import root.models.Review;

import java.util.LinkedHashMap;
import java.util.List;


/**
 * JdbcReviewRepository is a repository class that implements the review repository interface. It provides methods to
 * perform CRUD operations on reviews in the database using JDBC.
 */



public interface ReviewRepository extends ProxyRepository<Review, Long> {
    // Review save(Review review) throws Exception;
    // Review create(Review review) throws Exception;
    // Review update(Review review) throws Exception;
    // void deleteByTenantIdAndId(long tenantId, long reviewId) throws Exception;
    // Optional<Review> findById(long tenantId, long reviewId) throws Exception;

    List<Review> findByStatusAndExternalId(int status, String externalId) throws Exception;

    List<Review> findByExternalId(String externalId) throws Exception;
    List<Review> findByScoreAndExternalId(int score, String externalId) throws Exception;

    long countByExternalId(String externalId) throws Exception;

    void updateReviewLikeCount(long reviewId, int likeCount) throws Exception;
    void updateReviewDislikeCount(long reviewId, int dislikeCount) throws Exception;
    void updateReviewStatus(long reviewId, int newStatus) throws Exception;

    LinkedHashMap<Integer, Integer> findReviewScoreStatsByExternalId(String externalId) throws Exception;
    List<String> findUniqueExternalIds() throws Exception;
    List<Review> findByExternalIdWithPagination(String externalId, Long prevId, Long nextId, int limit, String orderBy) throws Exception;
    int countByExternalIdAndStatus(String externalId, int status) throws Exception;
}
