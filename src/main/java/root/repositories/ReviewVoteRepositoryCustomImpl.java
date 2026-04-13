package root.repositories;

import root.app.AppConfig;
import root.database.FSQLQuery;

public class ReviewVoteRepositoryCustomImpl implements ReviewVoteRepositoryInterface{

    /**
     * Votes a review. If the user has already voted, it updates the existing vote.
     *
     * @param reviewId
     * @param voteTypeId
     * @param sessionId, typically String sessionId = request.getSession().getId();
     * @param ip , typically String ip = request.getRemoteAddr();
     * @return
     */

    @Override
    public int addVote(long reviewId, int voteTypeId, String sessionId, String ip) {
        String sql = "INSERT INTO " + AppConfig.REVIEW_VOTE_TABLE_NAME
            + " (review_id, session_id, ip, vote)"
            + " VALUES (?, ?, ?, ?)"
            + " ON CONFLICT (review_id, session_id)"
            + " DO UPDATE SET vote = EXCLUDED.vote, ip = EXCLUDED.ip";

        return FSQLQuery.create(sql)
            .bind(reviewId)
            .bind(sessionId)
            .bind(ip)
            .bind(voteTypeId)
            .update();
    }


    /**
     * Removes a user's vote for a review. This can be used to allow users to retract their votes.
     *
     * @param reviewId
     * @param sessionId
     * @return number of rows affected (should be 1 if a vote was removed, 0 if no vote existed)
     */

    @Override
    public int removeVote(long reviewId, String sessionId) {
        String sql = "DELETE FROM " + AppConfig.REVIEW_VOTE_TABLE_NAME + " WHERE review_id = ? AND session_id = ?";

        return FSQLQuery.create(sql)
            .bind(reviewId)
            .bind(sessionId)
            .delete();
    }


    /**
     * Retrieves the current vote for a review by a specific user. Returns 0 if no vote exists, 1 for like, and -1 for
     * dislike.
     *
     * @param reviewId
     * @param sessionId
     * @param ip
     * @return
     */

    @Override
    public int getVote(long reviewId, String sessionId, String ip) {
        String sql = "SELECT vote FROM " + AppConfig.REVIEW_VOTE_TABLE_NAME + " WHERE review_id = ? AND session_id = ?";

        return FSQLQuery.create(sql)
            .bind(reviewId)
            .bind(sessionId)
            .fetchColumn(Integer.class)
            .orElse(0);
    }

    @Override
    public void removeExpiredVotes(int expirationTimeInDays) {
        String sql = "DELETE FROM " + AppConfig.REVIEW_VOTE_TABLE_NAME + " WHERE created_at < (NOW() - INTERVAL '" + expirationTimeInDays + " days')";

        FSQLQuery.create(sql)
            .delete();
    }
}