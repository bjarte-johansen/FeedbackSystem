package root.controllers.dto;

import root.includes.Utils;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * DTO for creating a new review. This record encapsulates all necessary information required to submit a review,
 * including tenant information, reviewer credentials, and the review content itself.
 */

public record NewReviewFormDto(
    String externalId,
    String email,
    String displayName,
    Integer score,
    String title,
    String comment
) {
    public NewReviewFormDto {
        email = org.jsoup.Jsoup.parse(email).text();
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

    public static List<String> validate(NewReviewFormDto dto, List<String> errors){
        checkArgument(dto != null, "DTO cannot be null.");
        checkArgument(errors != null, "Error list cannot be null.");

        try {
            // Validate input parameters (you can add more validation as needed)
            checkArgument(dto.externalId != null, "External Id cannot be null");

            //Utils.requireValidEmail(dto.email);
            checkArgument(Utils.hasText(dto.email), "Email cannot be null or blank.");
            checkArgument(Utils.isValidEmail(dto.email), "Email format is invalid.");

            checkArgument(Utils.isBetweenInclusive(dto.score, 1, 5), "Score must be between 1 and 5.");

            checkArgument(Utils.hasText(dto.displayName), "Display name cannot be null or blank.");

            checkArgument(Utils.hasText(dto.comment), "Comment cannot be null or blank.");
        }catch(Exception e){
            errors.add(e.getMessage() != null ? e.getMessage() : "An unknown error occurred during validation.");
        }

        return errors;
    }
}