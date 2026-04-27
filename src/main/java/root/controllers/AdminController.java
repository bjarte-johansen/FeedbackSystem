package root.controllers;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import root.annotations.AdminOnly;
import root.app.AppConfig;
import root.controllers.dto.ReviewSettingsDto;
import root.models.review.Review;
import root.repositories.review.ReviewSettingsRepository;
import root.services.review.ClientReviewPageService;
import root.services.review.ReviewService;
import root.services.ReviewSettingsService;

import java.time.LocalDate;
import java.util.Map;

import static com.google.common.base.Preconditions.*;


/**
 * Class to handle admin routes
 * <p>
 * Routes for letting one edit administrator settings have intentionally been left out in this version. Particularly
 * the editing of domains would have to be isolated so that no one delete their domain -> tenant mapping, as it would
 * cause lookups to stop working
 * <p>
 * Only rudimentary ways of editing review capabilities such as enableListing and enableSubmit for specific review-pages
 * have been left.
 */

@Controller
public class AdminController {
    // declare repos
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewSettingsService reviewSettingsService;

    @Autowired
    private ReviewSettingsRepository reviewSettingsRepo;

    //@Autowired
    //private TenantResolver tenantResolver;

    @Autowired
    private ClientReviewPageService clientReviewPageService;


    /*
    helper methods
     */

    @AdminOnly
    private ResponseEntity<Void> markReviewWithStatus(long reviewId, int newStatus) {
        checkArgument(reviewId > 0, "Invalid review Id");

        int affectedRows = reviewService.setReviewStatus(reviewId, newStatus);

        return (affectedRows == 0)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok().build();
    }


    /*
    API endpoints
    TODO: test these endpoints with Postman or similar tool, and implement a simple admin UI if time permits.
     */


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
     */

    @AdminOnly
    @GetMapping({"/admin/dashboard", "/admin", "/admin/", "/admin/dashboard/reviews"})
    public String showFilteredReviews(
        @RequestParam(defaultValue = "/invalid-path") String externalId,
        @RequestParam(required = false) Integer orderByEnum,
        @RequestParam(required = false) String statusFilter,
        @RequestParam(required = false) LocalDate dateFilterStart,
        @RequestParam(required = false) LocalDate dateFilterEnd,
        @RequestParam(required = false) Integer dateFilterPreset,
        @RequestParam(name = "cursor", required = false, defaultValue="") String pageCursorStr,
        HttpServletRequest req,
        Model model
    ) {
        Map<String, Object> vm = clientReviewPageService.buildPageData(
            false,
            true,
            AppConfig.ADMIN_DEFAULT_MAX_VISIBLE_REVIEWS,
            req
        );
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
    @PatchMapping("/api/review/mark-approved")
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
    @PatchMapping("/api/review/mark-rejected")
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
    @PatchMapping("/api/review/mark-pending")
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


    /**
     * create edit reviewing settings list form
     * @param model
     * @return
     */

    @AdminOnly
    @GetMapping("/api/review/settings/list")
    public String showListForReviewSettings(Model model) {
        var reviewSettingsList = reviewSettingsRepo.findAll()
            .stream()
            .sorted((a, b) -> a.getId().compareTo(b.getId()))
            .toList();
        model.addAttribute("reviewSettingsList", reviewSettingsList);
        return "admin/review-settings-list";
    }


    /**
     * save review settings form
     * @param dto
     * @return
     */

    @AdminOnly
    @PutMapping("/api/review/settings/list")
    public ResponseEntity<Void> saveReviewSettings(
        @RequestBody ReviewSettingsDto dto
    ){
        checkArgument(dto.externalId() != null, "External Id cannot be empty");

        // we dont do any validation here
        // we use externalId instead of id
        var reviewSettings = reviewSettingsService.findOrCreateByExternalId(dto.externalId());
        reviewSettings.setName(dto.name());
        reviewSettings.setEnableSubmit(Boolean.parseBoolean(dto.enableSubmit()));
        reviewSettings.setEnableListing(Boolean.parseBoolean(dto.enableListing()));
        reviewSettingsRepo.save(reviewSettings);
        return ResponseEntity.ok().build();
    }
}