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
import root.app.includes.PageCursor;
import root.app.includes.PageCursorEncoder;
import root.common.utils.FunnyUserNameGenerator;
import root.common.utils.IpsumLoremGenerator;
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
    ReviewVoteRepository reviewVoteRepo;

    @Autowired
    ReviewPageService reviewPageService;



    /**
     * Simple route to display an error page. This is just for demonstration purposes and should be replaced with proper
     * error handling in production code.
     */

    @GetMapping("/error")
    public String error() {
        return "error";
    }





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
        return "redirect:/show-reviews?externalId=/invalid-path";
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
            .withStatus(true, "Session cleared successfully.")
            .redirect(ra, "/");
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

    @GetMapping("/show-reviews")
    public String showReviews(
        @RequestParam String externalId,
        @RequestParam(name = "cursor", defaultValue = "") String encodedCursor,
        @RequestParam(name = "orderByEnum", defaultValue = ("" + ReviewQueryOptions.OPTION_ORDER_BY_ID_DESC)) int orderByEnum,
        @RequestParam(name = "scoreFilter", defaultValue = "-1") String scoreFilter,
        Model model,
        HttpServletRequest req
    )  throws Exception {
        Map<String, Object> vm = reviewPageService.buildReviewListingPage(
            externalId,
            encodedCursor,
            orderByEnum,
            scoreFilter
            );

        model.addAllAttributes(vm);

        return "index";
    }





    /**
     * API endpoint to generate HTML for a single review by id.
     *
     * @param reviewId
     * @param model
     * @return
     * @throws Exception
     */
    @GetMapping("/api/review/build-html/{reviewId}")
    public String makeReviewHtml(@PathVariable long reviewId, Model model) throws Exception {
        Review review = reviewRepo.findById(reviewId).orElse(null);
        if (review == null) return "error";

        model.addAttribute("review", review);

        // add externalId to model for display in JSP and for use in form submission for new reviews
        AppContext appContext = AppContext.getSingleton();
        model.addAttribute("tenantId", appContext.getTenantId());

        // add formatters to model for display in JSP
        model.addAttribute("daysAgoFormatter", ReviewPageService.DAYS_AGO_FORMATTER);

        return "pretty-review.partial";
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


    /*
    public void actualInsertReviewCode(){
        // create reviewer
        String passwordSalt = PasswordService.generateSalt();
        String passwordHash = PasswordService.hash(password, passwordSalt);

        var reviewer = new Reviewer();
        reviewer.setTenantId(tenantId);
        reviewer.setEmail(email);
        reviewer.setDisplayName(displayName);
        reviewer.setPasswordSalt(passwordSalt);
        reviewer.setPasswordHash(passwordHash);
        reviewer.setCreatedAt(Instant.now());

        // persist to database
        reviewerRepo.create(reviewer);

        // create review
        var review = new Review(tenantId, externalId, reviewer.getId(), reviewer.getDisplayName(), score, comment);

        // persist to database
        reviewRepository.create(review);
    }
    */
}
