package root;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.app.AppConfig;
import root.app.AppRequestSchema;
import root.common.testdata.FunnyUserNameGenerator;
import root.common.testdata.IpsumLoremGenerator;
import root.database.*;
import root.includes.logger.Logger;
import root.models.Review;
//import root.models.Reviewer;
import root.models.Tenant;
import root.models.TenantDomain;
import root.A_TODO.no_test_extra.TryWithTimer;
import root.services.PasswordService;
import root.repositories.*;
//import root.repositories.TenantRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;


// -Dspring.profiles.active=local


@Component
public class DatabaseManager {
    public static boolean DEBUG = true;
    public static boolean VERBOSE_SCOPE = true;

    //@Autowired
    //ReviewerRepository reviewerRepo;

    @Autowired
    TenantRepository tenantRepo;

    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    PasswordService passwordService;

    @Autowired
    private TenantDomainRepository tenantDomainRepository;

    public static DatabaseManager create(){
        return new DatabaseManager();
    }

    private void cleanTable(String tableName) {
        FSQLQuery.create("TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE")
            .update();
    }

    public void cleanTenantSchema(){
        Logger.log("Cleaning tenant schema ...");
        cleanTable(AppConfig.REVIEW_TABLE_NAME);
        cleanTable(AppConfig.REVIEWER_TABLE_NAME);
        cleanTable(AppConfig.REVIEW_VOTE_TABLE_NAME);
        Logger.log("Cleaning tenant schema OK");
    }

