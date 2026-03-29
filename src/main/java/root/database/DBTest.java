package root.database;

import org.apache.logging.log4j.util.Strings;
import root.ProxyRepositoryFactory;
import root.common.utils.FunnyUserNameGenerator;
import root.common.utils.IpsumLoremGenerator;
import root.common.utils.RandomPastInstant;
import root.interfaces.ReviewRepositoryCustom;
import root.logger.Logger;
import root.models.Review;
import root.models.Reviewer;
import root.models.Tenant;
import root.models.services.PasswordService;
import root.repositories.*;
//import root.repositories.TenantRepository;
import root.utils.PasswordSaltGenerator;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

//import static root.database.SPIImpl.insert_review;

public class DBTest {
    public static void cleanTable(String tableName) throws Exception {
        DB.with(conn -> {
            FSQLQuery.create(conn, "TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE")
                .update();
            return null;
        });
    }

    public static void clean() throws Exception{
        cleanTable("reviews");
        cleanTable("reviewers");
        cleanTable("tenants");
    }

    public static void run() throws Exception {
        try(var ignore = Logger.scope("Running DBTest...")) {

            DB.with(conn -> {
                insertTenants(conn);

                insertAuthors(conn, 1L);

                // insert demo ratings for tenant_id = 1
                DBTest.insertDemoRatings(conn, 1L);

                // fetch and print ratings for tenant_id = 1
                DBTest.fetchAndPrintReviews(conn, 1);

                return null;
            });

            // end print
            System.out.println("OK");
        }
    }

    public static void insertTenants(Connection conn) throws Exception {
        try (var p = Logger.scope("Inserting tenant")) {
            Tenant tenant = new Tenant();
            tenant.setName("Tenant 1");
            tenant.setId(1L);
            tenant.setEmail("tenant-1@demo.only");
            tenant.setDomain("tenant1.demo.only");
            tenant.setApiKey("tenant-1-api-key");
            tenant.setPasswordHash(PasswordService.hash("tenant-1", "salt"));
            tenant.setPasswordSalt("salt");

            var tenantRepo = ProxyRepositoryFactory.createTenantRepo(new TenantRepositoryCustomImpl(), Map.of("tableName", "tenants", "modelClass", Tenant.class));
            TenantRepository repo = ProxyRepositoryFactory.create(TenantRepository.class);
            tenantRepo.create(tenant);

            Logger.log("Inserted tenant: " + tenant);
        }
    }

/*
    static LinkedHashMap<String, Object> makeLinkedMap(Object... values) {
        return linkedMap(values);
    }
 */

    static void insertAuthors(Connection conn, long tenantId) throws SQLException, Exception {
        var passwordHashForPasswordPass = PasswordService.hash("pass", "salt");

        Reviewer[] reviewers = {
            new Reviewer(tenantId, "test@test.com", "Leif", passwordHashForPasswordPass, "salt", Instant.now(), Instant.now()),
            new Reviewer(tenantId, "alice@example.com", "Alice", "hash1", "salt1", Instant.now(), Instant.now()),
            new Reviewer(tenantId, "bob@example.com", "Bob", "hash2", "salt2", Instant.now(), Instant.now()),
            new Reviewer(tenantId, "charlie@example.com", "Charlie", "hash3", "salt3", Instant.now(), Instant.now()),
            new Reviewer(tenantId, "diana@example.com", "Diana", "hash4", "salt4", Instant.now(), Instant.now()),
            new Reviewer(tenantId, "eve@example.com", "Eve", "hash5", "salt5", Instant.now(), Instant.now())
        };

        ReviewerRepositoryCustomImpl reviewerRepo = new ReviewerRepositoryCustomImpl();
        for (Reviewer reviewer : reviewers) {
            reviewerRepo.create(reviewer);
        }
    }



    static Reviewer insertReviewer(
        Connection conn,
        long tenantId,
        String authorEmail,
        String authorDisplayName,
        String authorPassword
        ) throws Exception
    {
        String authorPasswordSalt = PasswordSaltGenerator.generate(16);
        String authorPasswordHash = PasswordService.hash(authorPassword, authorPasswordSalt);

        Reviewer reviewer = new Reviewer(
            tenantId,
            authorEmail,
            authorDisplayName,
            authorPasswordHash,
            authorPasswordSalt,
            Instant.now(),
            null
        );

        ReviewerRepository reviewerRepo = new ProxyRepositoryFactory.create(ReviewerRepository.class);
        reviewerRepo.create(reviewer);

        return reviewer;
    }

    public static Review insert_review(long tenant_id, String external_id, String displayName, long author_id, String title, String comment, int score, Instant created_at) throws Exception {
        Review r = new Review();
        r.setTenantId(tenant_id);
        r.setExternalId(external_id);
        r.setAuthorId(author_id);
        r.setAuthorName(displayName);
        r.setTitle(title);
        r.setComment(comment);
        r.setScore(score);
        r.setCreatedAt(created_at);

        DB.with(conn -> {
            var repo = ProxyRepositoryFactory.createReviewRepository();
            repo.create(r);

            Logger.log("Inserted review: " + r);

            return r;
        });

        return r;
    }

    static void insertDemoRatings(Connection conn, long tenantId) throws SQLException, Exception {
        try(var p = Logger.scope("Inserting demo ratings for tenant_id = " + tenantId)) {
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
                insert_review(tenantId, path1, username.get(), 1L, title.get(), comment.get(), 5, createdAt.get()),
                insert_review(tenantId, path1, username.get(), 2L, title.get(), comment.get(), 4, createdAt.get()),
                insert_review(tenantId, path1, username.get(), 3L, title.get(), comment.get(), 3, createdAt.get()),
                insert_review(tenantId, path2, username.get(), 3L, title.get(), comment.get(), 4, createdAt.get())
            );

            String sql = SqlFactory.createUpdateSql(
                "reviews",
                FSQL.linkedNameValueMap("comment", "Updated text!!", "score", 4),
                FSQL.makeArr("tenant_id =", "?", "external_id =", "?")
            );

            FSQLQuery.create(conn, sql)
                .bind("Updated text!!", 4, 1L, path2)
                .update();
        }
    }


    //

    static void fetchAndPrintReviews(Connection conn, long tenantId) throws Exception {
        ReviewRepositoryCustom repo = ProxyRepositoryFactory.createReviewRepository(new JdbcReviewRepository(), Map.of(
            "tableName", "reviews",
            "modelClass", Review.class
        ));


        var reviews = repo.findByTenantId(tenantId);

        printList("PrintList Reviews (" + reviews.size() + ")", reviews);
    }

    static <T> void printList(String title, List<T> elements)    {
        Logger.log();

        if(title != null && !title.isEmpty())
            Logger.log(title);

        elements.forEach(Logger::log);
    }
}