package root.interfaces;

import java.time.Instant;

/**
 * Represents a reviewer in the system.
 */

public interface IReviewer extends HasId{
    // display name is the name shown to other users, while username is used for login and must be unique
    String getDisplayName();
    void setDisplayName(String displayName);

    // email is used for login and verification, and must be unique
    String getEmail();
    void setEmail(String email);

    // passwordHash is the hashed version of the user's password, and passwordSalt is the random salt used for hashing
    String getPasswordHash();
    void setPasswordHash(String passwordHash);

    // passwordSalt is the random salt used for hashing the user's password, and should be stored separately
    String getPasswordSalt();
    void setPasswordSalt(String passwordSalt);

    // verifiedAt is the timestamp when the user's email was verified, and should be null if not verified
    Instant getVerifiedAt();
    void setVerifiedAt(Instant verifiedAt);

    // createdAt is the timestamp when the user account was created, and should be set when the account is created
    Instant getCreatedAt();
    void setCreatedAt(Instant createdAt);
}