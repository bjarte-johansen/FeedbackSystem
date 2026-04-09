package root.models;

import root.includes.NorwegianTimeAgoTextFormatter;
import root.interfaces.IReview;

import java.time.Instant;
import java.util.Set;


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
     * Important: remember to update validVoteTypes set if you add new vote types
     */
    public static int VOTE_UP = 1;
    public static int VOTE_DOWN = 2;

    private static final Set<Integer> validVoteTypes = Set.of(VOTE_UP, VOTE_DOWN);


    /**
     * Review status constants: 1 = Approved 2 = Pending 3 = Rejected
     * Important: remember to update validReviewStatuses set if you add new vote types     *
     */

    public static final int REVIEW_STATUS_APPROVED = 1;
    public static final int REVIEW_STATUS_PENDING = 2;
    public static final int REVIEW_STATUS_REJECTED = 3;

    private static final Set<Integer> validReviewStatuses = Set.of(REVIEW_STATUS_APPROVED, REVIEW_STATUS_PENDING, REVIEW_STATUS_REJECTED);


    // Instance properties
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


    /**
    * Converts the review status integer to a human-readable string representation.
    * @param status The integer value representing the review status.
    * @return A string representation of the review status, such as "PENDING", "APPROVED", "REJECTED", or "UNKNOWN" for unrecognized status values.
     */

    public static String statusToString(int status) {
        return switch (status) {
            case Review.REVIEW_STATUS_PENDING -> "PENDING";
            case Review.REVIEW_STATUS_APPROVED -> "APPROVED";
            case Review.REVIEW_STATUS_REJECTED -> "REJECTED";
            default -> "UNKNOWN";
        };
    }

    public static String statusToNorwegianString(int status) {
        return switch (status) {
            case Review.REVIEW_STATUS_PENDING -> "Til vurdering";
            case Review.REVIEW_STATUS_APPROVED -> "Godkjent";
            case Review.REVIEW_STATUS_REJECTED -> "Avvist";
            default -> "Ukjent status";
        };
    }


    /** Returns the constant value for the pending review status. */
    public static int getPendingStatusConst() { return REVIEW_STATUS_PENDING; }

    /** Returns the constant value for the approved review status. */
    public static int getApprovedStatusConst() { return REVIEW_STATUS_APPROVED;  }

    /** Returns the constant value for the rejected review status. */
    public static int getRejectedStatusConst() { return REVIEW_STATUS_REJECTED; }


    /** Returns the constant value for the upvote type. */
    public static boolean isValidVoteType(int voteType) { return validVoteTypes.contains(voteType); }

    /** Returns the constant value for the pending review status. */
    public static boolean isValidReviewStatus(int reviewStatus) { return validReviewStatuses.contains(reviewStatus); }

    /**
     * Returns a human-readable string representing how long ago the review was created, formatted in Norwegian.
     *
     * @return A string like "for 5 minutter siden" indicating how long ago the review was created.
     */

    public String getShortDateString() {
        return NorwegianTimeAgoTextFormatter.formatInstantAgo(createdAt, "for ", " siden");
    }
}