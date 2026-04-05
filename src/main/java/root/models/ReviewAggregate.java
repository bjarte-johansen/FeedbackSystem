package root.models;

import root.includes.NorwegianTimeAgoTextFormatter;
import root.interfaces.HasId;
import root.interfaces.IReview;

import root.includes.NorwegianTimeAgoTextFormatter;
import root.interfaces.IReview;

import java.time.Instant;


import java.time.Instant;

@Deprecated
public class ReviewAggregate implements HasId {
    private Long id;
    private String externalId;
    private double averageScore;
    private long totalScoreCount;
    private long totalScoreSum;

    private String encoded_histogram_percent;
    private String encoded_histogram_count;

    public ReviewAggregate() {}

    @Override
    public Long getId() {
        return id != null ? id : 0L;
    }
    @Override
    public void setId(long id) {
        this.id = id;
    }

    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }

    public long getTotalCount() { return totalScoreCount; }
    public void setTotalCount(long totalCount) { this.totalScoreCount = totalCount; }

    public long getSumScore() { return totalScoreSum; }
    public void setSumScore(long sumScore) { this.totalScoreSum = sumScore; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public void setEncodedHistogramPercent(String encodedHistogram) {
        this.encoded_histogram_percent = encodedHistogram;
    }
    public String getEncodedHistogramPercent() {
        return encoded_histogram_percent;
    }

    public void setEncodedHistogramCount(String encodedHistogram) {
        this.encoded_histogram_count = encodedHistogram;
    }
    public String getEncodedHistogramCount() {
        return encoded_histogram_count;
    }

    @Override
    public String toString() {
        return "ReviewAggregate{" +
            "id=" + id +
            ", average_score=" + averageScore +
            ", total_count=" + totalScoreCount +
            ", sum_score=" + totalScoreSum +
            ", encoded_histogram_percent='" + encoded_histogram_percent + '\'' +
            ", encoded_histogram_count='" + encoded_histogram_count + '\'' +
            '}';
    }
}
