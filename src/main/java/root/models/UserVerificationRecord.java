package root.models;


import root.interfaces.IUserVerificationRecord;

import java.time.Instant;

/**
 * TODO: NOT WORKING YET
 */

public class UserVerificationRecord implements IUserVerificationRecord {

    private Long id = 0L;
    private Long userId = 0L;
    private String verificationCode;
    private Instant expiresAt;

    public UserVerificationRecord(Long userId, String verificationCode, Instant expiresAt) {
        this.userId = userId;
        this.verificationCode = verificationCode;
        this.expiresAt = expiresAt;
    }

    // --- getters / setters ---

    @Override
    public Long getId() { return id; }

    @Override
    public void setId(long id) { this.id = id; }

    @Override
    public Long getUserId() { return userId; }

    @Override
    public void setUserId(Long userId) { this.userId = userId; }

    @Override
    public String getVerificationCode() { return verificationCode; }

    @Override
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    @Override
    public Instant getExpiresAt() { return expiresAt; }

    @Override
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
