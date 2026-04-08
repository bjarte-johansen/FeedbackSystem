package root.controllers;

import jakarta.servlet.http.HttpServletRequest;
//import org.apache.coyote.BadRequestException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import root.app.AppConfig;
import root.app.AppContext;
import root.models.Review;
import root.repositories.ReviewRepository;
import root.repositories.ReviewerRepository;
import root.services.ReviewService;

import org.springframework.security.access.AccessDeniedException;
//import java.nio.file.AccessDeniedException;
import java.util.Map;

@Controller
public class AdminController {

    // declare repos
    private final ReviewRepository reviewRepo;
    private final ReviewerRepository reviewerRepo;
    private final ReviewService reviewService;


    /**
     * Constructor with dependency injection.
     * @param reviewRepo
     * @param reviewerRepo
     * @param reviewService
     */

    public AdminController(ReviewRepository reviewRepo, ReviewerRepository reviewerRepo, ReviewService reviewService) {
        this.reviewRepo = reviewRepo;
        this.reviewerRepo = reviewerRepo;
        this.reviewService = reviewService;
    }


    /*
    helper methods
     */

    private void requireAdministratorRole() {
        if (!AppContext.isAdministrator()) {
            throw new AccessDeniedException("User does not have administrator privileges");
        }
    }

    private ResponseEntity<Void> markReviewWithStatus(long reviewId, int newStatus) throws Exception {
        //requireAdministratorRole();

        reviewService.setReviewStatus(reviewId, newStatus);

        return ResponseEntity.ok().build();
    }




    /*
    API endpoints
    TODO: test these endpoints with Postman or similar tool, and implement a simple admin UI if time permits.
     */


    /**
     * API endpoint to mark a review as approved by id.
     *
     * @param reviewId
     * @return
     * @throws Exception
     */

    @PostMapping("/api/review/mark-approved")
    public ResponseEntity<Void> markReviewAsApproved(@RequestParam long reviewId) throws Exception {
        requireAdministratorRole();

        return markReviewWithStatus(reviewId, Review.REVIEW_STATUS_APPROVED);
    }





    /**
     * API endpoint to mark a review as rejected by id.
     * @param reviewId
     * @return
     * @throws Exception
     */

    @PostMapping("/api/review/mark-rejected")
    public ResponseEntity<Void> markReviewAsRejected(@RequestParam long reviewId) throws Exception {
        requireAdministratorRole();

        return markReviewWithStatus(reviewId, Review.REVIEW_STATUS_REJECTED);
    }





    /**
     * API endpoint to mark a review as pending by id.
     *
     * @param reviewId
     * @return
     * @throws Exception
     */

    @PostMapping("/api/review/mark-pending")
    public ResponseEntity<Void> markReviewAsPending(@RequestParam Long reviewId) throws Exception {
        requireAdministratorRole();

        return markReviewWithStatus(reviewId, Review.REVIEW_STATUS_PENDING);
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


    // todo: fix route name (maybe /api/reviews/{reviewId} or similar), and add authorization check for tenantId
    //  (should match the tenant of the review, or be a super admin)
    @DeleteMapping("/api/review/delete")
    public ResponseEntity<Void> deleteReviewApi(
        @RequestParam Long tenantId,
        @RequestParam Long reviewId,
        HttpServletRequest req
    ) throws Exception {
        requireAdministratorRole();

        try {
            // validate parameters
            if((tenantId.compareTo(0L) <= 0) || (reviewId.compareTo(0L) <= 0))
                throw new BadRequestException();

            reviewService.deleteById(reviewId);

            return ResponseEntity.ok().build();
        } catch(Exception e) {
            if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}