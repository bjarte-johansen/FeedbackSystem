package root.controllers;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * ScoreStatsHelper is a helper class that encapsulates the score statistics for reviews. It contains the average score,
 * total score count, score distribution, and score counts for each score value.
 */

public class ReviewAggregateScoreHelper {
    private double averageScore;
    private long totalScoreCount;
    private long totalScoreSum;

    private Map<Integer, Double> scoreDistribution = new LinkedHashMap<Integer, Double>();
    private Map<Integer, Integer> scoreCounts = new LinkedHashMap<>();

    public long getTotalScoreSum() { return totalScoreSum; }
    public void setTotalScoreSum(long totalScoreSum) { this.totalScoreSum = totalScoreSum; }

    public long getTotalScoreCount() { return totalScoreCount; }
    public void setTotalScoreCount(long totalScoreCount) { this.totalScoreCount = totalScoreCount; }

    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }

    public Map<Integer, Double> getScoreDistribution() { return scoreDistribution; }
    public void setScoreDistribution(Map<Integer, Double> scoreDistribution) { this.scoreDistribution = scoreDistribution; }

    public Map<Integer, Integer> getScoreCounts() { return scoreCounts; }
    public void setScoreCounts(Map<Integer, Integer> scoreCounts) { this.scoreCounts = scoreCounts; }
}