package root.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScoreStatsHelper {
    private double averageScore;
    private int totalScoreCount;
    private Map<Integer, Double> scoreDistribution = new LinkedHashMap<Integer, Double>();
    private Map<Integer, Integer> scoreCounts = new LinkedHashMap<>();

    public Map<Integer, Double> getScoreDistribution() { return scoreDistribution; }
    public void setScoreDistribution(Map<Integer, Double> scoreDistribution) { this.scoreDistribution = scoreDistribution; }

    public int getTotalScoreCount() { return totalScoreCount; }
    public void setTotalScoreCount(int totalScoreCount) { this.totalScoreCount = totalScoreCount; }

    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }

    public Map<Integer, Integer> getScoreCounts() { return scoreCounts; }
    public void setScoreCounts(Map<Integer, Integer> scoreCounts) { this.scoreCounts = scoreCounts; }
}