package root.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import root.models.Review;

import java.util.Map;

@ControllerAdvice(assignableTypes = AdminController.class)
public class AdminModelAdvice {
    private static final Map<String, Object> CONSTS = Map.of(
        "REVIEW_STATUS_APPROVED", Review.REVIEW_STATUS_APPROVED,
        "REVIEW_STATUS_PENDING", Review.REVIEW_STATUS_PENDING,
        "REVIEW_STATUS_REJECTED", Review.REVIEW_STATUS_REJECTED
        );

    @ModelAttribute("constants")
    public Map<String, Object> constants() {
        return CONSTS;
    }
}