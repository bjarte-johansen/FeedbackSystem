package root.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import root.app.AppConfig;
import root.controllers.helpers.ControllerConstantMaps;
import root.includes.logger.Logger;
import root.models.Review;
import root.app.ReviewQueryOptions;
//import root.repositories.ReviewerRepository;
import root.models.ReviewSettings;
import root.repositories.ReviewRepository;
import root.repositories.ReviewSettingsRepository;
import root.services.CachedReviewSettingsService;
import root.services.ReviewPageService;
import root.services.ReviewService;
import root.services.ReviewSettingsService;

import java.time.LocalDate;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNullElse;

//import static com.google.common.base.Preconditions.checkArgument;;

//import root.models.repositories.JdbcReviewRepository;

@Controller
public class ClientController {
    public static String INDEX_FILE = "client/index";

    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    ReviewService reviewService;

    @Autowired
    ReviewPageService reviewPageService;

    @Autowired
    ReviewSettingsRepository reviewSettingsRepo;
    @Autowired
    private CachedReviewSettingsService cachedReviewSettingsService;


    /**
     * Default route to show reviews for a default externalId. This is just for convenience and demonstration purposes,
     * and should be removed or redirected to a more appropriate page in production code.
     * TODO: remove or redirect to a more appropriate page in production code
     *
     * @param model
     * @param req
     * @return
     * @throws Exception
     */

    @GetMapping("/")
    public String index(Model model, HttpServletRequest req, RedirectAttributes ra) throws Exception {
        return "redirect:/api/reviews/list/html?showDemoPills=true&externalId=/invalid-path";
    }


    /**
     * Return JSON string corresponding to
     * @param externalId
     * @param cursor
     * @param orderByEnum
     * @param scoreFilter
     * @param startDateFilter
     * @param endDateFilter
     * @param numDaysFilter
     * @param includeStats
     * @param req
     * @return
     */

    @GetMapping({"/api/reviews/list/json"})
    @ResponseBody
    public Map<String, Object> showReviewsAsJson(
        @RequestParam(required = false) String externalId,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) Integer orderByEnum,
        @RequestParam(required = false) String scoreFilter,
        @RequestParam(required = false) String statusFilter,
        @RequestParam(required = false) LocalDate startDateFilter,
        @RequestParam(required = false) LocalDate endDateFilter,
        @RequestParam(required = false) Integer numDaysFilter,
        @RequestParam(required = false, defaultValue="false") boolean includeStats,

        // fake param to indicate default limit
        @RequestParam(defaultValue = ("" + AppConfig.CLIENT_DEFAULT_MAX_VISIBLE_REVIEWS)) int defaultLimit,

