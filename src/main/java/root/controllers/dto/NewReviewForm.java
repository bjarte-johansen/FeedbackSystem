package root.controllers.dto;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

import static root.common.utils.Preconditions.checkArgument;


/**
 * DTO for creating a new review. This record encapsulates all necessary information required to submit a review,
 * including tenant information, reviewer credentials, and the review content itself.
 */

public record NewReviewForm(
    long tenantId,
    String externalId,

    String email,
    String password,

    String displayName,
    int score,
    String title,
    String comment
) {
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

        // Validate input parameters (you can add more validation as needed)
        if (errors.isEmpty() && (dto.tenantId() <= 0 || dto.score() < 1 || dto.score() > 5)) {
            errors.add("Invalid input parameters.");
        }

        if(errors.isEmpty() && (dto.email().isEmpty() || dto.password().isEmpty())) {
            errors.add("Email and password are required.");
        }

        if(errors.isEmpty() && !(dto.email().equals("test@test.com") && dto.password().equals("Abacus556!"))) {
            errors.add("Test credentials (email: \"test@test.com\", pass: \"pass\" are required to submit a review.");
        }

        return errors;
    }
}
