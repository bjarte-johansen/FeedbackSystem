package root.models;

import root.utils.NorwegianTimeAgoTextFormatter;

import java.time.Instant;

public class Review implements IReview {

    private Long id = null;
    private String externalId = "";
    private long authorId;
    private String authorName = "";
    private int score;
    private String title = "";
    private String comment = "";
    private Instant createdAt = Instant.now();

    public Review() {}

    public Review(
        String externalId,
        long authorId,
        String authorName,
        int score,
        String comment,
        Instant createdAt
    ) {
        this.externalId = externalId; // ensures hash update
        this.authorId = authorId;
        this.authorName = authorName;
        this.score = score;
        this.comment = comment;
        this.createdAt = Instant.now();
    }

    public Review(
        String externalId,
        long authorId,
        String authorName,
        int score,
        String comment
    ) {
        this(externalId, authorId, authorName, score, comment, Instant.now());
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
    public String getTitle(){ return this.title; }

    @Override
    public void setTitle(String title){ this.title = title; }


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
    public String toString() {
        return "Review(" +
                "id=" + id +
                ", externalId='" + externalId + '\'' +
                ", authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", score=" + score +
                ", title='" + title + '\'' +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                ')';
    }


    /**
     * Returns a human-readable string representing how long ago the review was created, formatted in Norwegian.
     * @return A string like "for 5 minutter siden" indicating how long ago the review was created.
     */

    public String getShortDateString() {
        return NorwegianTimeAgoTextFormatter.formatInstantAgo(createdAt, "for ", " siden");
    }
}