    public void resetPublicSchema(){
        try(var __ = Logger.scope("Resetting tenant schema...")) {
            try (var ignore1 = AppRequestSchema.withThreadSchema("public")) {
                Logger.log("Cleaning public schema ...");
                cleanTable(AppConfig.TENANT_TABLE_NAME);
                cleanTable(AppConfig.TENANT_DOMAIN_TABLE_NAME);
                Logger.log("Cleaning public schema OK");

                Logger.log("Inserting tenants ...");
                insertTenants();
                Logger.log("Inserting tenants OK");
            } catch (Exception e) {
                Logger.log("Error trying to reset public schema");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void resetTenantSchema() {
        try(var __1 = Logger.scope("resetTenantSchema")){
            cleanTenantSchema();

            try(var __2 = Logger.scope("inserting schema demo data'" + AppRequestSchema.get() + "' ...")) {
                //insertAuthors();
                insertReviews();
            }
        }
    }

    private void insertTenants(){
        try (var p = Logger.scope("Inserting tenant", VERBOSE_SCOPE)) {
            String password = "password1";
            String passwordHash = passwordService.encode(password);

            Tenant tenant = new Tenant();
            tenant.setName("Tenant1");
            tenant.setEmail("tenant1@test.com");
            tenant.setDomain("tenant1.test.com");
            tenant.setApiKey("tenant-1-api-key");
            tenant.setPasswordHash(passwordHash);
            tenant.setPasswordSalt("");
            tenant.setSchemaName("test");

            tenantRepo.save(tenant);

            TenantDomain tenantDomain = new TenantDomain();
            tenantDomain.setDomain("localhost");
            tenantDomain.setTenantId(tenant.getId());
            tenantDomainRepository.save(tenantDomain);

            if(DEBUG) Logger.log("Inserted tenant: " + tenant);
        }
    }
//
//    private Reviewer createReviewer(
//        String email,
//        String displayName,
//        String passwordHash,
//        String passwordSalt,
//        Instant createdAt,
//        Instant verifiedAt
//    ) {
//        Reviewer reviewer = new Reviewer();
//        reviewer.setEmail(email);
//        reviewer.setDisplayName(displayName);
//        reviewer.setPasswordHash(passwordHash);
//        reviewer.setPasswordSalt(passwordSalt);
//        reviewer.setCreatedAt(createdAt);
//        reviewer.setVerifiedAt(verifiedAt);
//
//        return reviewer;
//    }
//    private void insertAuthors() {
//        // TODO: move to
//        var passwordHashForPasswordPass = passwordService.encode("myPassword1");
//
//        List<Reviewer> reviewers = List.of(
//            createReviewer("test@test.com", "Leif", passwordHashForPasswordPass, "", Instant.now(), Instant.now()),
//            createReviewer("alice@example.com", "Alice", "hash1", "", Instant.now(), Instant.now()),
//            createReviewer("bob@example.com", "Bob", "hash2", "", Instant.now(), Instant.now()),
//            createReviewer("charlie@example.com", "Charlie", "hash3", "", Instant.now(), Instant.now()),
//            createReviewer("diana@example.com", "Diana", "hash4", "", Instant.now(), Instant.now()),
//            createReviewer("eve@example.com", "Eve", "hash5", "", Instant.now(), Instant.now())
//        );
//
//        reviewers.forEach(reviewerRepo::save);
//
//        if (DEBUG) Logger.log("Inserted reviewers: " + reviewers);
//    }

    private Review createReview(int status, String external_id, String displayName, long author_id, String title, String comment, int score, Instant created_at) {
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


    private void insertReviews() {
        try {


            try (var p = Logger.scope("Inserting demo ratings", DEBUG)) {
                String path1 = "/product/1";
                String path2 = "/product/2";
                String path3 = "en-litt-annen-sti";

                int[] firstDay = {-365 * 2};

                Supplier<Instant> increasingPastInstant = () -> Instant.now().plus(Duration.ofDays(firstDay[0] += 10));

                Supplier<String> username = FunnyUserNameGenerator::generate;
                Supplier<String> title = () -> IpsumLoremGenerator.generate(2 + (int) (Math.random() * 3));
                Supplier<String> comment = () -> Strings.left(IpsumLoremGenerator.generate(10 + (int) (Math.random() * 20)), 255);

                List<Review> reviews = List.of(
                    createReview(Review.REVIEW_STATUS_APPROVED, path1, username.get(), 1L, title.get(), comment.get(), 5, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_APPROVED, path1, username.get(), 1L, title.get(), comment.get(), 5, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_APPROVED, path1, username.get(), 1L, title.get(), comment.get(), 5, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_APPROVED, path1, username.get(), 1L, title.get(), comment.get(), 5, increasingPastInstant.get()),

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
                    createReview(Review.REVIEW_STATUS_PENDING, path1, username.get(), 1L, title.get(), comment.get(), 4, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_REJECTED, path1, username.get(), 1L, title.get(), comment.get(), 3, increasingPastInstant.get()),

                    createReview(Review.REVIEW_STATUS_APPROVED, path3, username.get(), 2L, title.get(), comment.get(), 2, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_APPROVED, path3, username.get(), 2L, title.get(), comment.get(), 4, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_APPROVED, path3, username.get(), 2L, title.get(), comment.get(), 4, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_APPROVED, path3, username.get(), 2L, title.get(), comment.get(), 3, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_APPROVED, path3, username.get(), 2L, title.get(), comment.get(), 3, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_APPROVED, path3, username.get(), 2L, title.get(), comment.get(), 5, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_PENDING, path3, username.get(), 1L, title.get(), comment.get(), 2, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_PENDING, path3, username.get(), 1L, title.get(), comment.get(), 4, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_REJECTED, path3, username.get(), 1L, title.get(), comment.get(), 4, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_REJECTED, path3, username.get(), 1L, title.get(), comment.get(), 2, increasingPastInstant.get()),

                    createReview(Review.REVIEW_STATUS_APPROVED, path2, username.get(), 1L, title.get(), comment.get(), 1, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_APPROVED, path2, username.get(), 3L, title.get(), comment.get(), 4, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_PENDING, path2, username.get(), 1L, title.get(), comment.get(), 5, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_PENDING, path2, username.get(), 1L, title.get(), comment.get(), 5, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_REJECTED, path2, username.get(), 1L, title.get(), comment.get(), 4, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_REJECTED, path2, username.get(), 1L, title.get(), comment.get(), 3, increasingPastInstant.get()),
                    createReview(Review.REVIEW_STATUS_REJECTED, path2, username.get(), 1L, title.get(), comment.get(), 2, increasingPastInstant.get())
                );

                try(var __ = new TryWithTimer("with transaction")) {
                    // do actual insertion
                    DataSourceManager.begin();

                    if (DataSourceManager.TX.get() != null) {
                        Logger.log("We inside a transaction");
                    }
                    Logger.log("Inserting " + reviews.size() + " reviews ...");
                    reviews.forEach(reviewRepo::save);
                    if (DEBUG) Logger.log("Inserted reviews ... OK");

                    DataSourceManager.commit();
                }
            }

        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error inserting reviews", e);
        }

    }
}

