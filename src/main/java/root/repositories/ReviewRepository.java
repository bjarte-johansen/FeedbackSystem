package root.repositories;

import root.includes.proxyrepo.ProxyRepository;
import root.app.ReviewQueryOptions;
import root.models.Review;

import java.util.LinkedHashMap;
import java.util.List;


/**
 * JdbcReviewRepository is a repository class that implements the review repository interface. It provides methods to
 * perform CRUD operations on reviews in the database using JDBC.
 */



public interface ReviewRepository extends ProxyRepository<Review, Long> {
    //List<Review> findByStatusAndExternalId(int status, String externalId) throws Exception;

    List<Review> findByExternalId(String externalId) throws Exception;
    List<Review> findByScoreAndExternalId(int score, String externalId) throws Exception;

    long countByExternalId(String externalId) throws Exception;

    void addVoteReport(long reviewId) throws Exception;
    void addVoteUp(long reviewId, int offset) throws Exception;
    void addVoteDown(long reviewId, int offset) throws Exception;

    void updateReviewStatus(long reviewId, int newStatus) throws Exception;

    // stats
    LinkedHashMap<Integer, Integer> findReviewScoreStatsByExternalId(String externalId, int filterScoreMin, int filterScoreMax) throws Exception;

    // find unique review ids
    List<String> findUniqueExternalIds() throws Exception;

    // main method to find reviews by external id with pagination and sorting
    //List<Review> findByExternalIdWithPagination(String externalId, Long prevId, Long nextId, int limit, int statusEnum, int orderByEnum) throws Exception;
    List<Review> findByExternalIdWithPagination(String externalId, ReviewQueryOptions options) throws Exception;
    List<Review> findAllExternalIdsWithPagination(ReviewQueryOptions options) throws Exception;

    int countByExternalIdAndStatus(String externalId, int status) throws Exception;

    default void sayHello(String msg) {
        System.out.println("hello world from ReviewRepository, msg: " + msg);
    }
}
