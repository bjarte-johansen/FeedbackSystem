package root.models;

import root.includes.NorwegianTimeAgoTextFormatter;
import root.interfaces.IReview;

import java.time.Instant;


/**
 * Represents a review entity with properties such as id, author information, score, title, comment, status, and
 * timestamps. This class implements the IReview interface and provides methods to access and modify these properties.
 * <p>
 * The Review class also includes static constants for vote types and review statuses, as well as a method to convert
 * the review status to a human-readable string and a method to get a short date string indicating how long ago the
 * review was created, formatted in Norwegian.
 * <p>
 * Javadoc is provided in interface and therefore omitted here for brevity, but all methods and properties are
 * documented in the IReview interface.
 */

public class Review implements IReview {
    /**
     * Vote type constants:
     * 1 = Upvote
     * -1 = Downvote
     */
    public static int VOTE_UP = 1;
    public static int VOTE_DOWN = 2;

    /**
     * Review status constants:
     * 1 = Approved
     * 2 = Pending
     * 4 = Rejected
     * 1 | 2 | 4 = Any (used for filtering, not an actual status)
     */

    public static int REVIEW_STATUS_APPROVED = 1 << 0;
    public static int REVIEW_STATUS_PENDING = 1 << 1;
    public static int REVIEW_STATUS_REJECTED = 1 << 2;

    public static int REVIEW_STATUS_MATCH_ALL = REVIEW_STATUS_APPROVED | REVIEW_STATUS_PENDING | REVIEW_STATUS_REJECTED;

    private Long id;
    private String externalId;
    private long authorId;
    private String authorName;
    private int score;
    private String title;
    private String comment;
    private Instant createdAt;
    private int status = REVIEW_STATUS_PENDING;
    private int likeCount = 0;
    private int dislikeCount = 0;

    /**
     * Default constructor for the Review class. Initializes a new instance of the Review class with default values.
     */
    public Review() {
    }

    @Override
    public Long getId() {
        return id != null ? id : 0L;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }


    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }


    @Override
    public String getExternalId() {
        return externalId;
    }

    @Override
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public long getAuthorId() {
        return authorId;
    }

    @Override
    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    @Override
    public String getAuthorName() {
        return authorName;
    }

    @Override
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }


    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int getDislikeCount() {
        return dislikeCount;
    }

    @Override
    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    @Override
    public int getLikeCount() {
        return likeCount;
    }

    @Override
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * Returns a string representation of the Review object, including all its properties.
     *
     * @return A string representation of the Review object.
     */
    @Override
    public String toString() {
        return "Review(" +
            "id=" + id +
            ", externalId='" + externalId + '\'' +
            ", authorId=" + authorId +
            ", authorName='" + authorName + '\'' +
            ", score=" + score +
            ", title='" + title + '\'' +
            ", comment='" + comment + '\'' +
            ", status=" + statusToString(status) +
            ", likeCount=" + likeCount +
            ", dislikeCount=" + dislikeCount +
            ", createdAt=" + createdAt +
            ')';
    }


    /*

     */

    public static String statusToString(int status) {
        return switch (status) {
            case 0 -> "PENDING";
            case 1 -> "APPROVED";
            case 2 -> "REJECTED";
            default -> "UNKNOWN";
        };
    }

    /**
     * Returns a human-readable string representing how long ago the review was created, formatted in Norwegian.
     *
     * @return A string like "for 5 minutter siden" indicating how long ago the review was created.
     */

    public String getShortDateString() {
        return NorwegianTimeAgoTextFormatter.formatInstantAgo(createdAt, "for ", " siden");
    }
}