package root.controllers;

import jakarta.servlet.http.HttpServletRequest;
//import org.apache.coyote.BadRequestException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import root.app.AppConfig;
import root.app.AppContext;
import root.app.ReviewQueryOptions;
import root.app.includes.PageCursor;
import root.includes.Utils;
import root.models.Review;
import root.repositories.ReviewRepository;
import root.repositories.ReviewerRepository;
import root.services.ReviewService;

import org.springframework.security.access.AccessDeniedException;
//import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

@Controller
public class AdminController {
    private final LinkedHashMap<String, Object> reviewStatusFilterOptions = getReviewStatusFilterOptions();

    // declare repos
    private final ReviewRepository reviewRepo;
    private final ReviewerRepository reviewerRepo;
    private final ReviewService reviewService;
    private final AppContext appContext;


    /**
     * Constructor with dependency injection.
     *
     * @param reviewRepo
     * @param reviewerRepo
     * @param reviewService
     */

    public AdminController(ReviewRepository reviewRepo, ReviewerRepository reviewerRepo, ReviewService reviewService, AppContext appContext) {
        this.reviewRepo = reviewRepo;
        this.reviewerRepo = reviewerRepo;
        this.reviewService = reviewService;
        this.appContext = appContext;
    }


    /*
    helper methods
     */

    private LinkedHashMap<String, Object> getReviewStatusFilterOptions() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("Venter", Review.REVIEW_STATUS_PENDING);
        map.put("Godkjent", Review.REVIEW_STATUS_APPROVED);
        map.put("Avvist", Review.REVIEW_STATUS_REJECTED);
        map.put("Alle", -1);
        return map;
    }

    private void requireAdministratorRole() {
        if (!appContext.isAdministrator()) {
            throw new AccessDeniedException("User does not have administrator privileges");
        }
    }

    private ResponseEntity<Void> markReviewWithStatus(long reviewId, int newStatus) throws Exception {
        //requireAdministratorRole();

        reviewService.setReviewStatus(reviewId, newStatus);

        return ResponseEntity.ok().build();
    }




    /*
    API endpoints
    TODO: test these endpoints with Postman or similar tool, and implement a simple admin UI if time permits.
     */


    @GetMapping("/admin-dashboard")
    public String adminDashboard(Model model) throws Exception {
        return showPendingReviews("", model);
    }


    @GetMapping("/admin-dashboard/pending-review")
    public String showPendingReviews(
        @RequestParam(defaultValue = "-1") String statusFilter,
        Model model
    ) throws Exception {
        return showReviewList("" + Review.REVIEW_STATUS_PENDING, model);
    }


    @GetMapping("/admin/reviews")
    public String showReviewList(
        @RequestParam(defaultValue = "-1") String statusFilter,
        Model model
    ) throws Exception{
        //requireAdministratorRole();

        // decode reviewStatusFilter from CSV string to set of integers. If the filter contains -1, we want to include
        // all statuses, so we add all possible statuses to the filter set.
        // if status = -1, we want to include all statuses, so we add all possible statuses to the filter set.
        // If the filter is empty, we also want to include all statuses.

        Set<Integer> reviewStatusFilterSet = new HashSet<>(Utils.parseCsvIntList(statusFilter));
        if(reviewStatusFilterSet.contains(-1) || reviewStatusFilterSet.isEmpty()){
            reviewStatusFilterSet.remove(-1);

            reviewStatusFilterSet.addAll(List.of(
                Review.REVIEW_STATUS_PENDING,
                Review.REVIEW_STATUS_APPROVED,
                Review.REVIEW_STATUS_REJECTED)
            );
        }

        // add data to model for select externalId pill in admin dashboard JSP. This will be used to filter reviews by externalId.
        ControllerUtils.addSelectExternalIdPillData(model.asMap(), reviewRepo);

        // create dump options for fetching all reviews for the given externalId without pagination and with a
        // specific sorting order.
        ReviewQueryOptions options = new ReviewQueryOptions();
        options.setPageCursor(new PageCursor(0, Integer.MAX_VALUE));
        options.getStatusFilterSet().addAll(reviewStatusFilterSet);
        options.setOrderByEnum(ReviewQueryOptions.OPTION_ORDER_BY_STATUS_PENDING_FIRST);

        if(reviewStatusFilterSet.size() == 1) {
            options.setOrderByEnum(ReviewQueryOptions.OPTION_ORDER_BY_ID_DESC);
        }

        // add dump to model for display in JSP. This is just for demonstration purposes to show how to fetch all
        // reviews for a given externalId with pagination and sorting, and should be removed for production code.
        List<Review> reviews = reviewRepo.findByAnyExternalIdWithPagination(options);
        model.addAttribute("reviews", reviews);

        // add total count of reviews for the given externalId and status filter to model for display in JSP.
        int totalStatusFilterCount = reviewRepo.countByAnyExternalId(options);
        model.addAttribute("totalStatusFilterCount", totalStatusFilterCount);

        Function<String, String> toCssIdentifier = (s) -> {
            if(s == null || s.isEmpty()) return "";
            return Utils.toCssIdentifier(s).toLowerCase();
        };
        model.addAttribute("toCssIdentifier", toCssIdentifier);

        // add ordering options to model
        model.addAttribute("reviewStatusFilterOptions", reviewStatusFilterOptions);
        model.addAttribute("currentReviewStatusFilter", statusFilter);

        return "admin/admin-dashboard";
    }



    /**
     * API endpoint to mark a review as approved by id.
     *
     * @param reviewId
     * @return
     * @throws Exception
     */

    @PostMapping("/api/review/mark-approved")
    public ResponseEntity<Void> markReviewAsApproved(@RequestParam long reviewId) throws Exception {
        requireAdministratorRole();

        return markReviewWithStatus(reviewId, Review.REVIEW_STATUS_APPROVED);
    }



    /**
     * API endpoint to mark a review as rejected by id.
     * @param reviewId
     * @return
     * @throws Exception
     */

    @PostMapping("/api/review/mark-rejected")
    public ResponseEntity<Void> markReviewAsRejected(@RequestParam long reviewId) throws Exception {
        requireAdministratorRole();

        return markReviewWithStatus(reviewId, Review.REVIEW_STATUS_REJECTED);
    }



    /**
     * API endpoint to mark a review as pending by id.
     *
     * @param reviewId
     * @return
     * @throws Exception
     */

    @PostMapping("/api/review/mark-pending")
    public ResponseEntity<Void> markReviewAsPending(@RequestParam Long reviewId) throws Exception {
        requireAdministratorRole();

        return markReviewWithStatus(reviewId, Review.REVIEW_STATUS_PENDING);
    }



    /**
     * API endpoint to delete a review by id.
     *
     * @param tenantId
     * @param reviewId
     * @param req
     * @return
     * @throws Exception
     */

    // todo: fix route name (maybe /api/reviews/{reviewId} or similar), and add authorization check for tenantId
    //  (should match the tenant of the review, or be a super admin)
    @DeleteMapping("/api/review/delete")
    public ResponseEntity<Void> deleteReviewApi(
        @RequestParam Long tenantId,
        @RequestParam Long reviewId,
        HttpServletRequest req
    ) throws Exception {
        requireAdministratorRole();

        try {
            // validate parameters
            if((tenantId.compareTo(0L) <= 0) || (reviewId.compareTo(0L) <= 0))
                throw new BadRequestException();

            reviewService.deleteById(reviewId);

            return ResponseEntity.ok().build();
        } catch(Exception e) {
            if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}