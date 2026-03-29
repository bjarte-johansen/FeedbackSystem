package root.interfaces;

import java.time.Instant;

public interface IUserVerificationRecord extends HasId {
    Long getUserId();
    void setUserId(Long userId);

    String getVerificationCode();
    void setVerificationCode(String verificationCode);

    Instant getExpiresAt();
    void setExpiresAt(Instant expiresAt);
}