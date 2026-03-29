package root.interfaces;

import root.models.UserVerificationRecord;

public interface IUserVerificationService {

    Long getExpirationDurationSeconds() throws Exception;

    void sendVerificationNotification(Long userId) throws Exception;

    Boolean checkVerificationCode(UserVerificationRecord verificationRecord, String code) throws Exception;

    Boolean tryVerificationCode(UserVerificationRecord verificationRecord, String code) throws Exception;

    void setUserAsFullyVerified(Long userId) throws Exception;
}