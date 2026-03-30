package root.quicktests;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.AppConfig;
import root.RepositoryProxyConstructor;
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
import root.utils.PasswordSaltGenerator;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

//import static root.database.SPIImpl.insert_review;

@Component
public class DBTest {
    @Autowired
    ReviewerRepository reviewerRepo;

    @Autowired
    TenantRepository tenantRepo;

    @Autowired
    ReviewRepository reviewRepo;

    public static DBTest create(){
        return new DBTest();
    }

    public void cleanTable(String tableName) throws Exception {
        DB.with(conn -> {
            FSQLQuery.create(conn, "TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE")
                .update();
            return null;
        });
    }

    public void clean() throws Exception{
        cleanTable(AppConfig.REVIEW_TABLE_NAME);
        cleanTable(AppConfig.REVIEWER_TABLE_NAME);
        cleanTable(AppConfig.TENANT_TABLE_NAME);
    }

    public void run() throws Exception {
        try(var ignore = Logger.scope("Running DBTest...")) {

            DB.with(conn -> {
                if(tenantRepo == null){
                    Logger.log("tenantRepo is null");
                    throw new RuntimeException("tenantRepo is null");
                }
                if(reviewerRepo == null){
                    Logger.log("reviewerRepo is null");
                    throw new RuntimeException("reviewerRepo is null");
                }
                if(reviewRepo == null) {
                    Logger.log("reviewRepo is null");
                    throw new RuntimeException("ReviewRepo is null");
                }

                insertTenants(conn);

                insertAuthors(conn);

                // insert demo ratings
                insertDemoRatings(conn);

                // fetch and print ratings
                fetchAndPrintReviews(conn);

                return null;
            });

            // end print
            System.out.println("OK");
        }
    }

    private void insertTenants(Connection conn) throws Exception {


        try (var p = Logger.scope("Inserting tenant")) {
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

            Logger.log("Inserted tenant: " + tenant);
        }
    }

/*
    static LinkedHashMap<String, Object> makeLinkedMap(Object... values) {
        return linkedMap(values);
    }
 */

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



    private Reviewer insertReviewer(
        Connection conn,
        String authorEmail,
        String authorDisplayName,
        String authorPassword
        ) throws Exception
    {
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

        ReviewerRepository reviewerRepo = RepositoryProxyConstructor.create(ReviewerRepository.class);
        reviewerRepo.save(reviewer);

        return reviewer;
    }

    private Review insert_review(String external_id, String displayName, long author_id, String title, String comment, int score, Instant created_at) throws Exception {
        Review r = new Review();
        r.setExternalId(external_id);
        r.setAuthorId(author_id);
        r.setAuthorName(displayName);
        r.setTitle(title);
        r.setComment(comment);
        r.setScore(score);
        r.setCreatedAt(created_at);
        reviewRepo.save(r);

        Logger.log("Inserted review: " + r);

        return r;
    }

    private void insertDemoRatings(Connection conn) throws SQLException, Exception {
        try(var p = Logger.scope("Inserting demo ratings")) {
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
                insert_review(path1, username.get(), 1L, title.get(), comment.get(), 5, createdAt.get()),
                insert_review(path1, username.get(), 2L, title.get(), comment.get(), 4, createdAt.get()),
                insert_review(path1, username.get(), 3L, title.get(), comment.get(), 3, createdAt.get()),
                insert_review(path2, username.get(), 3L, title.get(), comment.get(), 4, createdAt.get())
            );

            var data = FSQL.linkedNameValueMap("comment", "Updated text!!", "score", 4);
            String sql = SqlFactory.createUpdateSql(
                AppConfig.REVIEW_TABLE_NAME,
                data,
                FSQL.makeArr("external_id =", "?")
            );

            FSQLQuery.create(conn, sql)
                .bindArray(data.values().toArray())
                .bind(path2)
                .update();
        }
    }


    //

    private void fetchAndPrintReviews(Connection conn) throws Exception {
        var reviews = reviewRepo.findAll();

        printList("PrintList Reviews (" + reviews.size() + ")", reviews);
    }

    private <T> void printList(String title, List<T> elements)    {
        Logger.log();

        if(title != null && !title.isEmpty())
            Logger.log(title);

        elements.forEach(Logger::log);
    }
}