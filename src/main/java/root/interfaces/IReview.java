package root.interfaces;

import java.time.Instant; /***********************************************/

public interface IReview extends HasId {
    int getStatus();
    void setStatus(int status);

    // external resource ID (e.g., from a third-party app/website, any string is allowed)
    String getExternalId();
    void setExternalId(String externalId);

    // reviewer id
    long getAuthorId();
    void setAuthorId(long authorId);

    // reviewer name
    String getAuthorName();
    void setAuthorName(String authorName);

    // review score (1-5, scalars allowed to make it more flexible, e.g., 4.5 = 45 with division on 10 on client side)
    int getScore();
    void setScore(int score);

    // review title
    String getTitle();
    void setTitle(String title);

    // review comment
    String getComment();
    void setComment(String comment);

    // review creation timestamp
    Instant getCreatedAt();
    void setCreatedAt(Instant createdAt);

    int getDislikeCount();
    void setDislikeCount(int dislikeCount);

    int getLikeCount();
    void setLikeCount(int likeCount);

    //Map<String, Object> getAttributes();
    //void setAttributes(Map<String, Object> attributes);
}
