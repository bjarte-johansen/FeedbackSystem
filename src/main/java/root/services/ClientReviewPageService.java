package root.services;

import org.springframework.stereotype.Service;
import root.app.AppConfig;
import root.app.AppContext;
import root.app.ReviewQueryOptions;
import root.controllers.ReviewQueryOptionsParser;
import root.includes.Utils;
import root.includes.ReviewAggregateStatistics;
import root.models.Review;
import root.repositories.ReviewRepository;

import java.util.*;


@Service
public class ClientReviewPageService {
    // map of ordering options for the review list page, with display name as key and corresponding
    // orderByEnum value as value
    public static LinkedHashMap<String, Object> orderByOptionsMap() {
        return Utils.linkedMap(
            "Nyest først", ReviewQueryOptions.OPTION_ORDER_BY_ID_DESC,
            "Eldst først", ReviewQueryOptions.OPTION_ORDER_BY_ID_ASC,
            "Nyttigst først", ReviewQueryOptions.OPTION_ORDER_BY_LIKE_COUNT_DESC,
            "Score (høyest først)", ReviewQueryOptions.OPTION_ORDER_BY_SCORE_DESC,
            "Score (lavest først)", ReviewQueryOptions.OPTION_ORDER_BY_SCORE_ASC
            //"-", ""
        );
    }


    private final AppContext appContext;
    private final ReviewRepository reviewRepo;
    private final ReviewService reviewService;

    public ClientReviewPageService(ReviewRepository reviewRepo, ReviewService reviewService, AppContext appContext) {
        this.reviewRepo = reviewRepo;
        this.reviewService = reviewService;
        this.appContext = appContext;
    }


    /**
     * Build the model data for the review listing page based on the provided externalId and query options. This method
     * fetches the reviews from the database using the ReviewRepository, and adds them to the model map along with other
     * relevant data such as the externalId, page cursor, and optionally the review statistics if includeStats is true.
     * The model map is then returned for use in the JSP view.
     *
     * @param externalId
     * @param options
     * @param includeStats
     * @return
     */

    public Map<String, Object> buildReviewListingModelData(
        String externalId,
        ReviewQueryOptions options,
        boolean includeStats
    ) {
        Map<String, Object> vm = new HashMap<>();

        // extract external id
        externalId = (externalId == null || externalId.isBlank()) ? AppConfig.DEFAULT_INVALID_EXTERNAL_ID : externalId;
        vm.put("externalId", externalId);
        vm.put("pageCursor", options.getPageCursor().encode());

        // get reviews and add to model
        List<Review> reviews = reviewRepo.findByExternalIdWithPagination(externalId, options);
        vm.put("reviews", reviews);

        int totalReviewCount = reviewRepo.countByExternalId(externalId, options);
        vm.put("unpaginatedFilteredReviewCount", totalReviewCount);

        // add stats to model for display in JSP if includeStats is true.
        if (includeStats) {
            // get score stats for the given externalId for approved reviews. This will be used for display of average score,
            ReviewAggregateStatistics reviewStats = reviewService.getReviewAggregateStatistics(externalId, AppConfig.DEFAULT_MAX_SCORE);

            // get score stats for the given externalId and add to model
            vm.put("statistics", reviewStats);
        }

        vm.put("filters", ReviewQueryOptionsParser.encodeOptions(options));

        return vm;
    }
}
