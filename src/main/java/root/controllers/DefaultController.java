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
import root.app.AppContext;
import root.controllers.dto.NewReviewForm;
import root.models.Review;
import root.models.Reviewer;
import root.app.ReviewQueryOptions;
import root.repositories.ReviewVoteRepository;
import root.repositories.ReviewerRepository;
import root.repositories.ReviewRepository;
import root.services.ReviewPageService;
import root.services.ReviewService;

import java.time.Instant;
import java.util.*;
import java.util.List;

import static root.common.utils.Preconditions.checkArgument;
//import root.models.repositories.JdbcReviewRepository;


@Controller
public class DefaultController {
    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    ReviewerRepository reviewerRepo;

    @Autowired
    ReviewService reviewService;

    @Autowired
    ReviewPageService reviewPageService;


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
        return "redirect:/reviews/build-html?externalId=/invalid-path";
    }


    /**
     * Clear session route for testing purposes. This allows us to clear the session and all associated data, such as
     * review votes, for testing the like/dislike functionality without having to wait for the session to expire.
     *
     * @param session
     * @param ra
     * @return
     * @throws Exception
     */

    @GetMapping("/clear-session")
    public String clearSession(HttpSession session, RedirectAttributes ra) throws Exception {
        session.invalidate();

        return ControllerHelper.create()
            .with(ra)
            .withStatus(true, "Session cleared successfully.")
            .redirect("/");
    }


    /**
     * Main route to show reviews for a given externalId. This is the main route of the application and is used to
     * display reviews for a given externalId with pagination and sorting. It also displays aggregate score stats for
     * the given externalId.
     *
     * @param externalId
     * @param encodedCursor
     * @param model
     * @param req
     * @return
     * @throws Exception
     */

    @GetMapping("/reviews/build-html")
    public String showReviews(
        @RequestParam String externalId,
        @RequestParam(name = "cursor", defaultValue = "") String encodedCursor,
        @RequestParam(name = "orderByEnum", defaultValue = ("" + ReviewQueryOptions.OPTION_ORDER_BY_ID_DESC)) int orderByEnum,
        @RequestParam(name = "scoreFilter", defaultValue = "-1") String scoreFilter,
        Model model,
        HttpServletRequest req
    ) throws Exception {
        Map<String, Object> vm = reviewPageService.buildReviewListingPage(
            externalId, encodedCursor, orderByEnum, scoreFilter, // filters
            req,
            true // include stats
        );

        // add test data for select externalId pill, in production this should be dynamically loaded based on existing
        // reviews in the database
        ControllerUtils.addSelectExternalIdPillData(vm, reviewRepo);

        // add default "new review" form values for quick testing of form submission
        if (AppConfig.TESTING_MODE) {
            // TODO: remove for production code, should have empty form
            ControllerUtils.addDefaultNewReviewFormValues(vm);
        }

        model.addAllAttributes(vm);

        return "client/index";
    }


    /**
     * API endpoint to generate HTML for a list of reviews based on the given filters. This is used for the "Load more"
     * functionality on the frontend, where the frontend can call this endpoint with the appropriate filters and
     * pagination cursor to get the next page of reviews as HTML to append to the existing list. This allows us to reuse
     * the same HTML rendering logic for both the initial page load and subsequent "Load more" requests, ensuring
     * consistency in the review display and reducing code duplication.
     *
     * @param externalId
     * @param encodedCursor
     * @param orderByEnum
     * @param scoreFilter
     * @param model
     * @param req
     * @return
     * @throws Exception
     */

    @GetMapping("/api/reviews/build-html")
    public String renderReviewsAsHtml(
        @RequestParam String externalId,
        @RequestParam(name = "cursor", defaultValue = "") String encodedCursor,
        @RequestParam(name = "orderByEnum", defaultValue = ("" + ReviewQueryOptions.OPTION_ORDER_BY_ID_DESC)) int orderByEnum,
        @RequestParam(name = "scoreFilter", defaultValue = "-1") String scoreFilter,
        Model model,
        HttpServletRequest req
    ) throws Exception {
        Map<String, Object> vm = reviewPageService.buildReviewListingPage(
            externalId, encodedCursor, orderByEnum, scoreFilter, // filters
            req,
            false // do not include stats
        );

        model.addAllAttributes(vm);

        return "client/pretty-review-list.partial";
    }


    /**
     * API endpoint to generate HTML for a single review by id.
     *
     * @param reviewId
     * @param model
     * @return
     * @throws Exception
     */

    @GetMapping("/api/review/{reviewId}/build-html")
    public String makeReviewHtml(@PathVariable long reviewId, Model model) throws Exception {
        Review review = reviewRepo.findById(reviewId).orElse(null);
        if (review == null) return "error";

        model.addAttribute("review", review);

        // add formatters to model for display in JSP
        model.addAttribute("daysAgoFormatter", ReviewPageService.daysAgoFormatter);

        return "client/pretty-review.partial";
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
        if (reviewService.submitVote(reviewId, Review.VOTE_UP, session.getId(), req.getRemoteAddr())) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.noContent().build();
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
        if (reviewService.submitVote(reviewId, Review.VOTE_DOWN, session.getId(), req.getRemoteAddr())) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.noContent().build();
    }


    /**
     * API endpoint to submit a new review. For simplicity, we are using request parameters for all input, but in a real
     * application you would likely want to use a request body with a DTO object for better structure and validation.
     *
     * @param form
     * @return
     * @throws Exception
     */

    @PostMapping("/api/submit-review")
    public ResponseEntity<Void> submitReview(
        @ModelAttribute NewReviewForm form
    ) throws Exception {
        List<String> errors = NewReviewForm.validate(form, new ArrayList<String>());
        checkArgument(errors.isEmpty(), errors.toString());

        Reviewer reviewer = reviewerRepo.findByEmail(form.email()).orElse(null);
        checkArgument(reviewer != null, "Reviewer not found");

        Review review = new Review();
        review.setAuthorName(form.displayName());
        review.setScore(form.score());
        review.setComment(form.comment());
        review.setExternalId(form.externalId());
        review.setCreatedAt(Instant.now());
        review.setTitle(form.title());
        review.setStatus(Review.REVIEW_STATUS_PENDING);

        if (AppConfig.AUTO_APPROVE_NEW_REVIEWS /* NOTE: should be set to false for production */) {
            review.setStatus(Review.REVIEW_STATUS_APPROVED);
        }

        reviewRepo.save(review);

        return ResponseEntity.ok().build();
    }
}