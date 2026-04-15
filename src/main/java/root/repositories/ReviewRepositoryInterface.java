package root.repositories;

import root.app.ReviewQueryOptions;
import root.models.Review;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public interface ReviewRepositoryInterface {
    void incrementReportVote(long reviewId, int delta);
    void incrementLikeVote(long reviewId, int delta);
    void incrementDislikeVote(long reviewId, int delta);

    void updateReviewStatus(long reviewId, int newStatus);

    LinkedHashMap<Integer, Integer> findReviewScoreStatsByExternalId(String externalId);

    List<String> findDistinctExternalIdByExternalId();
    List<Review> findByExternalIdWithPagination(String externalId, ReviewQueryOptions options);
    List<Review> findByAnyExternalIdWithPagination(ReviewQueryOptions options);

    int countByExternalId(String externalId, ReviewQueryOptions options);

    int countByAnyExternalId(ReviewQueryOptions options);
}
