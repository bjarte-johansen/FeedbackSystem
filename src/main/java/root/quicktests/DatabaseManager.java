package root.quicktests;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.AppConfig;
import root.common.utils.FunnyUserNameGenerator;
import root.common.utils.IpsumLoremGenerator;
import root.common.utils.RandomPastInstant;
import root.database.*;
import root.logger.Logger;
import root.models.Review;
import root.models.Reviewer;
import root.models.Tenant;
import root.models.services.PasswordService;
import root.repositories.*;
//import root.repositories.TenantRepository;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

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
    }

    public void resetDemoData() throws Exception {
        clean();

        validateReposReferences();

        try(var ignore = Logger.scope("Inserting demo data...", DEBUG)) {
            DB.with(conn -> {
                insertTenants(conn);
                insertAuthors(conn);
                insertDemoRatings(conn);
                return null;
            });
        }
    }

    private void validateReposReferences(){
        if(tenantRepo == null){
            if(DEBUG) Logger.log("tenantRepo is null");
            throw new RuntimeException("tenantRepo is null");
        }
        if(reviewerRepo == null){
            if(DEBUG) Logger.log("reviewerRepo is null");
            throw new RuntimeException("reviewerRepo is null");
        }
        if(reviewRepo == null) {
            if(DEBUG) Logger.log("reviewRepo is null");
            throw new RuntimeException("ReviewRepo is null");
        }
    }



    private void insertTenants(Connection conn) throws Exception {

        try (var p = Logger.scope("Inserting tenant", DEBUG)) {
            Tenant tenant = new Tenant();
            tenant.setName("Tenant 1");
            tenant.setId(1L);
            tenant.setEmail("tenant-1@demo.only");
            tenant.setDomain("tenant1.demo.only");
            tenant.setApiKey("tenant-1-api-key");
            tenant.setPasswordHash(PasswordService.hash("tenant-1", "salt"));
            tenant.setPasswordSalt("salt");

            //TenantRepository tenantRepo = RepositoryProxyConstructor.create(TenantRepository.class);
            tenantRepo.save(tenant);

            if(DEBUG) Logger.log("Inserted tenant: " + tenant);
        }
    }

    private void insertAuthors(Connection conn) throws SQLException, Exception {
        // TODO: move to
        var passwordHashForPasswordPass = PasswordService.hash("pass", "salt");

        List<Reviewer> reviewers = List.of(
            new Reviewer("test@test.com", "Leif", passwordHashForPasswordPass, "salt", Instant.now(), Instant.now()),
            new Reviewer("alice@example.com", "Alice", "hash1", "salt1", Instant.now(), Instant.now()),
            new Reviewer("bob@example.com", "Bob", "hash2", "salt2", Instant.now(), Instant.now()),
            new Reviewer("charlie@example.com", "Charlie", "hash3", "salt3", Instant.now(), Instant.now()),
            new Reviewer("diana@example.com", "Diana", "hash4", "salt4", Instant.now(), Instant.now()),
            new Reviewer("eve@example.com", "Eve", "hash5", "salt5", Instant.now(), Instant.now())
        );

        reviewers.forEach(reviewerRepo::save);
    }

    private Review insertReview(String external_id, String displayName, long author_id, String title, String comment, int score, Instant created_at) throws Exception {
        Review r = new Review();
        r.setExternalId(external_id);
        r.setAuthorId(author_id);
        r.setAuthorName(displayName);
        r.setTitle(title);
        r.setComment(comment);
        r.setScore(score);
        r.setCreatedAt(created_at);
        reviewRepo.save(r);

        if(DEBUG) Logger.log("Inserted review: " + r);

        return r;
    }

    private void insertDemoRatings(Connection conn) throws SQLException, Exception {
        try(var p = Logger.scope("Inserting demo ratings", DEBUG)) {
            String path1 = "/product/1";
            String path2 = "/product/2";

            var period = new FSQLPairRecord<>(
                Duration.ofDays(3),
                Duration.ofDays(365 * 2)
            );

            Supplier<String> username = FunnyUserNameGenerator::generate;
            Supplier<String> title = () -> IpsumLoremGenerator.generate(2 + (int) (Math.random() * 3));
            Supplier<String> comment = () -> Strings.left(IpsumLoremGenerator.generate(10 + (int) (Math.random() * 20)), 255);
            Supplier<Instant> createdAt = () -> RandomPastInstant.generate(period.first(), period.second());

            List<Review> reviews = List.of(
                insertReview(path1, username.get(), 1L, title.get(), comment.get(), 5, createdAt.get()),
                insertReview(path1, username.get(), 2L, title.get(), comment.get(), 4, createdAt.get()),
                insertReview(path1, username.get(), 3L, title.get(), comment.get(), 3, createdAt.get()),
                insertReview(path2, username.get(), 3L, title.get(), comment.get(), 4, createdAt.get())
            );
        }
    }
}

