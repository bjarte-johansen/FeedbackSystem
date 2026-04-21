package root.interfaces;

import java.time.Instant;

/**
 * Represents a reviewer in the system.
 */

public interface IReviewer extends HasId {
    /**
     * Gets the display name of the reviewer.
     *
     * @return
     */
    String getDisplayName();

    /**
     * Sets the display name of the reviewer.
     *
     * @param displayName
     */
    void setDisplayName(String displayName);

    /**
     * Gets the email of the reviewer.
     *
     * @return
     */
    String getEmail();

    /**
     * Sets the email of the reviewer.
     *
     * @param email
     */
    void setEmail(String email);

    /**
     * Gets the password hash of the reviewer.
     *
     * @return
     */
    String getPasswordHash();

    /**
     * Sets the password hash of the reviewer.
     *
     * @param passwordHash
     */
    void setPasswordHash(String passwordHash);

    /**
     * Gets the password salt of the reviewer.
     *
     * @return
     */
    String getPasswordSalt();

    /**
     * Sets the password salt of the reviewer.
     *
     * @param passwordSalt
     */
    void setPasswordSalt(String passwordSalt);


    /**
     * Gets the timestamp when the reviewer's email was verified. This should be null if the email is not verified.
     *
     * @return
     */
    Instant getVerifiedAt();

    /**
     * Sets the timestamp when the reviewer's email was verified. This should be set to null if the email is not
     * verified.
     *
     * @param verifiedAt
     */
    void setVerifiedAt(Instant verifiedAt);


    /**
     * Gets the timestamp when the reviewer account was created. This should be set when the account is created and
     * should not be null.
     *
     * @return
     */
    Instant getCreatedAt();

    /**
     * Sets the timestamp when the reviewer account was created. This should be set when the account is created and
     * should not be null.
     *
     * @param createdAt
     */
    void setCreatedAt(Instant createdAt);
}