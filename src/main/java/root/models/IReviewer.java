package root.models;

import root.interfaces.HasId;

import java.time.Instant;

/**
 * Represents a reviewer in the system.
 */

public interface IReviewer extends HasId{
    Long getTenantId();
    void setTenantId(long tenantId);

    String getDisplayName();
    void setDisplayName(String displayName);

    String getEmail();
    void setEmail(String email);

    String getPasswordHash();
    void setPasswordHash(String passwordHash);

    String getPasswordSalt();
    void setPasswordSalt(String passwordSalt);

    Instant getVerifiedAt();
    void setVerifiedAt(Instant verifiedAt);

    Instant getCreatedAt();
    void setCreatedAt(Instant createdAt);
}

/*
interface IVerificationCodeGenerator {
    String generate(int digits);
}

interface IVerificationService {
    void sendVerificationNotification(IReviewer user) throws Exception;

    boolean checkVerificationCode(IReviewer user, String code) throws Exception;
}
*/

