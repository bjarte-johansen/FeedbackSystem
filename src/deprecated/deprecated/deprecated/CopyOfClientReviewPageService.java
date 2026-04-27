package root.services.review.deprecated;

import org.springframework.stereotype.Service;
import root.app.AppConfig;
import root.includes.ReviewAggregateStatistics;
import root.includes.ReviewQueryOptions;
import root.includes.ReviewQueryOptionsParser;
import root.models.review.Review;
import root.repositories.review.ReviewRepository;
import root.services.review.ReviewService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Deprecated
public class CopyOfClientReviewPageService {
//    private final ReviewRepository reviewRepo;
//    private final ReviewService reviewService;
//
//    /**
//     * constructor that injects services/repos
//     */
//
//    public CopyOfClientReviewPageService(ReviewRepository reviewRepo, ReviewService reviewService) {
//        this.reviewRepo = reviewRepo;
//        this.reviewService = reviewService;
//    }
//
//
//    /**
//     * Build the model data for the review listing page based on the provided externalId and query options. This method
//     * fetches the reviews from the database using the ReviewRepository, and adds them to the model map along with other
//     * relevant data such as the externalId, page cursor, and optionally the review statistics if includeStats is true.
//     * The model map is then returned for use in the JSP view.
//     *
//     * @param externalId
//     * @param options
//     * @param includeStats
//     * @return
//     */
//
//    public Map<String, Object> buildReviewListingModelData(
//        String externalId,
//        ReviewQueryOptions options,
//        boolean includeStats
//    ) {
//        Map<String, Object> vm = new HashMap<>();
//
//        // extract external id
//        externalId = (externalId == null || externalId.isBlank()) ? AppConfig.DEFAULT_INVALID_EXTERNAL_ID : externalId;
//        vm.put("externalId", externalId);
//        vm.put("pageCursor", options.getPageCursor().encode());
//
//        // get reviews and add to model
//        List<Review> reviews = reviewRepo.findByExternalIdWithPagination(externalId, options);
//        vm.put("reviews", reviews);
//
//        int totalReviewCount = reviewRepo.countByExternalId(externalId, options);
//        vm.put("unpaginatedFilteredReviewCount", totalReviewCount);
//
//        // add stats to model for display in JSP if includeStats is true.
//        if (includeStats) addStats(vm, externalId);
//
//        vm.put("filters", ReviewQueryOptionsParser.encodeOptions(options));
//
//        return vm;
//    }
//
//    private void addStats(Map<String, Object> vm, String externalId) {
//        ReviewAggregateStatistics reviewStats = reviewService.getReviewAggregateStatistics(externalId, AppConfig.DEFAULT_MAX_SCORE);
//        vm.put("statistics", reviewStats);
//    }
}
