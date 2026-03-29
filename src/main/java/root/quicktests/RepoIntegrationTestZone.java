package root.quicktests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.RepositoryProxyConstructor;
import root.database.DB;
import root.database.FSQL;
import root.database.FSQLQuery;
import root.database.SqlFactory;
import root.logger.Logger;
import root.models.FNV1A64HashGenerator;
import root.models.IReview;
import root.models.Review;
import root.repositories.ReviewRepository;

import java.util.List;

@Component
public class RepoIntegrationTestZone {
    @Autowired
    ReviewRepository repo;

    public void execTest() throws Exception {
        Review review = new Review();
        {
            review.setAuthorId(3L);
            review.setComment("Im old");
            review.setExternalId("/product/69");
            review.setTitle("My review title");
            review.setScore(4);
            review.setTenantId(1L);

            repo.save(review);
        }

        String sql = SqlFactory.createUpdateSql(
            "review",
            FSQL.linkedNameValueMap(
                "title", "My review title",
                "comment", "Im old",
                "score", 4
            )
            , FSQL.makeArr(
                "tenant_id", 1L,
                "external_id_hash", FNV1A64HashGenerator.generate("/product/69"),
                "external_id", "/product/69"
            )
        );

        DB.with(conn -> {
            FSQLQuery.create(conn, sql)
                .bindNamed("comment", "Im updated")
                .bindNamed("score", 4)
                .bindNamed("tenant_id", 1L)
                .bindNamed("external_id_hash", FNV1A64HashGenerator.generate("/product/69"))
                .bindNamed("external_id", "/product/69")
                .update();

            return null;
        });

/*
        FSQLQuery.create(conn, "UPDATE reviews SET comment = :comment WHERE id = :id")
            .bindNamed("comment", "Im updated with named param")
            .bindNamed("id", review.getId())
            .update();

 */

        {
            Logger.warn("ACTUALLY USING findByExternalId");
            List<Review> reviews = repo.findByExternalIdHashAndExternalId(null, "/product/69");

            for (IReview r : reviews) {
                System.out.println("Fetched review: " + r.getComment() + " with score: " + r.getScore());
            }

        }
    }


    public static void testProxy() throws Exception {
        ReviewRepository repo = RepositoryProxyConstructor.create(ReviewRepository.class);

        try (var scope = Logger.scope("Testing proxy repository...")) {
            Logger.log("Created proxy repository for reviews");
            Review r1 = new Review();
            r1.setAuthorId(2L);
            r1.setComment("Proxied create");
            r1.setExternalId("/proxy/create");
            r1.setTitle("Proxy review title");
            r1.setScore(4);
            r1.setTenantId(1L);
            repo.save(r1);
        }

        {
            var results = repo.findAll();
            printEntities(results, "review");

            repo.delete(results.getFirst());
        }

        {
            var resultsByScoreAndExternalId = repo.findByScoreAndExternalId(4, "2");
            printEntities(resultsByScoreAndExternalId, "reviews by author and external id");

            var results = repo.findAll();
            repo.deleteAll(results);
        }
    }

    public static void printEntities(List<?> els, String... strings) {
        String entityName = (strings != null && strings.length > 0) ? strings[0] : "Object";
        Logger.log("Found " + els.size() + " (" + entityName + ")");

        try (var scope = Logger.scope("Elements")) {
            els.forEach(Logger::log);
        }
    }
}
