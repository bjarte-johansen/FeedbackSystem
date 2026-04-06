package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.AppConfig;
import root.app.AppContext;
import root.controllers.ReviewAggregateScoreHelper;
import root.includes.logger.logger.Logger;
import root.models.Review;
import root.repositories.ReviewRepository;
import root.repositories.ReviewVoteRepository;

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
     * @throws Exception
     */

    private void addReviewVote(long reviewId, int newVoteId, int delta) throws Exception {
        if (newVoteId == Review.VOTE_UP) {
            reviewRepo.addVoteUp(reviewId, delta);
        } else if (newVoteId == Review.VOTE_DOWN) {
            reviewRepo.addVoteDown(reviewId, delta);
        }else{
            throw new Exception("Invalid vote id");
        }
        /*
        else if(voteType == Review.VOTE_REPORT){
            reviewRepo.addVoteReport(reviewId, delta);
        }
        */
    }

    /**
     * Adds a vote to the review. voteType should be either Review.VOTE_UP or Review.VOTE_DOWN.
     *
     * @param reviewId
     * @param voteType
     * @throws Exception
     */
    private void addReviewVote(long reviewId, int voteType) throws Exception {
        addReviewVote(reviewId, voteType, 1);
    }


    /**
     * Removes a vote from the review. voteType should be either Review.VOTE_UP or Review.VOTE_DOWN.
     *
     * @param reviewId
     * @param voteType
     * @throws Exception
     */
    private void removeReviewVote(long reviewId, int voteType) throws Exception {
        addReviewVote(reviewId, voteType, -1);
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

    public boolean addReviewVote(long reviewId, int newVoteId, String sessionId, String remoteIp) {
        try {
            int oldVoteId = reviewVoteRepo.getVote((int) reviewId, sessionId, remoteIp);

            if (newVoteId != oldVoteId) {
                // remove like/dislike from review
                if (oldVoteId == Review.VOTE_UP) {
                    removeReviewVote(reviewId, Review.VOTE_UP);
                } else if (oldVoteId == Review.VOTE_DOWN) {
                    removeReviewVote(reviewId, Review.VOTE_DOWN);
                }

                // remove old vote record, if exists
                reviewVoteRepo.removeVote(reviewId, sessionId);

                // add new vote
                addReviewVote(reviewId, newVoteId);
                reviewVoteRepo.addVote(reviewId, newVoteId, sessionId, remoteIp);

                return true;
            }
        } catch (Exception e) {
            if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
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
     * @throws Exception
     */

    public void deleteById(long reviewId) throws Exception {
        AppContext.checkIsAdministrator();

        reviewRepo.deleteById(reviewId);
    }


    /**
     * Sets the review status by id. Only administrators can perform this action.
     *
     * @param reviewId
     * @param newStatus
     * @throws Exception
     */

    public void setReviewStatus(long reviewId, int newStatus) throws Exception {
        AppContext.checkIsAdministrator();

        reviewRepo.updateReviewStatus(reviewId, newStatus);
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
    public ReviewAggregateScoreHelper getScoreStatsHelper(String externalId, int defaultScore, int filterScoreMin, int filterScoreMax) throws Exception {
        var scoreMap = reviewRepo.findReviewScoreStatsByExternalId(externalId, filterScoreMin, filterScoreMax);

        var scoreStats = new ReviewAggregateScoreHelper();

        long totalScoreSum = 0;
        long totalCount = 0;

        for (int i = 5; i >= 1; i--) {
            long hits = scoreMap.getOrDefault(i, 0);
            totalScoreSum += hits * i;
            totalCount += hits;
        }

        double averageScore = avg(totalScoreSum, totalCount, defaultScore);
        scoreStats.setAverageScore(averageScore);
        scoreStats.setTotalCount(totalCount);
        scoreStats.setTotalScore(totalScoreSum);

        for (int i = 5; i >= 1; i--) {
            int hits = scoreMap.getOrDefault(i, 0);
            double pct = avg(hits, totalCount, 0.0) * 100.0;
            scoreStats.getScoreDistribution().put(i, pct);
            scoreStats.getScoreCounts().put(i, hits);
        }

        return scoreStats;
    }

    private double avg(double sum, double count, double defaultScore) {
        return (count > 0) ? sum / count : defaultScore;
    }

}
