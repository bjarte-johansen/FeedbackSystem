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
import root.models.review.Review;
//import root.repositories.ReviewerRepository;
import root.repositories.review.ReviewRepository;
import root.services.review.CachedReviewSettingsService;
import root.services.review.ClientReviewPageService;
import root.services.review.ReviewService;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

//import static com.google.common.base.Preconditions.checkArgument;;

//import root.models.repositories.JdbcReviewRepository;

@Controller
public class ClientController {
    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    ReviewService reviewService;

    @Autowired
    ClientReviewPageService clientReviewPageService;
/*
    @Autowired
    ReviewSettingsRepository reviewSettingsRepo;
 */

    @Autowired
    private CachedReviewSettingsService cachedReviewSettingsService;


    /**
     * Default route set up for demo
     * @param model
     * @param req
     * @param ra
     * @return
     * @throws Exception
     */
    @GetMapping("/")
    public String index(Model model, HttpServletRequest req, RedirectAttributes ra) throws Exception {
        return "demo/demo";
    }



    /**
     * Return JSON string corresponding to
     * @param includeStats
     * @param includeJsonAsAttribute
     * @param defaultLimit
     * @param req
     * @return
     */

    @GetMapping({"/api/reviews/list/json"})
    @ResponseBody
    public Map<String, Object> showReviewsAsJson(
        /*
        @RequestParam(required = false) String externalId,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) Integer orderByEnum,
        @RequestParam(required = false) String scoreFilter,
        @RequestParam(required = false) String statusFilter,
        @RequestParam(required = false) LocalDate startDateFilter,
        @RequestParam(required = false) LocalDate endDateFilter,
        @RequestParam(required = false) Integer numDaysFilter,
        @RequestParam(required = false) Boolean showDemoPills,
        @RequestParam(required = false) Boolean realApi,
         */
        @RequestParam(defaultValue = "false") boolean includeStats,
        @RequestParam(defaultValue = "false") Boolean includeJsonAsAttribute,
        @RequestParam(defaultValue = ("" + AppConfig.CLIENT_DEFAULT_MAX_VISIBLE_REVIEWS)) int defaultLimit,
        HttpServletRequest req
    ) {
        Map<String, Object> vm = clientReviewPageService.buildPageData(
            includeStats,
            includeJsonAsAttribute,
            defaultLimit,
            req
        );

        return vm;
    }


    /**
     * Main route to show reviews for a given externalId. This is the main route of the application and is used to
     * display reviews for a given externalId with pagination and sorting. It also displays aggregate score stats for
     * the given externalId. This will render HTML and give showReviewsAsJson as json in a attribute or javascript
     * snippet to extract information for page viewing.
     *
     * @param includeStats
     * @param includeJsonAsAttribute
     * @param defaultLimit
     * @param model
     * @param req
     * @return
     */

    @GetMapping("/api/reviews/list/html")
    public String showReviewsAsHtml(
        /*
        @RequestParam(required = false) String externalId,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) Integer orderByEnum,
        @RequestParam(required = false) String scoreFilter,
        @RequestParam(required = false) String statusFilter,
        @RequestParam(required = false) LocalDate startDateFilter,
        @RequestParam(required = false) LocalDate endDateFilter,
        @RequestParam(required = false) Integer numDaysFilter,
        @RequestParam(required = false) Boolean showDemoPills,
        @RequestParam(required = false) Boolean realApi,
         */
        @RequestParam(defaultValue = "true") boolean includeStats,
        @RequestParam(defaultValue = "true") Boolean includeJsonAsAttribute,
        @RequestParam(defaultValue = ("" + AppConfig.CLIENT_DEFAULT_MAX_VISIBLE_REVIEWS)) final int defaultLimit,
        HttpServletRequest req,
        Model model
    ) {
        // use same route as other controller so we know they are 100% compatible
        Map<String, Object> vm = showReviewsAsJson(
            includeStats,
            includeJsonAsAttribute,
            defaultLimit,
            req
            );

        model.addAllAttributes(vm);
        return "client/client";
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