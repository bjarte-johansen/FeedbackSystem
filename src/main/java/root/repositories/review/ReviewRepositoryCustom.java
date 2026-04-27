package root.repositories.review;

import root.includes.ReviewQueryOptions;
import root.models.review.Review;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ReviewRepositoryCustom {
//    /** Increments report vote count for a review, ensuring it stays within the bounds */
//    void incrementReportVote(long reviewId, int delta);

    /** Increments like count for a review, ensuring it stays within the bounds */
    void incrementLikeVote(long reviewId, int delta);

    /** Increments dislike count for a review, ensuring it stays within the bounds */
    void incrementDislikeVote(long reviewId, int delta);

    int updateReviewStatus(long reviewId, int newStatus);

    LinkedHashMap<Integer, Integer> findApprovedReviewScoreStatsByExternalId(String externalId);

    List<String> findDistinctExternalIdByExternalId();
    List<Review> findByExternalIdWithPagination(String externalId, ReviewQueryOptions options);
    List<Review> findByAnyExternalIdWithPagination(ReviewQueryOptions options);
    List<Review> findByOptionalExternalIdWithPagination(String externalId, ReviewQueryOptions options);

    int countByExternalId(String externalId, ReviewQueryOptions options);
    //int countByAnyExternalId(ReviewQueryOptions options);

    Map<Integer, Integer> countByExternalIdMapByStatus(String externalId);
}
