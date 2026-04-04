package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.controllers.ReviewAggregateScoreHelper;
import root.repositories.ReviewRepository;

@Service
public class ReviewService {
    @Autowired
    ReviewRepository reviewRepo;

    void addLike(long reviewId) throws Exception {
        reviewRepo.updateReviewLikeCount(reviewId, 1);
    }

    void addDislike(long reviewId) throws Exception {
        reviewRepo.updateReviewDislikeCount(reviewId, 1);
    }

    void updateStatus(long reviewId, int newStatus) throws Exception {
        reviewRepo.updateReviewStatus(reviewId, newStatus);
    }

    public ReviewAggregateScoreHelper getScoreStatsHelper(String externalId) throws Exception {
        var scoreMap = reviewRepo.findReviewScoreStatsByExternalId(externalId);

        var scoreStats = new ReviewAggregateScoreHelper();

        long totalScoreSum = 0;
        long totalScoreCount = 0;

        for(int i=5; i>=1; i--) {
            long hits = scoreMap.getOrDefault(i, 0);
            totalScoreSum += hits * i;
            totalScoreCount += hits;
        }
        double averageScore = (double) totalScoreSum / (double) totalScoreCount;
        scoreStats.setAverageScore(averageScore);
        scoreStats.setTotalScoreCount(totalScoreCount);
        scoreStats.setTotalScoreSum(totalScoreSum);

        for(int i=5; i>=1; i--) {
            int hits = scoreMap.getOrDefault(i, 0);
            Double pct = totalScoreCount > 0 ? ((double) hits / totalScoreCount) * 100.0 : 0.0;
            scoreStats.getScoreDistribution().put(i, pct);
            scoreStats.getScoreCounts().put(i, hits);
        }

        return scoreStats;
    }
}
