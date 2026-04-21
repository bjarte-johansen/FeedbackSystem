package root.models;


import root.interfaces.HasId;

import java.time.Instant;

/**
 * TODO: NOT WORKING YET
 */

public class UserVerificationRecord implements HasId {
    private Long id;
    private Long userId = 0L;
    private String verificationCode;
    private Instant expiresAt;

    public UserVerificationRecord(Long userId, String verificationCode, Instant expiresAt) {
        this.userId = userId;
        this.verificationCode = verificationCode;
        this.expiresAt = expiresAt;
    }

    // --- getters / setters ---

    public Long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
