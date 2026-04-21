package root.controllers.helpers;

import root.models.Review;

import java.util.LinkedHashMap;

public class ControllerConstantMaps{
    public static LinkedHashMap<String, Object> ALL_CONSTANTS = root.includes.Utils.linkedMap(
        "reviewStatus", root.includes.Utils.linkedMap(
            "friendlyNameToConst", root.includes.Utils.linkedMap(
                "pending", Review.REVIEW_STATUS_PENDING,
                "approved", Review.REVIEW_STATUS_APPROVED,
                "rejected", Review.REVIEW_STATUS_REJECTED
            ),
            "constToFriendlyName", root.includes.Utils.linkedMap(
                Review.REVIEW_STATUS_PENDING, "pending",
                Review.REVIEW_STATUS_APPROVED, "approved",
                Review.REVIEW_STATUS_REJECTED, "rejected"
            ),
            "constNorwegianName", root.includes.Utils.linkedMap(
                Review.REVIEW_STATUS_PENDING, "Til vurdering",
                Review.REVIEW_STATUS_APPROVED, "Godkjent",
                Review.REVIEW_STATUS_REJECTED, "Avvist"
            )
        )
    );
}