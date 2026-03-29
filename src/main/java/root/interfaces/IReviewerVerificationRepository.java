package root.interfaces;

import java.util.Optional;

public interface IReviewerVerificationRepository {
    IUserVerificationRecord save(IUserVerificationRecord record) throws Exception;
    Optional<IUserVerificationRecord> findByUserId(Long userId) throws Exception;
}
