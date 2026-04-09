package root.services;

import org.springframework.stereotype.Service;
import root.app.AppConfig;
import root.app.AppContext;
import root.app.ReviewQueryOptions;
import root.app.includes.PageCursor;
import root.app.includes.PageCursorEncoder;
import root.controllers.ControllerUtils;
import root.controllers.DefaultController;
import root.controllers.LotsOfUtils;
import root.models.Review;
import root.repositories.ReviewRepository;
import root.repositories.ReviewerRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

import static root.common.utils.Preconditions.checkArgument;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReviewPageService {
    static final ObjectMapper M = new ObjectMapper();


    /*
    lambda helper methods
     */
    private static double roundToHalf(double v) {
        return Math.round(v * 2.0) / 2.0;
    }

    // add a simple function to format double values to 2 decimals for display in JSP
    public static Function<Double, String> CSS_DOUBLE_FORMATTER_POINT_FIVE = (v) -> String.format(Locale.US, "%.1f", roundToHalf(v)).replace(".", "-");
    public static Function<Double, String> DOUBLE_FORMATTER_1 = (v) -> String.format(Locale.US, "%.1f", v);
    //private static Function<Double, String> DOUBLE_FORMATTER_2 = (v) -> String.format(Locale.US, "%.1f", v);

    // add a simple function to format double values to 2 decimals for display in JSP
    public static Function<Instant, String> DD_MM_YYYY_FORMATTER = v -> {
        if (v == null) return "";

        return DateTimeFormatter.ofPattern("dd/MM/yyyy")
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault())
            .format(v);
    };

    // add a simple function to format Instant values to "days ago" format for display in JSP
    public static Function<Instant, String> DAYS_AGO_FORMATTER = v -> {
        if (v == null) return "";

        long days = ChronoUnit.DAYS.between(v, Instant.now());
        return String.valueOf(Math.max(0, days));
    };


    private final ReviewRepository reviewRepo;
    private final ReviewerRepository reviewerRepo;
    private final ReviewService reviewService;

    public ReviewPageService(ReviewRepository reviewRepo, ReviewerRepository reviewerRepo, ReviewService reviewService) {
        this.reviewRepo = reviewRepo;
        this.reviewerRepo = reviewerRepo;
        this.reviewService = reviewService;
    }

    private int getApprovedReviewsTotalCount(String externalId) throws Exception{
        ReviewQueryOptions totalFilterCountOptions = new ReviewQueryOptions();
        totalFilterCountOptions.setPageCursor(new PageCursor(0, Integer.MAX_VALUE));
        totalFilterCountOptions.getStatusFilterSet().add(Review.REVIEW_STATUS_APPROVED);
        totalFilterCountOptions.setOrderByEnum(ReviewQueryOptions.OPTION_ORDER_NONE);

        return reviewRepo.countByExternalId(externalId, totalFilterCountOptions);
    }

    private void addReviewsOrderByOptionsToModel(Map<String, Object> modelMap, int currentOrderByEnum) {
        LinkedHashMap<String, Integer> reviewListOrderOptions = new LinkedHashMap<>();
        reviewListOrderOptions.put("Nyeste først", ReviewQueryOptions.OPTION_ORDER_BY_ID_DESC);
        reviewListOrderOptions.put("Eldste først", ReviewQueryOptions.OPTION_ORDER_BY_ID_ASC);
        reviewListOrderOptions.put("Score (høyeste først)", ReviewQueryOptions.OPTION_ORDER_BY_SCORE_DESC);
        reviewListOrderOptions.put("Score (laveste først)", ReviewQueryOptions.OPTION_ORDER_BY_SCORE_ASC);
        modelMap.put("reviewListOrderOptions", reviewListOrderOptions);
        modelMap.put("currentOrderByEnum", currentOrderByEnum);
    }

    public Map<String, Object> buildReviewListingPage(
        String externalId,
        String strEncodedCursor,
        int orderByEnum,
        String scoreFilter
    ) throws Exception {
        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put("scoreFilter", scoreFilter);
        //model.addAttribute("scoreFilterMin", scoreFilterMin);
        //model.addAttribute("scoreFilterMax", scoreFilterMax);

        // find all unique externalIds for reviews to display in the dropdown for quick navigation
        // only used in demonstration interface
        // TODO: remove for production code, should allways take an externalId as a parameter and not display
        //  a dropdown of all externalIds
        List<String> uniqueExternalIds = ControllerUtils.addSelectExternalIdPillData(modelMap, reviewRepo);


        if(AppConfig.TESTING_MODE) {
            // for quick insertion of reviews, we can generate random suggestions for display name, title and comment
            // TODO: remove for production code, should have empty form
            ControllerUtils.addDefaultNewReviewFormValues(modelMap);
        }


        // get tenant id or tenant name / something
        // TODO: figure out tenant handling and how to display tenant info in the interface.
        Long tenantId = AppContext.getSingleton().getTenantId();
        checkArgument(tenantId != null, "Tenant ID is not set in AppContext. This should never happen if the RequestContextFilter is working correctly.");
        modelMap.put("tenantId", tenantId);


        // real decoding of externalId from request URI for the /reviews/{externalId} route.
        // This allows for more complex routing and is more flexible than using a request parameter, but requires more
        // setup and handling in the controller. For simplicity, we are using a request parameter for the externalId
        // in this example, but in a real application you would likely want to use the path variable approach for
        // better routing and cleaner URLs.
        //String externalId = extractExternalIdFromRequest(req);
        //externalId = URLDecoder.decode(externalId, StandardCharsets.UTF_8);
        //Logger.log("External ID extracted from request: " + externalId);

        // get externalId from request parameter and validate. If not set, use the first unique externalId from
        // the database for demonstration purposes.
        // TODO: remove defaulting to first unique externalId for production code, should require an externalId to be
        //  provided and show an error if not provided
        if (externalId == null || externalId.isBlank()) {
            externalId = uniqueExternalIds.isEmpty() ? "\0?" : uniqueExternalIds.getFirst();
        }
        checkArgument(externalId != null && !externalId.isBlank(), "externalId must be non-null and non-empty");


        // add externalId to model for display in JSP and for use in form submission for new reviews
        modelMap.put("externalId", externalId);


        // decode cursor
        PageCursor decodedCursor = ControllerUtils.decodeOrCreateCursor(strEncodedCursor, AppConfig.CLIENT_DEFAULT_MAX_VISIBLE_REVIEWS);


        // build review query options based on request parameters.
        // This includes pagination, sorting, and filtering options for fetching reviews from the database.
        ReviewQueryOptions options = new ReviewQueryOptions();
        options.setPageCursor(decodedCursor);
        options.getStatusFilterSet().add(Review.REVIEW_STATUS_APPROVED);
        options.setOrderByEnum(orderByEnum);

        // add score filter to options if set
        if (scoreFilter != null && !scoreFilter.isBlank() && !scoreFilter.equals("-1")) {
            List<Integer> scoreFilterList = LotsOfUtils.parseCsvIntList(scoreFilter);
            options.getScoreFilterSet().addAll(scoreFilterList);
        }


        // add reviews to model for display in JSP
        List<Review> reviews = reviewRepo.findByExternalIdWithPagination(externalId, options);
        modelMap.put("reviews", reviews);


        // get score stats for the given externalId and add to model
        // this includes things like average score, total reviews, and score distribution for display in JSP
        var reviewStats = reviewService.getScoreStatsHelper(externalId, Review.REVIEW_STATUS_APPROVED, null);
        modelMap.put("reviewStats", reviewStats);


        // add total approved reviews count for the given externalId to model for display in JSP.
        //modelMap.put("totalFilteredCount", reviewStats.getTotalCount());


        // add ordering options to model
        addReviewsOrderByOptionsToModel(modelMap, orderByEnum);


        // add cursor to model
        // set query options for fetching reviews, including pagination and sorting. We are only fetching approved reviews for display.
        //ControllerUtils.addCursorToModel(modelMap, (int) reviewStats.getTotalCount(), decodedCursor);
        modelMap.put("pageCursor", PageCursorEncoder.encodeCursor(decodedCursor));


        // add formatters to model for display in JSP
        modelMap.put("dblFormatter1", DOUBLE_FORMATTER_1);
        modelMap.put("dateFormatter", DD_MM_YYYY_FORMATTER);
        modelMap.put("daysAgoFormatter", DAYS_AGO_FORMATTER);
        modelMap.put("dblFormatterCssPointFive", CSS_DOUBLE_FORMATTER_POINT_FIVE);

        // TODO END: get scores for aggregate display of score distribution and average score

        return modelMap;
    }

    public Map<String, Object> buildPartialReviewListingPage(
        String externalId,
        String strEncodedCursor,
        int orderByEnum,
        String scoreFilter
    ) throws Exception {
        Map<String, Object> modelMap = new HashMap<>();

        return modelMap;
    }
}
