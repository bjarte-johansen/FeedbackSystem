package root.repositories.review;

public interface ReviewVoteRepositoryCustom {

    /**
     * Votes a review. If the user has already voted, it updates the existing vote.
     * @param reviewId
     * @param voteTypeId
     * @param sessionId, typically String sessionId = request.getSession().getId();
     * @param ip , typically String ip = request.getRemoteAddr();
     * @return
     */
    int addVote(long reviewId, int voteTypeId, String sessionId, String ip);


    /**
     * Removes a vote for a review by the user identified by sessionId and ip.
     * @param reviewId
     * @param sessionId
     */
    int removeVote(long reviewId, String sessionId);


    /**
     * Gets the current vote for a review by the user identified by sessionId and ip.
     * @param reviewId
     * @param sessionId
     * @param ip
     * @return the current vote (1 for upvote, -1 for downvote, 0 for no vote)
     */
    int getVote(long reviewId, String sessionId, String ip);


    /**
     * Removes votes that are older than the specified expiration time.
     * @param expirationTimeInDays
     */
    void removeExpiredVotes(int expirationTimeInDays);
}
