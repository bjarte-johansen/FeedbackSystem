package root.models

import root.database.HasId
import java.sql.ResultSet
import java.time.Instant
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

class Review: IReview, HasId {
    override var id: Long? = null;
    override var tenantId: Long? = null;
    override var externalId: String = ""
        set(value) {
            externalIdHash = FNV1A64HashGenerator.generate(value);
            field = value;
        }
    override var externalIdHash: Long = 0
    override var authorId: Long = 0
    override var authorName: String = "";
    override var score: Int = 0
    override var comment: String = ""
    override var createdAt: Instant = Instant.now()
    //override var attributes: Map<String, Any> = emptyMap()

    constructor() {
    }

    constructor(
        id: Long? = null,
        tenantId: Long? = null,
        externalId: String = "",
        externalIdHash: Long = 0,
        authorId: Long = 0,
        authorName: String = "",
        score: Int = 0,
        comment: String = "",
        createdAt: Instant = Instant.now(),
        //attributes: LinkedHashMap<String, Any> = linkedMapOf()
    ) {
        this.id = id
        this.tenantId = tenantId
        this.externalId = externalId
        this.externalIdHash = externalIdHash
        this.authorId = authorId
        this.authorName = authorName
        this.score = score
        this.comment = comment
        this.createdAt = createdAt
        //this.attributes = attributes
    }

    @Override
    override fun toString(): String {
        return "Review(id=$id, tenantId=$tenantId, externalId='$externalId', externalIdHash=$externalIdHash, authorId=$authorId, authorName='$authorName', score=$score, comment='$comment', createdAt=$createdAt, " /*attributes=$attributes*/ + ")";
    }

    override fun getId(): Long {
        return this.id ?: 0;
    }

    override fun setId(id: Long) {
        this.id = id;
    }
};