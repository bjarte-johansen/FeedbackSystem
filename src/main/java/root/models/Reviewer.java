package root.models;

import root.interfaces.IReviewer;

import java.time.Instant;

/**
 * The Reviewer class represents a user who can submit reviews. It implements the IReviewer interface,
 * which defines the contract for reviewer entities in the system. This class contains fields for
 * storing reviewer information such as email, display name, password hash, and timestamps for
 * account creation and verification.
 *
 * JavaDoc comments are written in interface and omitted here for brevity.
 *
 * TODO: Any code inserted here must have javadoc comments. This is a requirement for all code in this project.
 */

public class Reviewer implements IReviewer {

    //private Long tenantId = 0L;
    private Long id = 0L;
    private String email = "";
    private String displayName = "";
    private String passwordHash = "";
    private String passwordSalt = "";
    private Instant createdAt = Instant.now();
    private Instant verifiedAt = null;

    /**
     * Default constructor for the Reviewer class. Initializes all fields to their default values.
     */
    public Reviewer() {}
    /*
    public Reviewer(
    String email,
    String displayName,
    String passwordHash,
    String passwordSalt,
    Instant createdAt,
    Instant verifiedAt
    ) {
        this.email = email;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.verifiedAt = verifiedAt;
    }
     */

    // --- getters / setters ---

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

    @Override
    public String toString() {
        return "Reviewer{" +
            "id=" + id +
            ", email='" + email + '\'' +
            ", displayName='" + displayName + '\'' +
            ", passwordHash='" + passwordHash + '\'' +
            ", passwordSalt='" + passwordSalt + '\'' +
            ", createdAt=" + createdAt +
            ", verifiedAt=" + verifiedAt +
            '}';
    }
}