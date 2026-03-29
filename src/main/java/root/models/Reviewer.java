package root.models;

import java.time.Instant;

public class Reviewer implements IReviewer {

    private Long tenantId = 0L;
    private Long id = 0L;
    private String email = "";
    private String displayName = "";
    private String passwordHash = "";
    private String passwordSalt = "";
    private Instant createdAt = Instant.now();
    private Instant verifiedAt = null;

    public Reviewer() {}

    public Reviewer(
            Long tenantId,
    String email,
    String displayName,
    String passwordHash,
    String passwordSalt,
    Instant createdAt,
    Instant verifiedAt
    ) {
        this.tenantId = tenantId;
        this.email = email;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.verifiedAt = verifiedAt;
    }

    // --- getters / setters ---

    @Override
    public Long getTenantId() { return tenantId; }

    @Override
    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public Long getId() { return id; }

    @Override
    public void setId(long id) { this.id = id; }

    @Override
    public String getEmail() { return email; }

    @Override
    public void setEmail(String email) { this.email = email; }

    @Override
    public String getDisplayName() { return displayName; }

    @Override
    public void setDisplayName(String displayName) { this.displayName = displayName; }



    @Override
    public String getPasswordHash() { return passwordHash; }

    @Override
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    @Override
    public String getPasswordSalt() { return passwordSalt; }

    @Override
    public void setPasswordSalt(String passwordSalt) { this.passwordSalt = passwordSalt; }

    @Override
    public Instant getCreatedAt() { return createdAt; }

    @Override
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override
    public Instant getVerifiedAt() { return verifiedAt; }

    @Override
    public void setVerifiedAt(Instant verifiedAt) { this.verifiedAt = verifiedAt; }
}