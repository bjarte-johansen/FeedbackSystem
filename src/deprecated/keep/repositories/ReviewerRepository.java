package root.repositories;

import root.includes.proxyrepo.ProxyRepository;
import root.interfaces.IReviewer;
import root.models.Reviewer;

import java.util.Optional; /**
 * JDBC implementation of the IReviewerRepository (name subject to change) interface.
 * This class provides methods to perform CRUD operations on Reviewer entities using JDBC.
 */

public interface ReviewerRepository extends ProxyRepository<Reviewer, Long> {
    Optional<Reviewer> findByEmail(String email) throws Exception;
    Optional<Reviewer> findByReviewerName(String username) throws Exception;
}