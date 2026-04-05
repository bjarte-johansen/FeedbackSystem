package root.interfaces;

import java.time.Instant;

/***********************************************/

public interface IReview extends HasId {
    /**
     * Returns the status of this review. The status is an integer value that represents the current state of the review,
     * such as whether it is pending, approved, or rejected.
     *
     * @return the status of this review
     */
    int getStatus();

    /**
     * Sets the status for this review. The status is an integer value that represents the current state of the review,
     * such as whether it is pending, approved, or rejected.
     *
     * @param status
     */
    void setStatus(int status);

    /**
     * Returns the external ID for this review. The external ID is a string that can be used to associate the review
     * with an external resource, such as a product, service, or third-party application.
     *
     * @return
     */
    String getExternalId();

    /**
     * Sets the external ID for this review. The external ID is a string that can be used to associate the review with
     * an external resource, such as a product, service, or third-party application.
     *
     * @param externalId
     */
    void setExternalId(String externalId);

    /**
     * Returns the ID of the author of this review. The author ID is a long value that represents the unique identifier
     * of the person who wrote the review. It can be used to associate the review with a specific user or account in a
     * system, and to track the reviewer's activity and contributions over time.
     *
     * @return
     */
    long getAuthorId();

    /**
     * Sets the ID of the author of this review. The author ID is a long value that represents the unique identifier of
     * the person who wrote the review. It can be used to associate the review with a specific user or account in a
     * system, and to track the reviewer's activity and contributions over time.
     *
     * @param authorId
     */
    void setAuthorId(long authorId);

    /**
     * Returns the name of the author of this review. The author name is a string that represents the name of the person
     * who wrote the review. It can be used to identify the reviewer and to provide context for the review.
     *
     * @return
     */
    String getAuthorName();

    /**
     * Sets the name of the author of this review. The author name is a string that represents the name of the person
     * who wrote the review. It can be used to identify the reviewer and to provide context for the review.
     *
     * @param authorName
     */
    void setAuthorName(String authorName);

    /**
     * Returns the score for this review. The score is an integer value that represents the rating given in the review.
     *
     * @return
     */
    int getScore();

    /**
     * Sets the score for this review. The score should be an integer value that represents the rating given in the
     * review. The specific range of valid scores may depend on the context in which the review is being used, but
     * common ranges include 1-5 or 1-10.
     *
     * @param score
     */
    void setScore(int score);

    /**
     * Returns the title of this review. The title is a string that provides a brief summary of the review. It can be
     * used to give potential readers an idea of what the review is about and to attract their attention.
     *
     * @return the title of this review
     */
    String getTitle();

    /**
     * Sets the title for this review. The title should be a string that provides a brief summary of the review. It can
     * be used to give potential readers an idea of what the review is about and to attract their attention.
     *
     * @param title the title to set for this review
     */
    void setTitle(String title);

    /**
     * Returns the comment for this review. The comment is a string that provides additional information about the
     * review. It can be used to explain the rating given in the review or to provide feedback on the product or service
     * being reviewed.
     *
     * @return
     */
    String getComment();

    /**
     * Sets the comment for this review. The comment should be a string that provides additional information about the
     * review. It can be used to explain the rating given in the review or to provide feedback on the product or service
     * being reviewed.
     *
     * @param comment the comment to set for this review
     */
    void setComment(String comment);

    /**
     * Returns the creation timestamp of this review.
     *
     * @return the creation timestamp
     */
    Instant getCreatedAt();

    /**
     * Sets the creation timestamp for this review.
     *
     * @param createdAt
     */
    void setCreatedAt(Instant createdAt);

    /**
     * Returns the number of dislikes this review has received.
     *
     * @return the dislike count
     */
    int getDislikeCount();

    /**
     * Sets the number of dislikes for this review.
     *
     * @param dislikeCount the dislike count
     */
    void setDislikeCount(int dislikeCount);

    /**
     * Returns the number of likes this review has received.
     *
     * @return the like count
     */
    int getLikeCount();

    /**
     * Sets the number of likes for this review.
     *
     * @param likeCount the like count
     */
    void setLikeCount(int likeCount);

    //Map<String, Object> getAttributes();
    //void setAttributes(Map<String, Object> attributes);
}
