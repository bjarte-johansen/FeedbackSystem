package root.controllers.dto;

import root.app.AppConfig;
import root.includes.Utils;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * DTO for creating a new review. This record encapsulates all necessary information required to submit a review,
 * including tenant information, reviewer credentials, and the review content itself.
 */

public record NewReviewForm(
    String externalId,
    String email,
    String password,
    String displayName,
    int score,
    String title,
    String comment
) {
    public NewReviewForm {
        email = org.jsoup.Jsoup.parse(email).text();
        password = org.jsoup.Jsoup.parse(password).text();
        displayName = org.jsoup.Jsoup.parse(displayName).text();
        title = org.jsoup.Jsoup.parse(title).text();
        comment = org.jsoup.Jsoup.parse(comment).text();
    }

    /**
     * Validates the NewReviewForm DTO and populates the provided errors list with any validation errors found.
     * No need for spring-mvc validators here, as this is a simple DTO and we want to keep validation logic centralized
     * and straightforward.
     *
     * @param dto
     * @param errors
     * @return
     */

    public static List<String> validate(NewReviewForm dto, List<String> errors){
        checkArgument(dto != null, "DTO cannot be null.");
        checkArgument(errors != null, "Error list cannot be null.");
        //errors = errors != null ? errors : new ArrayList<String>();

        try {
            // Validate input parameters (you can add more validation as needed)
            //checkArgument(dto.tenantId > 0, "Tenant ID must be a positive number.");
            checkArgument(dto.score >= 1 && dto.score <= 5, "Score must be between 1 and 5.");

            if (AppConfig.ENABLE_CLIENT_EMAIL_AND_PASSWORD_REQUIRED) {
                Utils.requireValidEmail(dto.email());
                Utils.requireValidPassword(dto.password());

                checkArgument(
                    dto.email().equals("test@test.com") && dto.password().equals("Abacus556!"),
                    "Invalid test credentials (email: \"test@test.com\", pass: \"Abacus556!\" are required to submit a review.");
            }
        }catch(Exception e){
            errors.add(e.getMessage() != null ? e.getMessage() : "An unknown error occurred during validation.");
        }

        return errors;
    }


}
