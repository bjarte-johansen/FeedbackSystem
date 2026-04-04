package root.controllers.dto;

import org.springframework.lang.NonNull;

public record NewReviewForm(
    long tenantId,
    String externalId,

    String email,
    String password,

    String displayName,
    int score,
    String title,
    String comment
) {}
