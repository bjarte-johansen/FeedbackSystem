package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.AppContext;
import root.controllers.ReviewAggregateScoreHelper;
import root.models.Review;
import root.repositories.ReviewRepository;

@Service
public class ReviewService {
    @Autowired
    ReviewRepository reviewRepo;

    /*
    things that do NOT require administrator privileges:
     */

    public void addReviewVote(long reviewId, int voteType, int delta) throws Exception{
        if(voteType == Review.VOTE_UP)
            reviewRepo.addVoteUp(reviewId, (int) Math.abs(delta));
        else if(voteType == Review.VOTE_DOWN){
            reviewRepo.addVoteDown(reviewId, (int) Math.abs(delta));
        }
    }
/*
    public void addReviewLike(long reviewId) throws Exception {
        reviewRepo.addReviewVote(reviewId, Review.VOTE_UP, 1);
    }

    public void addReviewDislike(long reviewId) throws Exception {
        reviewRepo.updateReviewDislikeCount(reviewId, Review.VOTE_UP, 1);
    }
 */


    /*
    things that require administrator privileges:
     */
    public void deleteById(long reviewId) throws Exception {
        AppContext.checkIsAdministrator();

        reviewRepo.deleteById(reviewId);
    }

    public void setReviewStatus(long reviewId, int newStatus) throws Exception {
        AppContext.checkIsAdministrator();

        reviewRepo.updateReviewStatus(reviewId, newStatus);
    }


    /*
    other things
     */

    private double avg(double sum, double count, double defaultScore) {
        return (count > 0) ? sum / count : defaultScore;
    }

    public ReviewAggregateScoreHelper getScoreStatsHelper(String externalId, int defaultScore) throws Exception {
        var scoreMap = reviewRepo.findReviewScoreStatsByExternalId(externalId);

        var scoreStats = new ReviewAggregateScoreHelper();

        long totalScoreSum = 0;
        long totalCount = 0;

        for(int i=5; i>=1; i--) {
            long hits = scoreMap.getOrDefault(i, 0);
            totalScoreSum += hits * i;
            totalCount += hits;
        }

        double averageScore = avg(totalScoreSum, totalCount, defaultScore);
        scoreStats.setAverageScore(averageScore);
        scoreStats.setTotalCount(totalCount);
        scoreStats.setTotalScore(totalScoreSum);

        for(int i=5; i>=1; i--) {
            int hits = scoreMap.getOrDefault(i, 0);
            double pct = avg(hits, totalCount, 0.0) * 100.0;
            scoreStats.getScoreDistribution().put(i, pct);
            scoreStats.getScoreCounts().put(i, hits);
        }

        return scoreStats;
    }

}
