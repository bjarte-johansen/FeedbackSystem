package root.services;

import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import root.app.AppConfig;
import root.app.AppContext;
import root.app.ReviewQueryOptions;
import root.includes.PageCursor;
import root.includes.PageCursorEncoder;
import root.includes.Utils;
import root.includes.ReviewAggregateStatistics;
import root.models.Review;
import root.repositories.ReviewRepository;

import java.util.*;

import static com.google.common.base.Preconditions.*;



@Service
public class ReviewPageService {
    // map of ordering options for the review list page, with display name as key and corresponding
    // orderByEnum value as value
    private static LinkedHashMap<String, Object> orderByOptionsMap = Utils.linkedMap(
        "Nyest først", ReviewQueryOptions.OPTION_ORDER_BY_ID_DESC,
        "Eldst først", ReviewQueryOptions.OPTION_ORDER_BY_ID_ASC,
        "Nyttigst først", ReviewQueryOptions.OPTION_ORDER_BY_LIKE_COUNT_DESC,
        "Score (høyest først)", ReviewQueryOptions.OPTION_ORDER_BY_SCORE_DESC,
        "Score (lavest først)", ReviewQueryOptions.OPTION_ORDER_BY_SCORE_ASC
        );




    private final AppContext appContext;
    private final ReviewRepository reviewRepo;
    private final ReviewService reviewService;

    public ReviewPageService(ReviewRepository reviewRepo, ReviewService reviewService, AppContext appContext) {
        this.reviewRepo = reviewRepo;
        this.reviewService = reviewService;
        this.appContext = appContext;
    }


/*
    private static void addFormattersToModel(Map<String, Object> modelMap) {
        // add formatters to model for display in JSP
        modelMap.put("dblFormatter1", dblFormatWithSingleDecimal);
        modelMap.put("dateFormatter", dateFormatter);
        modelMap.put("daysAgoFormatter", daysAgoFormatter);
        modelMap.put("dblFormatterCssPointFive", dblFormatRoundToHalfDotToDash);
    }
 */

    private void addReviewStatsToModel(Map<String, Object> modelMap, String externalId) {
        // get score stats for the given externalId for approved reviews. This will be used for display of average score,
        ReviewAggregateStatistics reviewStats = reviewService.getScoreStatsHelper(externalId, Review.REVIEW_STATUS_APPROVED);

        // get score stats for the given externalId and add to model
        modelMap.put("reviewStats", reviewStats);
    }



    // real decoding of externalId from request URI for the /reviews/{externalId} route.
    // This allows for more complex routing and is more flexible than using a request parameter, but requires more
    // setup and handling in the controller. For simplicity, we are using a request parameter for the externalId
    // in this example, but in a real application you would likely want to use the path variable approach for
    // better routing and cleaner URLs.

    private String extractExternalIdFromRequest(String externalId, HttpServletRequest req) throws Exception {
        // TODO: remove defaulting to first unique externalId for production code, should require an externalId to be
        //  provided and show an error if not provided
        if (externalId == null || externalId.isBlank()) {
            Review firstReviewWithExternalIds = reviewRepo.findFirstByExternalIdNotEquals("").orElse(null);

            if (firstReviewWithExternalIds != null) {
                externalId = firstReviewWithExternalIds.getExternalId();
            } else {
                externalId = "\\//unique-double-escaped-path";
            }
        }
        checkArgument(externalId != null && !externalId.isBlank(), "externalId must be non-null and non-empty");

        return externalId;
    }

    private static @NotNull ReviewQueryOptions getReviewQueryOptions(int orderByEnum, String scoreFilter, PageCursor decodedCursor) {
        // build review query options based on request parameters.
        // This includes pagination, sorting, and filtering options for fetching reviews from the database.
        ReviewQueryOptions o = new ReviewQueryOptions();
        o.setPageCursor(decodedCursor);
        o.getStatusFilterSet().add(Review.REVIEW_STATUS_APPROVED);
        o.setOrderByEnum(orderByEnum);

        // add score filter to options if set
        if (scoreFilter != null && !scoreFilter.isBlank() && !scoreFilter.equals("-1")) {
            List<Integer> scoreFilterList = Utils.parseCsvIntList(scoreFilter);
            o.getScoreFilterSet().addAll(scoreFilterList);
        }
        return o;
    }

    public Map<String, Object> buildReviewListingPage(
        String externalId,
        String cursorStr,
        int orderByEnum,
        String scoreFilter,
        HttpServletRequest req,
        boolean includeStats
    ) throws Exception {
        Map<String, Object> modelMap = new HashMap<>();

        // extract external id
        externalId = extractExternalIdFromRequest(externalId, req);
        modelMap.put("externalId", externalId);

        // decode cursor
        PageCursor decodedCursor = PageCursorEncoder.parseOrDefault(cursorStr, AppConfig.CLIENT_DEFAULT_MAX_VISIBLE_REVIEWS);
        modelMap.put("pageCursor", decodedCursor.encode());

        // create query options
        var options = getReviewQueryOptions(orderByEnum, scoreFilter, decodedCursor);

        // get reviews and add to model
        List<Review> reviews = reviewRepo.findByExternalIdWithPagination(externalId, options);
        modelMap.put("reviews", reviews);

        // add stats to model for display in JSP if includeStats is true.
        if(includeStats) addReviewStatsToModel(modelMap, externalId);

        // add score filter to model
        modelMap.put("scoreFilter", scoreFilter);

        // add current orderBy enum to model for display in JSP
        modelMap.put("orderByEnum", orderByEnum);

        // add ordering options to model
        modelMap.put("reviewListOrderOptions", orderByOptionsMap);

        // add formatters to model
        //addFormattersToModel(modelMap);

        return modelMap;
    }
}
