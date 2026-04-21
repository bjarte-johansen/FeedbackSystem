package root.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import root.app.AppConfig;
import root.common.testdata.FunnyUserNameGenerator;
import root.common.testdata.IpsumLoremGenerator;
import root.controllers.dto.NewReviewForm;
import root.models.Review;
import root.models.ReviewSettings;
import root.repositories.ReviewRepository;
import root.services.ReviewSettingsService;

import java.time.Instant;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;


@Controller
public class NewReviewController {
    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    ReviewSettingsService reviewSettingsService;


    /**
     * get suggested form values for pre-filling, externalId is in result.externalId, rest is in result.formValues
     */

    private Map<String, Object> encodePrefilledValues() {
        Map<String, Object> prefilled = new HashMap<>();

        prefilled.put("displayNameSuggestion", FunnyUserNameGenerator.generate());
        prefilled.put("titleSuggestion", IpsumLoremGenerator.generate(2 + (int) (Math.random() * 4)).replace(".", ""));
        prefilled.put("commentSuggestion", IpsumLoremGenerator.generate(7 + (int) (Math.random() * 15)));
        prefilled.put("scoreSuggestion", 1 + new Random().nextInt(5));

        return prefilled;
    }


    /**
     * Gives back a partially prefilled json structure to initialize form from for testing during development to avoid
     * laborious work.
     *
     * @param externalId
     * @return
     */

    @ResponseBody
    @GetMapping("/api/default-dev-form-values")
    public Map<String, Object> defaultDevFormValues(
        @RequestParam(defaultValue = "") String externalId
    ) {
        Map<String, Object> vm = new HashMap<>();

        vm.put("externalId", externalId);
        vm.put("submitUrl", "/api/review/submit");

        vm.put("form", encodePrefilledValues());

        return vm;
    }


    /**
     * Shows the form for submitting a new review. This is just a placeholder for the actual form page, which should be
     * implemented in the frontend. The actual implementation of the form page is handled by the frontend, we only need
     * to show the view and provide the necessary data for it. The form is submitted to /api/submit-review, which is
     * handled by the submitReview endpoint in this controller.
     *
     * @param externalId
     * @param prefilled
     * @param model
     * @return
     * @throws Exception
     */

    @GetMapping("/api/new-review-form/create")
    public String create(
        @RequestParam(defaultValue = "") String externalId,
        @RequestParam(defaultValue = "0") int prefilled,
        Model model
    ) throws Exception {
        model.addAttribute("externalId", externalId);
        model.addAttribute("submitUrl", "/api/submit-review");

        // prefilled for dev work only
        if (prefilled > 0) {
            model.addAttribute("form", encodePrefilledValues());
        }

        return "new-review-form";
    }


    /**
     * API endpoint to submit a new review. For simplicity, we are using request parameters for all input, but in a real
     * application you would likely want to use a request body with a DTO object for better structure and validation.
     *
     * @param form
     * @return
     */

    @PostMapping({"/api/review/submit"})
    public ResponseEntity<Void> submitReview(
        @ModelAttribute NewReviewForm form
    ) {
        List<String> errors = NewReviewForm.validate(form, new ArrayList<String>());
        checkArgument(errors.isEmpty(), errors.toString());

        final ReviewSettings reviewCfg = reviewSettingsService.findOrCreateByExternalId(
            Objects.requireNonNullElse(form.externalId(), "")
            )
            .orElseThrow(() -> new RuntimeException("Unexpected empty review settings"));

        if(reviewCfg.getEnableSubmit()){
            // no reviewer, verification code in the future
            // Reviewer reviewer = reviewerRepo.findByEmail(form.email()).orElse(null);
            // checkArgument(reviewer != null, "Reviewer not found");

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
        }else{
            // TODO: serve message that form submission is temporarily disabled
        }

        return ResponseEntity.ok().build();
    }
}