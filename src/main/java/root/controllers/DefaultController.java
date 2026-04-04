package root.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import root.app.AppConfig;
import root.app.includes.PageCursor;
import root.app.includes.PageCursorEncoder;
import root.common.utils.FunnyUserNameGenerator;
import root.common.utils.IpsumLoremGenerator;
import root.controllers.dto.NewReviewForm;
import root.logger.Logger;
import root.models.Review;
import root.models.Reviewer;
import root.app.ReviewQueryOptions;
import root.repositories.ReviewerRepository;
import root.repositories.ReviewRepository;
import root.services.ReviewService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.function.Function;
//import root.models.repositories.JdbcReviewRepository;


@Controller
public class DefaultController {
    //private static final Logger log = new Logger();
    public static boolean DEBUG_ERRORS = true;

    // TODO: must be false for production
    public static boolean AUTO_APPROVE_NEW_REVIEWS = true;

    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    ReviewerRepository reviewerRepo;

    @Autowired
    ReviewService reviewService;

    private void addDefaultNewReviewFormValues(Model model){
        model.addAttribute("displayNameSuggestion", FunnyUserNameGenerator.generate());
        model.addAttribute("titleSuggestion", IpsumLoremGenerator.generate(2 + (int)(Math.random() * 4)).replace(".", ""));
        model.addAttribute("commentSuggestion", IpsumLoremGenerator.generate(7 + (int)(Math.random() * 15)));
        model.addAttribute("scoreSuggestion", 1 + new Random().nextInt(5));
    }

    private void addUniqueExternalIdsToModel(Model model) throws Exception{
        List<String> uniqueExternalIds = reviewRepo.findUniqueExternalIds();
        model.addAttribute("uniqueExternalIds", uniqueExternalIds);
    }

    private void addCursorToModel(Model model, int elementCount, PageCursor originalCursor){
        if(elementCount > 0) {
            var prevCursor = originalCursor.previous();
            var nextCursor = originalCursor.next();

            if(nextCursor.getOffset() >= elementCount)
                nextCursor.setOffset(elementCount - 1);

            model.addAttribute("pagePrevCursor", PageCursorEncoder.encodeCursor(prevCursor));
            model.addAttribute("pageNextCursor", PageCursorEncoder.encodeCursor(nextCursor));
        }else{
            model.addAttribute("pageNextCursor", null);
            model.addAttribute("pagePrevCursor", null);
        }
    }

    private void addTotalReviewsForExternalIdToModel(Model model, String externalId) throws Exception{
        long totalReviewCount = reviewRepo.countByExternalIdAndStatus(externalId, Review.REVIEW_STATUS_APPROVED);
        model.addAttribute("totalReviewCount", totalReviewCount);
    }

