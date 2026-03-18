package root.models


import root.database.HasId
import java.time.Instant


class Reviewer : IReviewer {
    constructor(){}

    constructor(
        tenantId: Long,
        email: String,
        displayName: String,
        passwordHash: String,
        passwordSalt: String,
        createdAt: Instant = Instant.now(),
        verifiedAt: Instant? = null
    ) {
        this.tenantId = tenantId
        this.email = email
        this.displayName = displayName
        this.passwordHash = passwordHash
        this.passwordSalt = passwordSalt
        this.createdAt = createdAt
        this.verifiedAt = verifiedAt
    }

    override var tenantId: Long = 0;
    override var id: Long = 0
    override var email: String = "";
    override var displayName: String = "";
    override var passwordHash: String = "";
    override var passwordSalt: String = "";
    override var createdAt: Instant? = Instant.now();
    override var verifiedAt: Instant? = Instant.now();
};