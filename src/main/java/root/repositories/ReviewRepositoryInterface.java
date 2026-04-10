package root.repositories;

import root.app.ReviewQueryOptions;
import root.models.Review;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public interface ReviewRepositoryInterface {
    void incrementReportVote(long reviewId, int delta) throws Exception;
    void incrementLikeVote(long reviewId, int delta) throws Exception;
    void incrementDislikeVote(long reviewId, int delta) throws Exception;

    void updateReviewStatus(long reviewId, int newStatus) throws Exception;

    LinkedHashMap<Integer, Integer> findReviewScoreStatsByExternalId(String externalId, Set<Integer> scoreFilterSet);

    List<String> findUniqueExternalIds() throws Exception;
    List<Review> findByExternalIdWithPagination(String externalId, ReviewQueryOptions options) throws Exception;
    List<Review> findByAnyExternalIdWithPagination(ReviewQueryOptions options) throws Exception;

    int countByExternalId(String externalId, ReviewQueryOptions options) throws Exception;
    int countByExternalIdAndStatus(String externalId, int status) throws Exception;

    int countByAnyExternalId(ReviewQueryOptions options) throws Exception;
}
