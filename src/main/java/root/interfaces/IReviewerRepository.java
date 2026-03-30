package root.interfaces;

import root.models.IReviewer;

import java.util.Optional;

public interface IReviewerRepository {
    //IReviewer create(IReviewer reviewer) throws Exception;

    //IReviewer update(IReviewer reviewer) throws Exception;

    //Optional<IReviewer> findById(long id) throws Exception;

    Optional<IReviewer> findByReviewerName(String username) throws Exception;

    Optional<IReviewer> findByEmail(String email) throws Exception;

    //int delete(IReviewer reviewer) throws Exception;

    //int deleteById(long id) throws Exception;
}
