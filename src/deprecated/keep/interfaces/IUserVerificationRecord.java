package root.interfaces;

import java.time.Instant;


/**
 * Represents a user verification record, which contains information about a user's verification code and its expiration time.
 * TODO: Any code inserted here must have javadoc comments. This is a requirement for all code in this project.
 */

public interface IUserVerificationRecord extends HasId {
    /**
     * Gets the user ID associated with this verification record.
     * @return the user ID
     */
    Long getUserId();

    /**
     * Sets the user ID associated with this verification record.
     * @param userId
     */
    void setUserId(Long userId);

    /**
     * Gets the verification code associated with this record.
     * @return the verification code
     */
    String getVerificationCode();

    /**
     * Sets the verification code associated with this record.
     * @param verificationCode
     */
    void setVerificationCode(String verificationCode);

    /**
     * Gets the expiration time of this verification record.
     * @return the expiration time
     */
    Instant getExpiresAt();

    /**
     * Sets the expiration time of this verification record.
     * @param expiresAt
     */
    void setExpiresAt(Instant expiresAt);
}