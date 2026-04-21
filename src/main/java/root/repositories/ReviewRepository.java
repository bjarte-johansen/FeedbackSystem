package root.repositories;

import root.includes.proxyrepo.ProxyRepository;
import root.app.ReviewQueryOptions;
import root.models.Review;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * JdbcReviewRepository is a repository class that implements the review repository interface. It provides methods to
 * perform CRUD operations on reviews in the database using JDBC.
 */



public interface ReviewRepository extends ProxyRepository<Review, Long>, ReviewRepositoryInterface{
    List<Review> findByExternalId(String externalId);
    long countByExternalId(String externalId);

    //Optional<Review> findFirstByExternalIdNotEquals(String externalId);

    // find unique review ids
    List<String> findDistinctExternalIdByExternalId();

    // find without external id, with query options (pagination, sorting, filtering, etc.)
    List<Review> findAllExternalIdsWithPagination(ReviewQueryOptions options);

    int countByExternalId(String externalId, ReviewQueryOptions options);
    int countByExternalIdAndStatus(String externalId, int status);

}
