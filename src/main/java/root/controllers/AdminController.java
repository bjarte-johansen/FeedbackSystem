package root.controllers;

import jakarta.servlet.http.HttpServletRequest;
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

import java.util.Map;

@Controller
public class AdminController {
    //private static final Logger log = new Logger();
    public static boolean DEBUG_ERRORS = true;

    // TODO: must be false for production


    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    ReviewerRepository reviewerRepo;

    @Autowired
    ReviewService reviewService;


    /*
    helper methods
     */

    private ResponseEntity<Void> markReviewWithStatus(long reviewId, int newStatus) throws Exception {
        if(!AppContext.isAdministrator()) return ResponseEntity.status(401).build();

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
    @PostMapping("/api/mark-review-approved/{customer}/{reviewId}")
    public ResponseEntity<Void> markReviewAsApproved(@PathVariable Long customer, @PathVariable long reviewId) throws Exception {
        // authorization check in markReviewWithStatus method

        return markReviewWithStatus(reviewId, Review.REVIEW_STATUS_APPROVED);
    }


    /**
     * API endpoint to mark a review as rejected by id.
     * @param reviewId
     * @return
     * @throws Exception
     */

    @PostMapping("/api/mark-review-rejected/{customer}/{reviewId}")
    public ResponseEntity<Void> markReviewAsRejected(@PathVariable Long customer, @PathVariable long reviewId) throws Exception {
        // authorization check in markReviewWithStatus method

        return markReviewWithStatus(reviewId, Review.REVIEW_STATUS_REJECTED);
    }


    /**
     * API endpoint to mark a review as pending by id.
     *
     * @param reviewId
     * @return
     * @throws Exception
     */

    @PostMapping("/api/mark-review-pending/{customer}/{reviewId}")
    public ResponseEntity<Void> markReviewAsPending(@PathVariable Long customer, @PathVariable long reviewId) throws Exception {
        // authorization check in markReviewWithStatus method

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

    @DeleteMapping("/api/delete-review/{tenantId}/{reviewId}")
    public ResponseEntity<Void> deleteReviewApi(
        @PathVariable long tenantId,
        @PathVariable long reviewId,
        HttpServletRequest req
    ) throws Exception {
        try {
            if(!AppContext.isAdministrator()) return ResponseEntity.status(401).build();

            // validate parameters
            if(tenantId <= 0 || reviewId <= 0) throw new BadRequestException();

            reviewService.deleteById(reviewId);

            return ResponseEntity.ok().build();
        } catch(BadRequestException e){
            return ResponseEntity.badRequest().build();
        } catch(Exception e) {
            if(AppConfig.CONTROLLER_PRINT_STACK_TRACE_ON_ERROR) e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
