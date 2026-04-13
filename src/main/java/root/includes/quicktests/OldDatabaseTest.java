package root.includes.quicktests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.includes.logger.Logger;
import root.models.Reviewer;
import root.services.PasswordService;
import root.repositories.ReviewRepository;
import root.repositories.ReviewerRepository;
import root.repositories.TenantRepository;

import java.time.Instant;
import java.util.List;

@Deprecated
@Service
class OldDatabaseTest {
    public static boolean DEBUG = false;

    @Autowired
    ReviewerRepository reviewerRepo;

    @Autowired
    TenantRepository tenantRepo;

    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    PasswordService passwordService;

    public void run() throws Exception {
        try (var ignore = Logger.scope("Running DBTest...", DEBUG)) {

            // fetch and print ratings
            if (DEBUG) fetchAndPrintReviews();

            // end print
            if (DEBUG) System.out.println("DB Test ran ok");
        }
    }

    private Reviewer insertReviewer(
        String authorEmail,
        String authorDisplayName,
        String authorPassword
    ) {
        String authorPasswordHash = passwordService.encode(authorPassword);

        Reviewer reviewer = new Reviewer();
        reviewer.setEmail(authorEmail);
        reviewer.setDisplayName(authorDisplayName);
        reviewer.setPasswordHash(authorPasswordHash);
        //reviewer.setPasswordSalt(authorPasswordSalt);
        reviewer.setCreatedAt(Instant.now());

        reviewerRepo.save(reviewer);

        return reviewer;
    }

    private void fetchAndPrintReviews() throws Exception {
        var reviews = reviewRepo.findAll();

        printList(reviews, "PrintList Reviews (" + reviews.size() + ")");
    }

    private <T> void printList(List<T> elements, String title) {
        Logger.log();

        if (title != null && !title.isEmpty()) {
            Logger.log(title);
        }

        elements.forEach(Logger::log);
    }
}
