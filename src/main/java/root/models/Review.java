package root.models;

import root.includes.NorwegianTimeAgoTextFormatter;
import root.interfaces.IReview;

import java.time.Instant;

public class Review implements IReview {
    public static int STATUS_PENDING = 0;
    public static int STATUS_APPROVED = 1;
    public static int STATUS_REJECTED = 2;

    private Long id;
    private String externalId;
    private long authorId;
    private String authorName;
    private int score;
    private String title;
    private String comment;
    private Instant createdAt;
    private int status = STATUS_PENDING;

    public Review() {}
/*
    public Review(
        int status,
        String externalId,
        long authorId,
        String authorName,
        int score,
        String title,
        String comment,
        Instant createdAt
    ) {
        this.status = status;
        this.externalId = externalId; // ensures hash update
        this.authorId = authorId;
        this.authorName = authorName;
        this.score = score;
        this.title = title;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Review(
        int status,
        String externalId,
        long authorId,
        String authorName,
        int score,
        String title,
        String comment
    ) {
        this(status, externalId, authorId, authorName, score, title, comment, Instant.now());
    }

 */

    @Override
    public Long getId() {
        return id != null ? id : 0L;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }


    @Override
    public int getStatus(){ return this.status; }

    @Override
    public void setStatus(int status){ this.status = status; }


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
                ", status=" + statusToString(status) +
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
     * @return A string like "for 5 minutter siden" indicating how long ago the review was created.
     */

    public String getShortDateString() {
        return NorwegianTimeAgoTextFormatter.formatInstantAgo(createdAt, "for ", " siden");
    }
}