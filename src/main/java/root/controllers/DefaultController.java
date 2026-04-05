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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
//import root.models.repositories.JdbcReviewRepository;



class AppClientSessionObject{
    Map<String, Boolean> reviewLikeMap = new LinkedHashMap<>();

    public AppClientSessionObject(HttpServletRequest req) {
        var session = req.getSession();
        if(session.getAttribute(AppConfig.SESSION_ROOT_KEY) == null){
            session.setAttribute(AppConfig.SESSION_ROOT_KEY, this);
        }

    }
    public static AppClientSessionObject getOrCreateClientSessionObject(HttpServletRequest req) {
        var session = req.getSession();
        AppClientSessionObject cso = (AppClientSessionObject) session.getAttribute(AppConfig.SESSION_ROOT_KEY);
        if(cso == null){
            cso = new AppClientSessionObject(req);
            session.setAttribute(AppConfig.SESSION_ROOT_KEY, cso);
        }
        return cso;
    }

    public Map<String, Boolean> getReviewLikeMap() {
        return reviewLikeMap;
    }

    public boolean hasLikedReview(long reviewId) {
        return reviewLikeMap.getOrDefault("like_" + reviewId, false);
    }
    public void markReviewLiked(long reviewId) {
        reviewLikeMap.put("like_" + reviewId, true);
    }

    public boolean hasDislikedReview(long reviewId) {
        return reviewLikeMap.getOrDefault("dislike_" + reviewId, false);
    }
    public void markReviewDisliked(long reviewId) {
        reviewLikeMap.put("dislike_" + reviewId, true);
    }
}

@Controller
public class DefaultController {
    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    ReviewerRepository reviewerRepo;

    @Autowired
    ReviewService reviewService;

    // debugging flag
    public static boolean DEBUG_ERRORS = true;
    public static boolean PRINT_REQUEST_PARAMS = false;
    public static boolean PRINT_STACK_TRACE_ON_ERROR = true;

    private static double roundToHalf(double v) { return Math.round(v * 2.0) / 2.0; }

    // add a simple function to format double values to 2 decimals for display in JSP
    private static Function<Double, String> CSS_DOUBLE_FORMATTER_POINT_FIVE = (v) -> String.format(Locale.US, "%.1f", roundToHalf(v)).replace(".", "-");
    private static Function<Double, String> DOUBLE_FORMATTER_1 = (v) -> String.format(Locale.US, "%.1f", v);
    private static Function<Double, String> DOUBLE_FORMATTER_2 = (v) -> String.format(Locale.US, "%.1f", v);

