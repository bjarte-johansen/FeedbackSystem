package root.repositories;

import root.includes.proxyrepo.ProxyRepository;
import root.app.ReviewQueryOptions;
import root.models.Review;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


/**
 * JdbcReviewRepository is a repository class that implements the review repository interface. It provides methods to
 * perform CRUD operations on reviews in the database using JDBC.
 */



public interface ReviewRepository extends ProxyRepository<Review, Long>, ReviewRepositoryInterface{
    List<Review> findByExternalId(String externalId) throws Exception;
    long countByExternalId(String externalId) throws Exception;

    //void addVoteReport(long reviewId) throws Exception;
    //void incrementLikeVote(long reviewId, int delta) throws Exception;
    //void incrementDislikeVote(long reviewId, int delta) throws Exception;

    //void updateReviewStatus(long reviewId, int newStatus) throws Exception;


    //void addVote(long reviewId, int voteId, int offset, String sessionId, String ipAddress) throws Exception;


    // stats
    //LinkedHashMap<Integer, Integer> findReviewScoreStatsByExternalId(String externalId, Set<Integer> scoreFilterSet) throws Exception;

    // find unique review ids
    List<String> findUniqueExternalIds() throws Exception;

    // find by external id, with query options (pagination, sorting, filtering, etc.)
    //List<Review> findByExternalIdWithPagination(String externalId, ReviewQueryOptions options) throws Exception;

    // find without external id, with query options (pagination, sorting, filtering, etc.)
    List<Review> findAllExternalIdsWithPagination(ReviewQueryOptions options) throws Exception;

    int countByExternalId(String externalId, ReviewQueryOptions options) throws Exception;

}
