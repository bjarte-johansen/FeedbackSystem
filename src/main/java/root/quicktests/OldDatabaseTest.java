package root.quicktests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.logger.Logger;
import root.models.Reviewer;
import root.models.services.PasswordService;
import root.repositories.ReviewRepository;
import root.repositories.ReviewerRepository;
import root.repositories.TenantRepository;
import root.includes.PasswordSaltGenerator;

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
    ) throws Exception {
        String authorPasswordSalt = PasswordSaltGenerator.generate(16);
        String authorPasswordHash = PasswordService.hash(authorPassword, authorPasswordSalt);

        Reviewer reviewer = new Reviewer(
            authorEmail,
            authorDisplayName,
            authorPasswordHash,
            authorPasswordSalt,
            Instant.now(),
            null
        );

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