        // request
        HttpServletRequest req
    ) {
        final Map<String, Object> vm = new HashMap<>();

        final ReviewSettings reviewCfg = cachedReviewSettingsService.findOrCreateByExternalId(
            Objects.requireNonNullElse(externalId, "")
        );

        vm.put("reviewConfig", root.includes.Utils.linkedMap(
            "enableListing", reviewCfg.getEnableListing(),
            "enableSubmit", reviewCfg.getEnableSubmit()
        ));

        if(reviewCfg.getEnableListing()) {
            ReviewQueryOptions qo = ReviewQueryOptionsParser.parseRequest(req, defaultLimit);
            vm.putAll(reviewPageService.buildReviewListingModelData(externalId, qo, includeStats));

            // put in names/lookups etc
            vm.put("constants", ControllerConstantMaps.ALL_CONSTANTS);
        }

        return vm;
    }


    /**
     * Main route to show reviews for a given externalId. This is the main route of the application and is used to
     * display reviews for a given externalId with pagination and sorting. It also displays aggregate score stats for
     * the given externalId.
     *
     * @param externalId
     * @param cursor
     * @param orderByEnum
     * @param scoreFilter
     * @param startDateFilter
     * @param endDateFilter
     * @param numDaysFilter
     * @param includeStats
     * @param model
     * @param req
     * @return
     */

    @GetMapping("/api/reviews/list/html")
    public String showReviews(
        @RequestParam(required = false) String externalId,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) Integer orderByEnum,
        @RequestParam(required = false) String scoreFilter,
        @RequestParam(required = false) String statusFilter,
        @RequestParam(required = false) LocalDate startDateFilter,
        @RequestParam(required = false) LocalDate endDateFilter,
        @RequestParam(required = false) Integer numDaysFilter,
        @RequestParam(required = false) Boolean showDemoPills,
        @RequestParam(required = false, defaultValue="true") boolean includeStats,
        @RequestParam(required = false) Boolean realApi,
        Model model,

        // fake param to indicate default limit
        @RequestParam(defaultValue = ("" + AppConfig.CLIENT_DEFAULT_MAX_VISIBLE_REVIEWS)) final int defaultLimit,

        // request
        HttpServletRequest req
    ) {
        final Map<String, Object> vm = new LinkedHashMap<>();

        final ReviewSettings reviewCfg = cachedReviewSettingsService.findOrCreateByExternalId(
            Objects.requireNonNullElse(externalId, "")
        );

        vm.put("reviewConfig", root.includes.Utils.linkedMap(
            "enableListing", reviewCfg.getEnableListing(),
            "enableSubmit", reviewCfg.getEnableSubmit()
        ));

        if(reviewCfg.getEnableListing()) {
            ReviewQueryOptions qo = ReviewQueryOptionsParser.parseRequest(req, defaultLimit);
            vm.putAll(reviewPageService.buildReviewListingModelData(externalId, qo, includeStats));

            // testing values
            if (Boolean.TRUE.equals(showDemoPills)) {
                // add test data for select externalId pill, in production this should be dynamically loaded based on
                // existing reviews in the database
                Utils.addSelectExternalIdPillData(vm, reviewRepo);
            }

            // put in names/lookups etc
            vm.put("constants", ControllerConstantMaps.ALL_CONSTANTS);

            // add ordering options to model
            vm.put("reviewListOrderOptions", ReviewPageService.orderByOptionsMap());
        }

        vm.put("json", root.includes.Utils.toJson(vm));

        model.addAllAttributes(vm);
        return Boolean.TRUE.equals(realApi) ? (INDEX_FILE + "-partial") : INDEX_FILE;
    }


    /**
     * Gets a review in json format {id: .., comment: "...", etc}
     * will return 404, empty string if not found,
     *
     * @param reviewId
     * @return
     * @throws Exception
     */

    @ResponseBody
    @GetMapping("/api/review/{reviewId}/json")
    public ResponseEntity<Review> renderReviewAsJson(@PathVariable long reviewId) throws Exception {
        return reviewRepo.findById(reviewId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }


    /**
     * API endpoint to add a like to a review. Can be called only once for a user session.
     *
     * @param reviewId
     * @return
     */

    @PostMapping("/api/review/{reviewId}/like")
    public ResponseEntity<Void> addReviewLike(
        @PathVariable long reviewId,
        HttpSession session,
        HttpServletRequest req
    ) {
        return (reviewService.submitVote(reviewId, Review.VOTE_UP, session.getId(), req.getRemoteAddr()))
            ? ResponseEntity.ok().build()
            : ResponseEntity.noContent().build();
    }


    /**
     * API endpoint to add a dislike to a review. Can be called only once for a user session.
     *
     * @param reviewId
     * @return
     */

    @PostMapping("/api/review/{reviewId}/dislike")
    public ResponseEntity<Void> addReviewDislike(
        @PathVariable long reviewId,
        HttpSession session,
        HttpServletRequest req
    ) {
        return (reviewService.submitVote(reviewId, Review.VOTE_DOWN, session.getId(), req.getRemoteAddr()))
            ? ResponseEntity.ok().build()
            : ResponseEntity.noContent().build();
    }
}