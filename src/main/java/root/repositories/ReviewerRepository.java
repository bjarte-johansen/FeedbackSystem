package root.repositories;

import root.ProxyRepository;
import root.models.IReviewer;
import root.models.Reviewer;

import java.util.Optional; /**
 * JDBC implementation of the IReviewerRepository (name subject to change) interface.
 * This class provides methods to perform CRUD operations on Reviewer entities using JDBC.
 */

public interface ReviewerRepository extends ProxyRepository<Reviewer, Long> {
    Optional<IReviewer> findByReviewerName(long tenantId, String username) throws Exception;
    Optional<IReviewer> findByEmail(long tenantId, String email) throws Exception;

    int deleteById(long tenantId, long reviewerId) throws Exception;
}