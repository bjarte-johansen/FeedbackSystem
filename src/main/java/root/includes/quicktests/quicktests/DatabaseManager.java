package root.includes.quicktests.quicktests;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.app.AppConfig;
import root.common.utils.FunnyUserNameGenerator;
import root.common.utils.IpsumLoremGenerator;
import root.database.*;
import root.includes.logger.logger.Logger;
import root.models.Review;
import root.models.Reviewer;
import root.models.Tenant;
import root.services.PasswordService;
import root.repositories.*;
//import root.repositories.TenantRepository;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

import static root.common.utils.Preconditions.checkArgument;

//import static root.database.SPIImpl.insert_review;

@Component
public class DatabaseManager {
    public static boolean DEBUG = false;

    @Autowired
    ReviewerRepository reviewerRepo;

    @Autowired
    TenantRepository tenantRepo;

    @Autowired
    ReviewRepository reviewRepo;

    public static DatabaseManager create(){
        return new DatabaseManager();
    }

    private void cleanTable(String tableName) throws Exception {
        FSQLQuery.create("TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE")
            .update();
    }

    public void clean() throws Exception{
        cleanTable(AppConfig.REVIEW_TABLE_NAME);
        cleanTable(AppConfig.REVIEWER_TABLE_NAME);
        cleanTable(AppConfig.TENANT_TABLE_NAME);
        cleanTable(AppConfig.REVIEW_VOTE_TABLE_NAME);
    }

    public void resetDemoData() throws Exception {
        clean();

        validateReposReferences();

        try(var ignore = Logger.scope("Inserting demo data...", DEBUG)) {
            insertTenants();
            insertAuthors();
            insertDemoRatings();
        }
    }

    private void validateReposReferences(){
        checkArgument(tenantRepo != null, "tenantRepo is null");
        checkArgument(reviewRepo != null, "reviewerRepo is null");
        checkArgument(reviewerRepo != null, "reviewRepo is null");
    }



    private void insertTenants() throws Exception {

        try (var p = Logger.scope("Inserting tenant", DEBUG)) {
            Tenant tenant = new Tenant();
            tenant.setName("Tenant 1");
            tenant.setId(1L);
            tenant.setEmail("tenant-1@demo.only");
            tenant.setDomain("tenant1.demo.only");
            tenant.setApiKey("tenant-1-api-key");
            tenant.setPasswordHash(PasswordService.hash("tenant-1", "salt"));
            tenant.setPasswordSalt("salt");
            tenant.setSchemaName("tenant_1");

            //TenantRepository tenantRepo = RepositoryProxyConstructor.create(TenantRepository.class);
            tenantRepo.save(tenant);

            if(DEBUG) Logger.log("Inserted tenant: " + tenant);
        }
    }

    private Reviewer createReviewer(
        String email,
        String displayName,
        String passwordHash,
        String passwordSalt,
        Instant createdAt,
        Instant verifiedAt
    ) {
        Reviewer reviewer = new Reviewer();
        reviewer.setEmail(email);
        reviewer.setDisplayName(displayName);
        reviewer.setPasswordHash(passwordHash);
        reviewer.setPasswordSalt(passwordSalt);
        reviewer.setCreatedAt(createdAt);
        reviewer.setVerifiedAt(verifiedAt);

        //if(DEBUG) Logger.log("Created reviewer: " + r);

        return reviewer;
    }
    private void insertAuthors() throws SQLException, Exception {
        // TODO: move to
        var passwordHashForPasswordPass = PasswordService.hash("pass", "salt");

        List<Reviewer> reviewers = List.of(
            createReviewer("test@test.com", "Leif", passwordHashForPasswordPass, "salt", Instant.now(), Instant.now()),
            createReviewer("alice@example.com", "Alice", "hash1", "salt1", Instant.now(), Instant.now()),
            createReviewer("bob@example.com", "Bob", "hash2", "salt2", Instant.now(), Instant.now()),
            createReviewer("charlie@example.com", "Charlie", "hash3", "salt3", Instant.now(), Instant.now()),
            createReviewer("diana@example.com", "Diana", "hash4", "salt4", Instant.now(), Instant.now()),
            createReviewer("eve@example.com", "Eve", "hash5", "salt5", Instant.now(), Instant.now())
        );

        reviewers.forEach(reviewerRepo::save);
    }

    private Review createReview(int status, String external_id, String displayName, long author_id, String title, String comment, int score, Instant created_at) throws Exception {
        Review r = new Review();
        r.setStatus(status);
        r.setExternalId(external_id);
        r.setAuthorId(author_id);
        r.setAuthorName(displayName);
        r.setTitle(title);
        r.setComment(comment);
        r.setScore(score);
        r.setCreatedAt(created_at);

        //if(DEBUG) Logger.log("Created review: " + r);

        return r;
    }


    private void insertDemoRatings() throws SQLException, Exception {
        try(var p = Logger.scope("Inserting demo ratings", DEBUG)) {
            String path1 = "/product/1";
            String path2 = "/product/2";
            String path3 = "en-litt-annen-sti";

            int[] firstDay = {-365 * 2};

            Supplier<Instant> increasingPastInstant = () -> Instant.now().plus(Duration.ofDays(firstDay[0] += 30));

            Supplier<String> username = FunnyUserNameGenerator::generate;
            Supplier<String> title = () -> IpsumLoremGenerator.generate(2 + (int) (Math.random() * 3));
            Supplier<String> comment = () -> Strings.left(IpsumLoremGenerator.generate(10 + (int) (Math.random() * 20)), 255);

            List<Review> reviews = List.of(
                createReview(Review.REVIEW_STATUS_APPROVED, path1, username.get(), 1L, title.get(), comment.get(), 5, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path1, username.get(), 2L, title.get(), comment.get(), 4, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path1, username.get(), 3L, title.get(), comment.get(), 3, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path1, username.get(), 1L, title.get(), comment.get(), 4, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path1, username.get(), 1L, title.get(), comment.get(), 2, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path1, username.get(), 1L, title.get(), comment.get(), 5, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path1, username.get(), 1L, title.get(), comment.get(), 1, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path1, username.get(), 1L, title.get(), comment.get(), 4, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path1, username.get(), 1L, title.get(), comment.get(), 3, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path1, username.get(), 1L, title.get(), comment.get(), 5, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_PENDING, path1, username.get(), 1L, title.get(), comment.get(), 5, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_PENDING, path1, username.get(), 1L, title.get(), comment.get(), 5, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_REJECTED, path1, username.get(), 1L, title.get(), comment.get(), 5, increasingPastInstant.get()),

                createReview(Review.REVIEW_STATUS_APPROVED, path3, username.get(), 2L, title.get(), comment.get(), 2, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path3, username.get(), 2L, title.get(), comment.get(), 4, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path3, username.get(), 2L, title.get(), comment.get(), 4, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path3, username.get(), 2L, title.get(), comment.get(), 3, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path3, username.get(), 2L, title.get(), comment.get(), 3, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path3, username.get(), 2L, title.get(), comment.get(), 5, increasingPastInstant.get()),

                createReview(Review.REVIEW_STATUS_APPROVED, path2, username.get(), 1L, title.get(), comment.get(), 1, increasingPastInstant.get()),
                createReview(Review.REVIEW_STATUS_APPROVED, path2, username.get(), 3L, title.get(), comment.get(), 4, increasingPastInstant.get())
            );

            reviews.forEach(reviewRepo::save);
        }
    }
}

