package root.repositories;

import root.ProxyRepository;
import root.models.IReviewer;
import root.models.Reviewer;

import java.util.Optional; /**
 * JDBC implementation of the IReviewerRepository (name subject to change) interface.
 * This class provides methods to perform CRUD operations on Reviewer entities using JDBC.
 */

public interface ReviewerRepository extends ProxyRepository<Reviewer, Long> {
/*
    IReviewer create(IReviewer reviewer) throws Exception;
    IReviewer update(IReviewer reviewer) throws Exception;

    Optional<IReviewer> findById(long reviewerId) throws Exception;

 */
    Optional<IReviewer> findByReviewerName(long tenantId, String username) throws Exception;
    Optional<IReviewer> findByEmail(long tenantId, String email) throws Exception;

    //int delete(IReviewer reviewer) throws Exception;
    int deleteById(long tenantId, long reviewerId) throws Exception;
}
