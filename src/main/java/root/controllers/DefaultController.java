package root.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import root.beans.FormatUtils;
import root.common.utils.FunnyUserNameGenerator;
import root.common.utils.IpsumLoremGenerator;
import root.logger.Logger;
import root.models.Review;
import root.models.Reviewer;
import root.repositories.ReviewerRepository;
import root.repositories.ReviewRepository;

import java.text.NumberFormat;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
//import root.models.repositories.JdbcReviewRepository;


@Controller
public class DefaultController {
    //private static final Logger log = new Logger();

    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    ReviewerRepository reviewerRepo;

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

    @GetMapping("/error")
    public String error()  {

        return "error";

    }

    @GetMapping("/")
    public String index(Model model) throws Exception{
        // find all unique externalIds for reviews to display in the dropdown for quick navigation
        // TODO: remove for production code
        addUniqueExternalIdsToModel(model);

        // for quick insertion of reviews, we can generate random suggestions for display name, title and comment
        // TODO: remove for production code
        addDefaultNewReviewFormValues(model);

        // add things like title etc
        ControllerHelper.setupModel(model);

        List<Review> reviews = reviewRepo.findAll();
        model.addAttribute("reviews", reviews);

        // TODO: set to 1 for testing only
        model.addAttribute("tenantId", 1);

        return "index";
    }

    private String extractExternalIdFromRequest(HttpServletRequest req) {
        String path = (String) req.getAttribute(
            org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        String bestMatch = (String) req.getAttribute(
            org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        String externalId = new org.springframework.util.AntPathMatcher()
            .extractPathWithinPattern(bestMatch, path);

        return externalId;
    }


    @GetMapping("/show-reviews")
    public String showReviews(@RequestParam String externalId, Model model, HttpServletRequest req) throws Exception{
        // find all unique externalIds for reviews to display in the dropdown for quick navigation
        // TODO: remove for production code
        addUniqueExternalIdsToModel(model);

        // for quick insertion of reviews, we can generate random suggestions for display name, title and comment
        // TODO: remove for production code
        addDefaultNewReviewFormValues(model);

        // add things like title etc
        ControllerHelper.setupModel(model);

        model.addAttribute("reviewFormExternalId__DEBUG__", externalId);

        //String externalId = extractExternalIdFromRequest(req);
        //externalId = URLDecoder.decode(externalId, StandardCharsets.UTF_8);
        //Logger.log("External ID extracted from request: " + externalId);

        // find relevant reviews
        List<Review> reviews = reviewRepo.findByExternalId(externalId);
        model.addAttribute("reviews", reviews);
        model.addAttribute("externalId", externalId);

        // TODO BEGIN: get scores for aggregate display of score distribution and average score
        var jspScoreObject = new LinkedHashMap<String, String>();



        var scoreStats = new ScoreStatsHelper();

        var scoreMap = reviewRepo.findReviewScoreStatsByExternalId(externalId);
        Logger.log("Review score stats for /product/1: " + scoreMap);

        // int totalScoreCount = scoreMap.values().stream().mapToInt(Integer::intValue).sum();
        float averageScore = 0.0f;
        int totalScoreCount = 0;
        for(int i=1; i<=5; i++) {
            int count = scoreMap.getOrDefault(i, 0);
            averageScore += count * i;
            totalScoreCount += count;
        }
        averageScore = averageScore / reviews.size();
        scoreStats.setAverageScore(averageScore);
        scoreStats.setTotalScoreCount(totalScoreCount);

        for(int i=5; i>=1; i--) {
            int count = scoreMap.getOrDefault(i, 0);
            Double pct = totalScoreCount > 0 ? ((double) count / totalScoreCount) * 100.0 : 0.0;
            scoreStats.getScoreDistribution().put(i, pct);
            scoreStats.getScoreCounts().put(i, count);
            //Logger.log("5 Stjerner: " + scoreMap.getOrDefault(i, 0) + ", andel: " + pct + "%");
        }

        model.addAttribute("scoreStats", scoreStats);

        Function<Double, String> dblFormatter = v -> String.format(Locale.US, "%.2f", v);

        model.addAttribute("dblFormatter2", dblFormatter);

        // TODO END: get scores for aggregate display of score distribution and average score

        // TODO: set to 1 for testing only
        model.addAttribute("tenantId", 1);

        return "index";
    }


    @DeleteMapping("/api/delete-review/{tenantId}/{reviewId}")
    public ResponseEntity deleteReviewApi(
        @PathVariable long tenantId,
        @PathVariable long reviewId,
        HttpServletRequest req
    ) throws Exception {
        try {
            // validate input parameters (you can add more validation as needed)
            if (tenantId <= 0 || reviewId <= 0) {
                return ResponseEntity.badRequest().build();
            }

            // delete review
            reviewRepo.deleteById(reviewId);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
/*
    @PostMapping("/api/delete-review/{tenantId}/{reviewId}")
    public ResponseEntity deleteReviewApi2(
        @PathVariable long tenantId,
        @PathVariable long reviewId,
        HttpServletRequest req
    ) throws Exception {
        req.getParameterMap().forEach((k, v) ->
            System.out.println(k + " = " + java.util.Arrays.toString(v))
        );

        try {
            // validate input parameters (you can add more validation as needed)
            if (tenantId <= 0 || reviewId <= 0) {
                //return Map.of("status", false, "message", "Invalid input parameters.");
                return ResponseEntity.badRequest().build();
            }

            // delete review
            reviewRepository.deleteById(tenantId, reviewId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
    */

    private static List<String> validateSubmitReviewParams(long tenantId, int score, String email, String password) throws Exception{
        List<String> errors = new ArrayList<>();

        // Validate input parameters (you can add more validation as needed)
        if (errors.isEmpty() && (tenantId <= 0 || score < 1 || score > 5)) {
            errors.add("Invalid input parameters.");
        }

        if(errors.isEmpty() && (email.isEmpty() || password.isEmpty())) {
            errors.add("Email and password are required.");
        }

        if(errors.isEmpty() && !(email.equals("test@test.com") && password.equals("pass"))) {
            errors.add("Test credentials (email: \"test@test.com\", pass: \"pass\" are required to submit a review.");
        }

        return errors;
    }

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

    private static void dumpRequestParams(HttpServletRequest req) {
        System.out.println("BEGIN request-params:");
        req.getParameterMap().forEach((k, v) ->
            System.out.println("\t" + k + " = " + java.util.Arrays.toString(v))
        );
        System.out.println("END request-params");
    }

    @DeleteMapping("/api/submit-review")
    public ResponseEntity apiSubmitReview(
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
                return ResponseEntity.badRequest().build();
            }

            Reviewer reviewer = (Reviewer) reviewerRepo.findByEmail(email).orElse(null);
            if(Objects.isNull(reviewer)) {
                return ResponseEntity.badRequest().build();
            }

            Review review = new Review();
            review.setAuthorName(displayName);
            review.setScore(score);
            review.setComment(comment);
            review.setExternalId(externalId);
            review.setCreatedAt(Instant.now());
            review.setTitle(title);
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