    // add a simple function to format double values to 2 decimals for display in JSP
    private static Function<Instant, String> DD_MM_YYYY_FORMATTER = v -> {
        if (v == null) return "";

        return DateTimeFormatter.ofPattern("dd/MM/yyyy")
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault())
            .format(v);
    };

    // add a simple function to format Instant values to "days ago" format for display in JSP
    private static Function<Instant, String> DAYS_AGO_FORMATTER = v -> {
        if (v == null) return "";

        long days = ChronoUnit.DAYS.between(v, Instant.now());
        return String.valueOf(Math.max(0, days));
    };





    /*
     * methods
     */

    private void addDefaultNewReviewFormValues(Model model){
        model.addAttribute("displayNameSuggestion", FunnyUserNameGenerator.generate());
        model.addAttribute("titleSuggestion", IpsumLoremGenerator.generate(2 + (int)(Math.random() * 4)).replace(".", ""));
        model.addAttribute("commentSuggestion", IpsumLoremGenerator.generate(7 + (int)(Math.random() * 15)));
        model.addAttribute("scoreSuggestion", 1 + new Random().nextInt(5));
    }

    private void addSelectExternalIdPillData(Model model) throws Exception{
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

    /*
    private void addTotalReviewsForExternalIdToModel(Model model, String externalId) throws Exception{
        long totalReviewCount = reviewRepo.countByExternalIdAndStatus(externalId, Review.REVIEW_STATUS_APPROVED);
        model.addAttribute("totalReviewCount", totalReviewCount);
    }
     */

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



    /**
     * Simple route to display an error page. This is just for demonstration purposes and should be replaced with
     * proper error handling in production code.
     */

    @GetMapping("/error")
    public String error()  {
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
    public String index(Model model, HttpServletRequest req) throws Exception{
        return showReviews("/product/1", null, model, req);
    }



    /**
     * Main route to show reviews for a given externalId. This is the main route of the application and is used to
     * display reviews for a given externalId with pagination and sorting. It also displays aggregate score stats
     * for the given externalId.
     * @param externalId
     * @param strEncodedCursor
     * @param model
     * @param req
     * @return
     * @throws Exception
     */

    @GetMapping("/show-reviews")
    public String showReviews(
        @RequestParam String externalId,
        @RequestParam(name = "cursor", defaultValue = "") String strEncodedCursor,
        Model model,
        HttpServletRequest req
    ) throws Exception{
        // add things like title etc
        ControllerHelper.setupModel(model);

        // find all unique externalIds for reviews to display in the dropdown for quick navigation
        // only used in demonstration interface
        // TODO: remove for production code, should allways take an externalId as a parameter and not display
        // a dropdown of all externalIds
        addSelectExternalIdPillData(model);

        // for quick insertion of reviews, we can generate random suggestions for display name, title and comment
        // TODO: remove for production code, should have empty form
        addDefaultNewReviewFormValues(model);

        //String externalId = extractExternalIdFromRequest(req);
        //externalId = URLDecoder.decode(externalId, StandardCharsets.UTF_8);
        //Logger.log("External ID extracted from request: " + externalId);


        // TODO: set to 1 for testing only
        model.addAttribute("tenantId", 1);

        if(externalId == null || externalId.isBlank()) {
            Logger.log("missing externalId, defaulting to empty string");
            throw new RuntimeException("externalId is null or empty");
        }

        // decode cursor
        PageCursor decodedCursor = decodeOrCreateCursor(strEncodedCursor, AppConfig.DEFAULT_MAX_VISIBLE_REVIEWS);

        // set query options for fetching reviews, including pagination and sorting. We are only fetching approved reviews for display.
        ReviewQueryOptions options = new ReviewQueryOptions();
        options.setPageCursor(decodedCursor);
        options.setStatusEnum(Review.REVIEW_STATUS_APPROVED);
        options.setOrderByEnum(ReviewQueryOptions.OPTION_ORDER_BY_ID_DESC);

        // get score stats for the given externalId and add to model
        // this includes things like average score, total reviews, and score distribution for display in JSP
        var reviewStats = reviewService.getScoreStatsHelper(externalId, Review.REVIEW_STATUS_APPROVED);
        model.addAttribute("reviewStats", reviewStats);

        Logger.log("QueryOptions: " + options);

        // add reviews to model for display in JSP
        List<Review> reviews = reviewRepo.findByExternalIdWithPagination(externalId, options);
        model.addAttribute("reviews", reviews);

        ReviewQueryOptions dumpOptions = new ReviewQueryOptions();
        dumpOptions.setPageCursor(new PageCursor(0, Integer.MAX_VALUE));
        dumpOptions.setStatusEnum(Review.REVIEW_STATUS_MATCH_ALL);
        dumpOptions.setOrderByEnum(ReviewQueryOptions.OPTION_ORDER_BY_ID_DESC);
        List<Review> reviewDump = reviewRepo.findByExternalIdWithPagination(externalId, dumpOptions);
        model.addAttribute("reviewDump", reviewDump);

        // add cursor to model
        addCursorToModel(model, (int) reviewStats.getTotalCount(), options.getPageCursor());
        //model.addAttribute("cursor", options.getPageCursor());

        // add externalId to model for display in JSP and for use in form submission for new reviews
        model.addAttribute("externalId", externalId);

        // add formatters to model for display in JSP
        model.addAttribute("dblFormatter1", DOUBLE_FORMATTER_1);
        //model.addAttribute("dblFormatter2", DOUBLE_FORMATTER_2);
        model.addAttribute("dateFormatter", DD_MM_YYYY_FORMATTER);
        model.addAttribute("daysAgoFormatter", DAYS_AGO_FORMATTER);
        model.addAttribute("dblFormatterCssPointFive", CSS_DOUBLE_FORMATTER_POINT_FIVE);

        // TODO END: get scores for aggregate display of score distribution and average score

        return "index";
    }



    /**
     * API endpoint to add a like to a review.
     * Can be called only once for a user session.
     *
     * @param customer
     * @param reviewId
     * @return
     * @throws Exception
     */

    @PostMapping("/api/like-review/{customer}/{reviewId}")
    public ResponseEntity<Void> addReviewLike(
        @PathVariable String customer,
        @PathVariable long reviewId,
        HttpSession session)
    throws Exception{
        try {
            if (addReviewVote(session, reviewId, 1)) {
                return ResponseEntity.ok().build();
            }
        }catch (Exception e){
            if(AppConfig.CONTROLLER_PRINT_REQUEST_PARAMS) e.printStackTrace();
        }

        return ResponseEntity.noContent().build();
    }



    /**
     * API endpoint to add a dislike to a review.
     * Can be called only once for a user session.
     *
     * @param customer
     * @param reviewId
     * @return
     * @throws Exception
     */

    @PostMapping("/api/dislike-review/{customer}/{reviewId}")
    public ResponseEntity<Void> addReviewDislike(
        @PathVariable String customer,
        @PathVariable long reviewId,
        HttpSession session)
    throws Exception{
        try {
            if (addReviewVote(session, reviewId, -1)) {
                return ResponseEntity.ok().build();
            }
        }catch (Exception e){
            if(AppConfig.CONTROLLER_PRINT_REQUEST_PARAMS) e.printStackTrace();
        }

        return ResponseEntity.noContent().build();
    }


    private boolean addReviewVote(HttpSession session, long reviewId, int offset) throws Exception {
        Supplier<Map<String, Boolean>> getSessionLikeMap = () -> {
            Map<String, Boolean> sessionLikeMap = (Map<String, Boolean>) session.getAttribute(AppConfig.SESSION_REVIEW_LIKE_MAP_KEY);
            if(sessionLikeMap == null) {
                sessionLikeMap = new HashMap<>();
                session.setAttribute(AppConfig.SESSION_REVIEW_LIKE_MAP_KEY, sessionLikeMap);
            }
            return sessionLikeMap;
        };

        try {
            String key = offset > 0 ? "like_" + reviewId : "dislike_" + reviewId;
            Map<String, Boolean> sessionLikeMap = getSessionLikeMap.get();

            if(sessionLikeMap.get(key) == null) {
                int voteType = (offset > 0) ? Review.VOTE_UP : Review.VOTE_DOWN;
                reviewService.addReviewVote(reviewId, voteType, 1);

                sessionLikeMap.put(key, true);
                return true;
            }

            return false;
        }catch(Exception e){
            if(AppConfig.CONTROLLER_PRINT_REQUEST_PARAMS) e.printStackTrace();
            return false;
        }
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
        @ModelAttribute NewReviewForm form,
        Model model,
        RedirectAttributes ra,
        HttpServletRequest req
    ) throws Exception {
        if(AppConfig.CONTROLLER_PRINT_REQUEST_PARAMS){
            ControllerUtils.dumpRequestParams(req);
        }

        try {
            List<String> errors = NewReviewForm.validate(form, new ArrayList<String>());
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

            if(AppConfig.AUTO_APPROVE_NEW_REVIEWS /* NOTE: should be set to false for production */) {
                review.setStatus(Review.REVIEW_STATUS_APPROVED);
            }

            reviewRepo.save(review);

            return ResponseEntity.ok().build();
        }catch(Exception e){
            if(AppConfig.CONTROLLER_PRINT_REQUEST_PARAMS) e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

/*
    @PostMapping("/api/submit-review")
    public ResponseEntity apiSubmitReview(
        @ModelAttribute NewReviewForm form,
        Model model,
        RedirectAttributes ra,
        HttpServletRequest req
    ) throws Exception {
        try {
            System.out.println("ROUTE /api/submit-review");
            ControllerUtils.dumpRequestParams(req);

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

            if(AppConfig.AUTO_APPROVE_NEW_REVIEWS) {
                review.setStatus(Review.REVIEW_STATUS_APPROVED);
            }

            reviewRepo.save(review);

            return ResponseEntity.ok().build();
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    */


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
