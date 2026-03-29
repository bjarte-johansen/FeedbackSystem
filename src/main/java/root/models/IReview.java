package root.models;

import root.interfaces.HasId;

import java.time.Instant; /***********************************************/

public interface IReview extends HasId {
    Long getTenantId();
    void setTenantId(Long tenantId);

    String getExternalId();
    void setExternalId(String externalId);

    long getExternalIdHash();
    void setExternalIdHash(long externalIdHash);

    long getAuthorId();
    void setAuthorId(long authorId);

    String getAuthorName();
    void setAuthorName(String authorName);

    int getScore();
    void setScore(int score);

    String getTitle();
    void setTitle(String title);

    String getComment();
    void setComment(String comment);

    Instant getCreatedAt();
    void setCreatedAt(Instant createdAt);

    //Map<String, Object> getAttributes();
    //void setAttributes(Map<String, Object> attributes);
}
