package root.interfaces;

import root.ProxyRepository;
import root.models.UserVerificationRecord;

import java.util.Optional;

public interface IReviewerVerificationRepository extends ProxyRepository<UserVerificationRecord, Long> {
    //UserVerificationRecord save(UserVerificationRecord record) throws Exception;
    Optional<UserVerificationRecord> findByUserId(Long userId) throws Exception;
}
