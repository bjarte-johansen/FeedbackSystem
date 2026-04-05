package root.interfaces;

import root.models.UserVerificationRecord;


/**
 * Interface for user verification services. This service is responsible for handling the logic related to user verification,
 * such as sending verification notifications, checking verification codes, and marking users as fully verified.
 * TODO: Any code inserted here must have javadoc comments. This is a requirement for all code in this project.
 */
public interface IUserVerificationService {

    /**
     * Returns the duration in seconds for which a verification code is valid before it expires.
     * @return the expiration duration in seconds
     * @throws Exception
     */
    Long getExpirationDurationSeconds() throws Exception;

    /**
     * Creates a new verification record for the specified user and sends a notification to the user with the verification code.
     * @param userId
     * @throws Exception
     */
    void sendVerificationNotification(Long userId) throws Exception;

    /**
     * Checks if the provided code matches the code in the verification record and is not expired.
     * @param verificationRecord
     * @param code
     * @return true if the code is correct and not expired, false otherwise
     * @throws Exception
     */
    Boolean checkVerificationCode(UserVerificationRecord verificationRecord, String code) throws Exception;

    /**
     * Tries to verify the provided code against the verification record. If the code is correct and not expired, it marks the user as fully verified.
     * @param verificationRecord
     * @param code
     * @return true if verification is successful, false otherwise
     * @throws Exception
     */
    Boolean tryVerificationCode(UserVerificationRecord verificationRecord, String code) throws Exception;

    /**
     * Marks the user as fully verified by setting the verifiedAt timestamp to the current time.
     * @param userId
     * @throws Exception
     */
    void setUserAsFullyVerified(Long userId) throws Exception;
}