    // used to extract the externalId from the request URI for the /reviews/{externalId} route. Must be used
    // allow for complex routing
    private String extractExternalIdFromRequest(HttpServletRequest req) {
        String path = (String) req.getAttribute(
            org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        String bestMatch = (String) req.getAttribute(
            org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        String externalId = new org.springframework.util.AntPathMatcher()
            .extractPathWithinPattern(bestMatch, path);

        return externalId;
    }

    public PageCursor decodeOrCreateCursor(String cursorStr, int defaultLimit) {
        if(cursorStr != null && !cursorStr.isBlank()) {
            return PageCursorEncoder.decodeCursor(cursorStr);
        }else{
            return new PageCursor(0, defaultLimit);
        }
    }

    @GetMapping("/error")
    public String error()  {
        return "error";
    }

    @GetMapping("/")
    public String index(Model model, HttpServletRequest req) throws Exception{
        return showReviews("/product/1", null, model, req);
    }

    @GetMapping("/show-reviews")
    public String showReviews(
        @RequestParam String externalId,
        @RequestParam(name = "cursor", defaultValue = "") String strEncodedCursor,
        Model model,
        HttpServletRequest req
    ) throws Exception{
        // find all unique externalIds for reviews to display in the dropdown for quick navigation
        // only used in demonstration interface
        // TODO: remove for production code
        addUniqueExternalIdsToModel(model);

        // for quick insertion of reviews, we can generate random suggestions for display name, title and comment
        // TODO: remove for production code
        addDefaultNewReviewFormValues(model);

        // add things like title etc
        ControllerHelper.setupModel(model);

        if(externalId == null || externalId.isBlank()) {
            Logger.log("missing externalId, defaulting to empty string");
            externalId = "";
        }

        // decode cursor
        PageCursor decodedCursor = decodeOrCreateCursor(strEncodedCursor, AppConfig.DEFAULT_MAX_VISIBLE_REVIEWS);

        ReviewQueryOptions options = new ReviewQueryOptions();
        options.setPageCursor(decodedCursor);
        options.setStatusEnum(Review.REVIEW_STATUS_APPROVED);
        options.setOrderByEnum(ReviewQueryOptions.OPTION_ORDER_BY_ID_ASC);

        // get score stats for the given externalId and add to model
        // this includes things like average score, total reviews, and score distribution for display in JSP
        var scoreStats = reviewService.getScoreStatsHelper(externalId);
        model.addAttribute("scoreStats", scoreStats);

        Logger.log("QueryOptions: " + options);

        // add reviews to model for display in JSP
        List<Review> reviews = reviewRepo.findByExternalIdWithPagination(externalId, options);
        model.addAttribute("reviews", reviews);

        // replace with
        int numPotentialReviewMatches = reviewRepo.countByExternalIdAndStatus(externalId, Review.REVIEW_STATUS_APPROVED);


        // add cursor to model
        addCursorToModel(model, numPotentialReviewMatches, decodedCursor);
        model.addAttribute("cursor", decodedCursor);

        // add total reviews for the given externalId to model for display in JSP
        // TODO: we could get this from our ReviewAggregateScoreHelper to avoid multiple queries to the database,
        //  but for simplicity we will just query it directly for now
        addTotalReviewsForExternalIdToModel(model, externalId);

        // add externalId to model for display in JSP and for use in form submission for new reviews
        model.addAttribute("externalId", externalId);

        //String externalId = extractExternalIdFromRequest(req);
        //externalId = URLDecoder.decode(externalId, StandardCharsets.UTF_8);
        //Logger.log("External ID extracted from request: " + externalId);



        // add a simple function to format double values to 2 decimals for display in JSP
        Function<Double, String> dblFormatter = v -> String.format(Locale.US, "%.2f", v);
        model.addAttribute("dblFormatter2", dblFormatter);

        // add a simple function to format double values to 2 decimals for display in JSP
        Function<Instant, String> dateFormatter = v -> {
            if (v == null) return "";

            return DateTimeFormatter.ofPattern("dd/MM/yyyy")
                .withLocale(Locale.US)
                .withZone(ZoneId.systemDefault())
                .format(v);
        };
        model.addAttribute("dateFormatter", dateFormatter);

        Function<Instant, String> daysAgo = v -> {
            if (v == null) return "";

            long days = ChronoUnit.DAYS.between(v, Instant.now());
            return String.valueOf(Math.max(0, days));
        };
        model.addAttribute("daysAgoFormatter", daysAgo);

        // TODO END: get scores for aggregate display of score distribution and average score

        // TODO: set to 1 for testing only
        model.addAttribute("tenantId", 1);

        return "index";
    }



    @PostMapping("/api/set-review-status/{customer}/{reviewId}/{newStatus}")
    public ResponseEntity<Void> setReviewStatus(@PathVariable String customer, @PathVariable long reviewId, @PathVariable int newStatus) throws Exception{
        try {
            reviewRepo.updateReviewStatus(reviewId, newStatus);
            return ResponseEntity.noContent().build();
        }catch(Exception e){
            // TODO: remove stacktrace?
            if(DEBUG_ERRORS) e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/like-review/{customer}/{reviewId}")
    public ResponseEntity<Void> addReviewLike(@PathVariable String customer, @PathVariable long reviewId, HttpServletRequest request) throws Exception{
        try {
            reviewRepo.updateReviewLikeCount(reviewId, 1);
            return ResponseEntity.noContent().build();
        }catch(Exception e){
            // TODO: remove stacktrace?
            if(DEBUG_ERRORS) e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/dislike-review/{customer}/{reviewId}")
    public ResponseEntity<Object> addReviewDislike(@PathVariable String customer, @PathVariable long reviewId, HttpServletRequest request) throws Exception{
        try {
            reviewRepo.updateReviewDislikeCount(reviewId, 1);
            return ResponseEntity.ok().build();
            //return "redirect:" + request.getHeader("Referer");
        }catch(Exception e){
            // TODO: remove stacktrace?
            if(DEBUG_ERRORS) e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
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

    @DeleteMapping("/api/delete-review/{tenantId}/{reviewId}")
    public ResponseEntity deleteReviewApi(
        @PathVariable long tenantId,
        @PathVariable long reviewId,
        HttpServletRequest req
    ) throws Exception {
        try {
            // TODO: check that tenant is set

            // validate parameters
            if(tenantId <= 0 || reviewId <= 0) throw new BadRequestException();

            reviewRepo.deleteById(reviewId);

            return ResponseEntity.noContent().build();
        } catch(BadRequestException e){
            return ResponseEntity.badRequest().build();
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    public boolean validateDeleteReviewApi(long tenantId, long reviewId) {
        return !(tenantId <= 0 || reviewId <= 0);
    }




    /**
     * API endpoint to submit a new review. For simplicity, we are using request parameters for all input,
     * but in a real application you would likely want to use a request body with a DTO object for better
     * structure and validation.
     * @param form
     * @param model
     * @param ra
     * @param req
     * @return
     * @throws Exception
     */

    @PostMapping("/submit-review")
    public ResponseEntity<Void> submitReview(
        /*
        @RequestParam(defaultValue = "1") long tenantId,
        @RequestParam(defaultValue = "") String externalId,

        @RequestParam(defaultValue = "") String email,
        @RequestParam(defaultValue = "") String password,

        @RequestParam(defaultValue = "Anonymous") String displayName,
        @RequestParam(defaultValue = "1") int score,
        @RequestParam(defaultValue = "") String title,
        @RequestParam(defaultValue = "") String comment,
         */
        @ModelAttribute NewReviewForm form,
        Model model,
        RedirectAttributes ra,
        HttpServletRequest req
    ) throws Exception {
        try {
            System.out.println("ROUTE /api/submit-review");
            dumpRequestParams(req);

            List<String> errors = validateNewReviewForm(form);
            if(!errors.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Reviewer reviewer = (Reviewer) reviewerRepo.findByEmail(form.email()).orElse(null);
            if(Objects.isNull(reviewer)) {
                return ResponseEntity.badRequest().build();
            }

            Review review = new Review();
            review.setAuthorName(form.displayName());
            review.setScore(form.score());
            review.setComment(form.comment());
            review.setExternalId(form.externalId());
            review.setCreatedAt(Instant.now());
            review.setTitle(form.title());
            review.setStatus(Review.REVIEW_STATUS_PENDING);

            if(AUTO_APPROVE_NEW_REVIEWS /* NOTE: should be set to false for production */) {
                review.setStatus(Review.REVIEW_STATUS_APPROVED);
            }

            reviewRepo.save(review);

            return ResponseEntity.ok().build();
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private static List<String> validateNewReviewForm(NewReviewForm form) throws Exception{
        List<String> errors = new ArrayList<>();

        // Validate input parameters (you can add more validation as needed)
        if (errors.isEmpty() && (form.tenantId() <= 0 || form.score() < 1 || form.score() > 5)) {
            errors.add("Invalid input parameters.");
        }

        if(errors.isEmpty() && (form.email().isEmpty() || form.password().isEmpty())) {
            errors.add("Email and password are required.");
        }

        if(errors.isEmpty() && !(form.email().equals("test@test.com") && form.password().equals("Abacus556!"))) {
            errors.add("Test credentials (email: \"test@test.com\", pass: \"pass\" are required to submit a review.");
        }

        return errors;
    }

    /*

    @PostMapping("/submit-review")
    public String submitReview(
        @RequestParam(defaultValue = "1") long tenantId,
        @RequestParam(defaultValue = "") String externalId,

        @RequestParam(defaultValue = "") String email,
        @RequestParam(defaultValue = "") String password,

        @RequestParam(defaultValue = "Anonymous") String displayName,
        @RequestParam(defaultValue = "1") int score,
        @RequestParam(defaultValue = "") String title,
        @RequestParam(defaultValue = "") String comment,
        Model model,
        RedirectAttributes ra,
        HttpServletRequest req
    ) throws Exception {
        try {
            System.out.println("ROUTE /api/submit-review");
            dumpRequestParams(req);

            List<String> errors = validateSubmitReviewParams(tenantId, score, email, password);
            if(!errors.isEmpty()) {
                return ControllerHelper.create()
                    .withError("Invalid input parameters.")
                    .redirect(ra, "/");
            }

            Reviewer reviewer = (Reviewer) reviewerRepo.findByEmail(email).orElse(null);
            if(Objects.isNull(reviewer)) {
                return ControllerHelper.create()
                    .withError("Reviewer already exists.")
                    .redirect(ra, "/");
            }

            Review review = new Review();
            review.setAuthorName(displayName);
            review.setScore(score);
            review.setComment(comment);
            review.setExternalId(externalId);
            review.setCreatedAt(Instant.now());
            review.setTitle(title);
            reviewRepo.save(review);

            return ControllerHelper.create()
                .withSuccess("Review has been added successfully. [REPLACE MSG WITH STATIC CONSTANT]")
                .redirect(ra, "/");
        }catch(Exception e){
            e.printStackTrace();
            return ControllerHelper.create()
                .withError("Exception thrown: " + e.getMessage())
                .redirect(ra, "/");
        }
    }
     */

    private static void dumpRequestParams(HttpServletRequest req) {
        System.out.println("BEGIN request-params:");
        req.getParameterMap().forEach((k, v) ->
            System.out.println("\t" + k + " = " + java.util.Arrays.toString(v))
        );
        System.out.println("END request-params");
    }

    @DeleteMapping("/api/submit-review")
    public ResponseEntity apiSubmitReview(
        /*
        @RequestParam(defaultValue = "1") long tenantId,
        @RequestParam(defaultValue = "") String externalId,

        @RequestParam(defaultValue = "") String email,
        @RequestParam(defaultValue = "") String password,

        @RequestParam(defaultValue = "Anonymous") String displayName,
        @RequestParam(defaultValue = "1") int score,
        @RequestParam(defaultValue = "") String title,
        @RequestParam(defaultValue = "") String comment,
         */
        @ModelAttribute NewReviewForm form,
        Model model,
        RedirectAttributes ra,
        HttpServletRequest req
    ) throws Exception {
        try {
            System.out.println("ROUTE /api/submit-review");
            dumpRequestParams(req);

            List<String> errors = validateNewReviewForm(form);
            if(!errors.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Reviewer reviewer = (Reviewer) reviewerRepo.findByEmail(form.email()).orElse(null);
            if(Objects.isNull(reviewer)) {
                return ResponseEntity.badRequest().build();
            }

            Review review = new Review();
            review.setAuthorName(form.displayName());
            review.setScore(form.score());
            review.setComment(form.comment());
            review.setExternalId(form.externalId());
            review.setCreatedAt(Instant.now());
            review.setTitle(form.title());
            review.setStatus(Review.REVIEW_STATUS_PENDING);

            if(AUTO_APPROVE_NEW_REVIEWS /* NOTE: should be set to false for production */) {
                review.setStatus(Review.REVIEW_STATUS_APPROVED);
            }

            reviewRepo.save(review);

            return ResponseEntity.ok().build();
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }



    public void actualInsertReviewCode(){
        /*
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
        */
    }
}
