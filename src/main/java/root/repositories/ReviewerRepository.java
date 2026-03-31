package root.repositories;

import root.ProxyRepository;
import root.interfaces.IReviewer;
import root.models.Reviewer;

import java.util.Optional; /**
 * JDBC implementation of the IReviewerRepository (name subject to change) interface.
 * This class provides methods to perform CRUD operations on Reviewer entities using JDBC.
 */

public interface ReviewerRepository extends ProxyRepository<Reviewer, Long> {
    Optional<IReviewer> findByEmail(String email) throws Exception;
    Optional<IReviewer> findByReviewerName(String username) throws Exception;

    //int deleteById(long reviewerId) throws Exception;
}