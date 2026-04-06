package root.models;

import root.interfaces.HasId;

import java.time.Instant;

public class ReviewVote implements HasId {
    private Long id;
    private Long reviewId;
    private String sessionId;
    private String ip;
    private int vote;
    private Instant createdAt;

    /**
     * The id should be stored as a long in the database. It should be auto-generated and unique for each ReviewVote.
     *
     * @return
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * The id should be stored as a long in the database. It should be auto-generated and unique for each ReviewVote.
     *
     * @param id
     */
    @Override
    public void setId(long id) {
        this.id = id;
    }

    /**
     * The reviewId should be stored as a long in the database. It should be validated to ensure it corresponds to an
     * existing review before being stored.
     *
     * @return
     */
    public Long getReviewId() {
        return reviewId;
    }

    /**
     * The reviewId should be stored as a long in the database. It should be validated to ensure it corresponds to an
     * existing review before being stored.
     *
     * @param reviewId
     */
    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    /**
     * The sessionId should be stored as a string in the database. It should be validated to ensure it is a valid UUID
     * before being stored.
     *
     * @return
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * The sessionId should be stored as a string in the database. It should be validated to ensure it is a valid UUID
     * before being stored.
     *
     * @param sessionId
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * The IP address should be stored as a string in the database. It should be validated to ensure it is a valid IPv4
     * or IPv6 address before being stored.
     *
     * @return
     */
    public String getIp() {
        return ip;
    }

    /**
     * The IP address should be stored as a string in the database. It should be validated to ensure it is a valid IPv4
     * or IPv6 address before being stored.
     *
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * The vote field should be set to 1 for an upvote and -1 for a downvote. It should not accept any other values.
     *
     * @return
     */
    public int getVote() {
        return vote;
    }

    /**
     * The vote field should be set to 1 for an upvote and -1 for a downvote. It should not accept any other values.
     *
     * @param vote
     */
    public void setVote(int vote) {
        this.vote = vote;
    }

    /**
     * The createdAt field should be set to the current timestamp when a new ReviewVote is created.
     *
     * @return
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * The createdAt field should be set to the current timestamp when a new ReviewVote is created. This can be done in
     * the service layer when creating a new ReviewVote instance.
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
