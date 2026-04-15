package root.controllers;

//import org.apache.coyote.BadRequestException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import root.annotations.AdminOnly;
import root.app.ReviewQueryOptions;
import root.includes.ImmutableUnboundedDateRange;
import root.includes.Utils;
import root.models.Review;
import root.services.AdminReviewPageService;
import root.services.ReviewService;

//import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.*;

@Controller
public class AdminController {
    // declare repos
    private final ReviewService reviewService;
    private final AdminReviewPageService adminReviewPageService;


    /*
    helper methods
     */

    @AdminOnly
    private ResponseEntity<Void> markReviewWithStatus(long reviewId, int newStatus) throws Exception {
        checkArgument(reviewId > 0, "Invalid review Id");

        reviewService.setReviewStatus(reviewId, newStatus);

        return ResponseEntity.ok().build();
    }


    /**
     * Constructor with dependency injection.
     *
     * @param reviewService
     * @param adminReviewPageService
     */

    public AdminController(ReviewService reviewService, AdminReviewPageService adminReviewPageService) {
        this.reviewService = reviewService;
        this.adminReviewPageService = adminReviewPageService;
    }



    /*
    API endpoints
    TODO: test these endpoints with Postman or similar tool, and implement a simple admin UI if time permits.
     */

    /**
     * Shows the admin dashboard page with a list of reviews filtered by status. The status filter is passed as a query
     * parameter, and defaults to -1 (which means all reviews). The method uses the AdminReviewPageService to build the
     * model data for the view, and adds it to the model before returning the view name.
     *
     * @param statusFilter
     * @param model
     * @return
     * @throws Exception
     */

    @AdminOnly
    @GetMapping("/admin/dashboard")
    public String showDashboard(
        @RequestParam(defaultValue = "-1") String statusFilter,
        @RequestParam(name="cursor", defaultValue = "") String cursorStr,
        Model model) throws Exception {
        return showFilteredReviews(/*orderEnum */0, statusFilter, null, null, 0, cursorStr, model);
    }


    /**
     * Shows the admin dashboard page with a list of reviews filtered by status. The status filter is passed as a query
     * parameter, and defaults to -1 (which means all reviews). The method uses the AdminReviewPageService to build the
     * model data for the view, and adds it to the model before returning the view name.
     * <p>
     * Note that dateFilterPreset overrides dateFilterStart and dateFilterEnd if provided, so we check for that and set
     * the dateRangeFilter accordingly.
     *
     * @param statusFilter
     * @param dateFilterStart
     * @param dateFilterEnd
     * @param dateFilterPreset
     * @param model
     * @return
     * @throws Exception
     */

    @AdminOnly
    @GetMapping("/admin/dashboard/reviews")
    public String showFilteredReviews(
        @RequestParam(defaultValue = "-1") int orderByEnum,
        @RequestParam(defaultValue = "-1") String statusFilter,
        @RequestParam(required = false) LocalDate dateFilterStart,
        @RequestParam(required = false) LocalDate dateFilterEnd,
        @RequestParam(required = false) Integer dateFilterPreset,
        @RequestParam(name = "cursor", required = false, defaultValue="") String pageCursorStr,
        Model model) throws Exception {

        // build model data for the view using the service
        var vm = adminReviewPageService.buildReviewListModelData(orderByEnum, statusFilter, dateFilterStart, dateFilterEnd, dateFilterPreset, pageCursorStr);

        model.addAllAttributes(vm);
        return "admin/admin-dashboard";
    }


    /**
     * API endpoint to mark a review as approved by id.
     *
     * @param reviewId
     * @return
     * @throws Exception
     */

    @AdminOnly
    @PostMapping("/api/review/mark-approved")
    public ResponseEntity<Void> markReviewAsApproved(@RequestParam long reviewId) throws Exception {
        return markReviewWithStatus(reviewId, Review.REVIEW_STATUS_APPROVED);
    }


    /**
     * API endpoint to mark a review as rejected by id.
     *
     * @param reviewId
     * @return
     * @throws Exception
     */

    @AdminOnly
    @PostMapping("/api/review/mark-rejected")
    public ResponseEntity<Void> markReviewAsRejected(@RequestParam long reviewId) throws Exception {
        return markReviewWithStatus(reviewId, Review.REVIEW_STATUS_REJECTED);
    }


    /**
     * API endpoint to mark a review as pending by id.
     *
     * @param reviewId
     * @return
     * @throws Exception
     */

    @AdminOnly
    @PostMapping("/api/review/mark-pending")
    public ResponseEntity<Void> markReviewAsPending(@RequestParam long reviewId) throws Exception {
        return markReviewWithStatus(reviewId, Review.REVIEW_STATUS_PENDING);
    }


    /**
     * API endpoint to delete a review by id.
     *
     * @param reviewId
     * @return
     * @throws Exception
     */

    @AdminOnly
    @DeleteMapping("/api/review/delete")
    public ResponseEntity<Void> deleteReviewApi(@RequestParam long reviewId) throws Exception {
        checkArgument(reviewId > 0, "Invalid review ID: " + reviewId);

        reviewService.deleteById(reviewId);

        return ResponseEntity.ok().build();
    }
}