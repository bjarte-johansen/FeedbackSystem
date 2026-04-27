package root.includes;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * ScoreStatsHelper is a helper class that encapsulates the score statistics for reviews. It contains the average score,
 * total score count, score distribution, and score counts for each score value.
 *
 * This object is used as a DTO (data transer object) only
 *
 * It is currently not cached because the following actions can invalidate stats:
 *  - Adding a new review
 *  - Deleting a review
 *  - Updating a review with new status (Only approved reviews are counted)
 *  -
 */

public class ReviewAggregateStatistics {
    /*
    public record Score(double percentOfTotal, long count) {
    }
     */

    private double averageScore;
    private long totalCount;
    private long totalScore;

    private Map<Integer, Double> scoreDistribution;
    private Map<Integer, Integer> scoreCount;


    public ReviewAggregateStatistics(){
        averageScore = 0.0;
        totalCount = 0;
        totalScore = 0;

        scoreDistribution = new  LinkedHashMap<>();
        scoreCount = new LinkedHashMap<>();
    }


    /**
     * Returns the total score for all reviews. The total score is the sum of all individual review scores and can be
     * used to calculate the average score or to provide an overall rating for a product, service, or entity based on
     * the reviews.
     *
     * @return
     */
    public long getTotalScore() {
        return totalScore;
    }

    /**
     * Sets the total score for all reviews. The total score is the sum of all individual review scores and can be used
     * to calculate the average score or to provide an overall rating for a product, service, or entity based on the
     * reviews.
     *
     * @param score
     */
    public void setTotalScore(long score) {
        this.totalScore = score;
    }

    /**
     * Returns the total count of reviews. The total count represents the number of individual reviews that have been
     * submitted for a product, service, or entity.
     *
     * @return
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * Sets the total count of reviews. The total count represents the number of individual reviews that have been
     * submitted for a product, service, or entity.
     *
     * @param count
     */
    public void setTotalCount(long count) {
        this.totalCount = count;
    }

    /**
     * Returns the average score for the reviews. The average score is calculated by dividing the total score by the
     * total count of reviews.
     *
     * @return
     */
    public double getAverageScore() {
        return averageScore;
    }

    /**
     * Sets the average score for the reviews. The average score is calculated by dividing the total score by the total
     * count of reviews.
     *
     * @param averageScore
     */
    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }


    /**
     * Returns the score distribution for the reviews. The score distribution is a map that represents the percentage of
     * reviews that fall into each score category. The keys of the map are the score values (e.g., 1, 2, 3, 4, 5), and
     * the values are the corresponding percentages of reviews that have that score.
     *
     * @return Map<Integer, Double> representing the score distribution for the reviews, where the keys are the score
     *  values and the values are the corresponding percentages of reviews that have that score.
     */
    public Map<Integer, Double> getScoreDistribution() {
        return scoreDistribution;
    }

//    /**
//     * Sets the score distribution for the reviews. The score distribution is a map that represents the percentage of
//     * reviews that fall into each score category. The keys of the map are the score values (e.g., 1, 2, 3, 4, 5), and
//     * the values are the corresponding percentages of reviews that have that score.
//     *
//     * @param scoreDistribution Map<Integer, Double> representing the score distribution for the reviews, where the keys
//     *  are the score values and the values are the corresponding percentages of reviews that have that score.
//     */
//    public void setScoreDistribution(Map<Integer, Double> scoreDistribution) {
//        this.scoreDistribution = scoreDistribution;
//    }

    /**
     * Returns the score counts for the reviews. The score counts is a map that represents the count of reviews that fall
     * into each score category. The keys of the map are the score values (e.g., 1, 2, 3, 4, 5), and the values are the
     * corresponding counts of reviews that have that score.
     *
     * @return Map<Integer, Integer> representing the score counts for the reviews, where the keys are the score values
     *  and the values are the corresponding counts of reviews that have that score.
     */
    public Map<Integer, Integer> getScoreCount() {
        return scoreCount;
    }

//    /**
//     * Sets the score counts for the reviews. The score counts is a map that represents the count of reviews that fall into
//     * each score category. The keys of the map are the score values (e.g., 1, 2, 3, 4, 5), and the values are the
//     * corresponding counts of reviews that have that score.
//     *
//     * @param scoreCount Map<Integer, Integer> representing the score counts for the reviews, where the keys are the
//     *  score values and the values are the corresponding counts of reviews that have that score.
//     */
//    public void setScoreCount(Map<Integer, Integer> scoreCount) {
//        this.scoreCount = scoreCount;
//    }
}