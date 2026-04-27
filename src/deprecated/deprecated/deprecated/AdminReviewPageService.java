package root.services.review.deprecated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.includes.ReviewQueryOptions;
import root.models.review.Review;
import root.repositories.review.ReviewRepository;

import java.util.*;
import java.util.function.Function;


/**
 * Service for handling business logic related to the admin review list page. This service is responsible for fetching
 * reviews from the database based on the provided status filter, and preparing the data for display in the JSP view.
 * It also adds additional data to the model, such as the total count of reviews for the given status filter, and a function to
 * convert strings to CSS identifiers for use in the JSP.
 */

@Service
public class AdminReviewPageService {
    private final static Function<String, String> fnToCssIdentifier = (s) -> {
        if (s == null || s.isEmpty()) return "";
        return root.includes.Utils.toCssIdentifier(s).toLowerCase();
    };

    private static Map<String, Object> reviewStatusFilterOptions = root.includes.Utils.linkedMap(
        "Godkjent", Review.REVIEW_STATUS_APPROVED,
        "Kontroll", Review.REVIEW_STATUS_PENDING,
        "Avvist", Review.REVIEW_STATUS_REJECTED,
        "Alle", -1
    );


    //@Autowired
    //ReviewService reviewService;

    @Autowired
    ReviewRepository reviewRepo;




/*
    public Map<String, Object> buildReviewListModelData
    (
        Integer orderByEnum,
        String statusFilter,
        LocalDate dateFilterStart,
        LocalDate dateFilterEnd,
        Integer dateFilterPreset,
        String pageCursorStr
    ) throws Exception {

        // decode reviewStatusFilter from CSV string to set of integers. If the filter contains -1, we want to include
        // all statuses, so we add all possible statuses to the filter set.
        Set<Integer> reviewStatusFilterSet = new HashSet<>(root.includes.Utils.parseCsvIntList(statusFilter));
        if (reviewStatusFilterSet.contains(-1) || reviewStatusFilterSet.isEmpty()) {
            // remove -1, if exists, and replace with list of all valid statuses
            reviewStatusFilterSet.remove(-1);
            reviewStatusFilterSet.addAll(Review.getValidReviewStatuses());
        }
        Logger.log("REVIEWSTATUSFILTERSET: " + reviewStatusFilterSet);


        // filter by date range or date filter preset
        ImmutableUnboundedDateRange<LocalDate> dateRangeFilter = null;

        if(dateFilterStart != null || dateFilterEnd != null) {
            // create date range filter based on provided start and end dates.
            // If one of them is null, it will be an unbounded range in that direction.
            dateRangeFilter = new ImmutableUnboundedDateRange<LocalDate>(dateFilterStart, dateFilterEnd);
        }

        if (dateFilterPreset != null && dateFilterPreset.compareTo(0) > 0) {
            // dateFilterPreset overrides dateFilterStart and dateFilterEnd if provided
            LocalDate presetStartDate = LocalDate.now().minusDays(dateFilterPreset);
            dateRangeFilter = new ImmutableUnboundedDateRange<LocalDate>(presetStartDate, null);
        }

        PageCursor cursor = PageCursorEncoder.parseOrDefault(pageCursorStr, AppConfig.ADMIN_DEFAULT_MAX_VISIBLE_REVIEWS);
        modelMap.put("pageCursor", cursor.encode());

        // make query options object and set filters
        ReviewQueryOptions options = new ReviewQueryOptions();
        options.setPageCursor(cursor);
        options.setDateFilterRange(dateRangeFilter);
        options.getStatusFilterSet().addAll(reviewStatusFilterSet);
        options.setOrderByEnum(orderByEnum);

 */

    /**
     * Builds the model data for the review list page in the admin dashboard. This method fetches reviews from the
     * database based on the provided status filter, and prepares the data for display in the JSP view.
     *
     * @param options
     * @return
     * @throws Exception
     */

    public Map<String, Object> buildReviewListModelData(ReviewQueryOptions options) throws Exception {
        Map<String, Object> modelMap = new HashMap<>();

        // add dump to model for display in JSP. This is just for demonstration purposes to show how to fetch all
        // reviews for a given externalId with pagination and sorting, and should be removed for production code.
        List<Review> reviews = reviewRepo.findByAnyExternalIdWithPagination(options);
        modelMap.put("reviews", reviews);

        // add ordering options to model
        modelMap.put("reviewStatusFilterOptions", reviewStatusFilterOptions);

        return modelMap;
    }
}
