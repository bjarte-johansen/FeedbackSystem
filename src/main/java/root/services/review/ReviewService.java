package root.services.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.AppConfig;
import root.includes.ReviewAggregateStatistics;
import root.models.review.Review;
import root.repositories.review.ReviewRepository;
import root.repositories.review.ReviewVoteRepository;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class ReviewService {
    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    ReviewVoteRepository reviewVoteRepo;

/*
    public void addReviewReportFlag(long reviewId) throws Exception {
        // TODO: Important
        // TODO:
        //      - We'd like send notifications to administrators when a review receives a new report flag, but
        //        for now we just update the count in the database.
        //      - Possibly should be marked as PENDING if enough report flags are received, and then an administrator can
        //        review it and decide whether to delete it or not.

        reviewRepo.addVoteReport(reviewId);
    }
 */



    /*
    things that do NOT require administrator privileges:
     */

    /**
     * Adds a vote to the review. voteType should be either Review.VOTE_UP or Review.VOTE_DOWN.
     *
     * @param reviewId
     * @param newVoteId Review.VOTE_UP, Review.VOTE_DOWN
     * @param delta Number of votes to add (positive) or remove (negative)
     */

    private void incrementVote(long reviewId, int newVoteId, int delta) {
        // we must ignore invalid vote ids, because when a user removes their vote, we call this method with
        // newVoteId = 0, which is not a valid vote id but we should ignore it and just decrement the old vote.

        if (newVoteId == Review.VOTE_UP) {
            reviewRepo.incrementLikeVote(reviewId, delta);
        } else if (newVoteId == Review.VOTE_DOWN) {
            reviewRepo.incrementDislikeVote(reviewId, delta);
        }
    }


    /**
     * Adds a vote to the review. voteType should be either Review.VOTE_UP or Review.VOTE_DOWN.
     *
     * @param reviewId
     * @param voteType
     */
    private void incrementVote(long reviewId, int voteType) {
        incrementVote(reviewId, voteType, 1);
    }


    /**
     * Removes a vote from the review. voteType should be either Review.VOTE_UP or Review.VOTE_DOWN.
     *
     * @param reviewId
     * @param voteType
     */
    private void decrementVote(long reviewId, int voteType) {
        incrementVote(reviewId, voteType, -1);
    }


    /**
     * Adds a vote to the review. If the user has already voted the same way, it does nothing. If the user has voted the
     * opposite way, it removes the old vote and adds the new vote.
     *
     * @param reviewId review id to vote on
     * @param newVoteId Review.VOTE_UP, Review.VOTE_DOWN
     * @param sessionId typically String sessionId = request.getSession().getId();
     * @param remoteIp typically String ip = request.getRemoteAddr();
     * @return true if the vote was added or changed, false if the user has already voted the same way
     * @throws Exception
     */

    public boolean submitVote(long reviewId, int newVoteId, String sessionId, String remoteIp) {
        try {
            int oldVoteId = reviewVoteRepo.getVote(reviewId, sessionId, remoteIp);

            if (newVoteId != oldVoteId) {
                // adjust old vote count, if exists
                if(oldVoteId == Review.VOTE_UP || oldVoteId == Review.VOTE_DOWN) {
                    incrementVote(reviewId, oldVoteId, -1);
                }

                // increment counter for new vote
                incrementVote(reviewId, newVoteId, 1);

                // remove old vote record, if exists
                reviewVoteRepo.removeVote(reviewId, sessionId);

                // add new vote record
                reviewVoteRepo.addVote(reviewId, newVoteId, sessionId, remoteIp);

                return true;
            }
        } catch (Exception e) {
            if (AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
        }

        return false;
    }



    /*
    things that require administrator privileges:
     */

    /**
     * Deletes a review by id. Only administrators can perform this action.
     *
     * @param reviewId
     */

    public void deleteById(long reviewId) {
        reviewRepo.deleteById(reviewId);
    }


    /**
     * Sets the review status by id. Only administrators can perform this action.
     *
     * @param reviewId
     * @param newStatus
     */

    public int setReviewStatus(long reviewId, int newStatus) {
        return reviewRepo.updateReviewStatus(reviewId, newStatus);
    }



    /*
    other things
     */

    /**
     * Gets the review score statistics for a given externalId. This method calculates the average score, total count,
     * total score sum, and score distribution (percentage and count for each score from 1 to 5) based on the review
     * data retrieved from the database.
     *
     * @param externalId
     * @param defaultScore
     * @return
     * @throws Exception
     */

    public ReviewAggregateStatistics getReviewAggregateStatistics(String externalId, int defaultScore) {
        var filteredScoreMap = reviewRepo.findApprovedReviewScoreStatsByExternalId(externalId);

        var scoreStats = new ReviewAggregateStatistics();

        long totalScoreSum = 0;
        long totalCount = 0;

        for (int i = 5; i >= 1; i--) {
            long hits = filteredScoreMap.getOrDefault(i, 0);
            totalScoreSum += hits * i;
            totalCount += hits;
        }

        double averageScore = avg(totalScoreSum, totalCount, 0);
        scoreStats.setAverageScore(averageScore);
        scoreStats.setTotalCount(totalCount);
        scoreStats.setTotalScore(totalScoreSum);

        for (int i = 5; i >= 1; i--) {
            int hits = filteredScoreMap.getOrDefault(i, 0);
            double pct = avg(hits, totalCount, 0.0) * 100.0;
            scoreStats.getScoreDistribution().put(i, pct);
            scoreStats.getScoreCount().put(i, hits);
        }

        return scoreStats;
    }

    private double avg(double sum, double count, double defaultScore) {
        return (count > 0) ? sum / count : defaultScore;
    }
}