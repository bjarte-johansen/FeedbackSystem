package root.models.review;

import root.interfaces.HasId;

import java.time.Instant;
import java.util.Map;
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

public class Review implements HasId {
    /**
     * Vote type constants:
     * Important: remember to update validVoteTypes set if you add new vote types
     */
    public static int VOTE_UP = 1;
    public static int VOTE_DOWN = 2;

    private static final Set<Integer> validVoteTypes = Set.of(VOTE_UP, VOTE_DOWN);


    /**
     * Review status constants: 1 = Approved 2 = Pending 3 = Rejected Important: remember to update validReviewStatuses
     * set if you add new vote types     *
     */

    public static final int REVIEW_STATUS_APPROVED = 1;
    public static final int REVIEW_STATUS_PENDING = 2;
    public static final int REVIEW_STATUS_REJECTED = 3;

    private static final Set<Integer> validReviewStatuses = Set.of(REVIEW_STATUS_APPROVED, REVIEW_STATUS_PENDING, REVIEW_STATUS_REJECTED);

    private static final Map<Integer, String> reviewStatusToStringMap = Map.of(
        REVIEW_STATUS_APPROVED, "APPROVED",
        REVIEW_STATUS_PENDING, "PENDING",
        REVIEW_STATUS_REJECTED, "REJECTED"
    );
    private static String reviewStatusToStringMapDefault = "Ukjent status";

    private static final Map<Integer, String> reviewStatusToNorwegianStringMap = Map.of(
        REVIEW_STATUS_APPROVED, "Godkjent",
        REVIEW_STATUS_PENDING, "Til vurdering",
        REVIEW_STATUS_REJECTED, "Avvist"
    );
    private static String reviewStatusToNorwegianStringMapDefault = "Ukjent status";

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
    public Review() { }

    /** get id */
    public Long getId() {return id != null ? id : 0L;}

    /** set id */
    public void setId(long id) {this.id = id;}

    /** get status */
    public int getStatus() {return this.status;}

    /** set status */
    public void setStatus(int status) {this.status = status;}

    /** get external id */
    public String getExternalId() {return externalId;}

    /** set external id */
    public void setExternalId(String externalId) {this.externalId = externalId;}

    /** get author id */
    public long getAuthorId() {return authorId;}

    /** set author id */
    public void setAuthorId(long authorId) {this.authorId = authorId;}

    /** get author name */
    public String getAuthorName() {return authorName;}

    /** set author name */
    public void setAuthorName(String authorName) {this.authorName = authorName;}

    /** get score */
    public int getScore() {return score;}

    /** set score */
    public void setScore(int score) {this.score = score;}

    /** get title */
    public String getTitle() {return this.title;}

    /** set title */
    public void setTitle(String title) {this.title = title;}

    /** get comment */
    public String getComment() {return comment;}

    /** set comment */
    public void setComment(String comment) {this.comment = comment;}

    /** get created at timestamp */
    public Instant getCreatedAt() {return createdAt;}

    /** set created at timestamp */
    public void setCreatedAt(Instant createdAt) {this.createdAt = createdAt;}

    /** get dislike count */
    public int getDislikeCount() {return dislikeCount;}

    /** set dislike count */
    public void setDislikeCount(int dislikeCount) {this.dislikeCount = dislikeCount;}

    /** get like count */
    public int getLikeCount() {return likeCount;}

    /** set like count */
    public void setLikeCount(int likeCount) {this.likeCount = likeCount;}


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
     *
     * @param status The integer value representing the review status.
     * @return A string representation of the review status, such as "PENDING", "APPROVED", "REJECTED", or "UNKNOWN" for
     * unrecognized status values.
     */

    public static String statusToString(int status) {
        return reviewStatusToStringMap.getOrDefault(status, reviewStatusToStringMapDefault);
    }


    /**
     * Converts the review status integer to a human-readable string representation in Norwegian.
     *
     * @param status The integer value representing the review status.
     * @return A string representation of the review status in Norwegian, such as "Til vurdering" for pending,
     * "Godkjent" for approved, "Avvist" for rejected, or "Ukjent status" for unrecognized status values.
     */
    public static String statusToNorwegianString(int status) {
        return reviewStatusToNorwegianStringMap.getOrDefault(status, reviewStatusToNorwegianStringMapDefault);
    }


    /** Returns the constant value for the pending review status. */
    public static int getPendingStatusConst() { return REVIEW_STATUS_PENDING; }

    /** Returns the constant value for the approved review status. */
    public static int getApprovedStatusConst() { return REVIEW_STATUS_APPROVED; }

    /** Returns the constant value for the rejected review status. */
    public static int getRejectedStatusConst() { return REVIEW_STATUS_REJECTED; }


    /** Returns the set of valid vote types. */
    public static Set<Integer> getValidReviewStatuses() { return validReviewStatuses; }


    /*
     check if status is valid
     */

    /** Returns the constant value for the upvote type. */
    public static boolean isValidVoteType(int voteType) {
        return validVoteTypes.contains(voteType);
    }

    /** Returns boolean indicating whether the given review status is valid */
    public static boolean isValidReviewStatus(int reviewStatus) {
        return validReviewStatuses.contains(reviewStatus);
    }
}