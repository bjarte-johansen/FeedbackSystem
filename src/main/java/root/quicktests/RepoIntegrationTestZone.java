package root.quicktests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.database.*;
import root.logger.Logger;
import root.interfaces.IReview;
import root.models.Review;
import root.repositories.ReviewRepository;

import java.util.List;

@Component
public class RepoIntegrationTestZone {
    @Autowired
    ReviewRepository reviewRepo;

    public void execTest() throws Exception {
        Review review = new Review();
        review.setAuthorId(3L);
        review.setComment("Im old");
        review.setExternalId("/product/69");
        review.setTitle("My review title");
        review.setScore(4);
        reviewRepo.save(review);

        String sql = SqlFactory.createUpdateSql(
            "review",
            FSQL.linkedNameValueMap(
                "title", "My review title",
                "comment", "Im old",
                "score", 4
            )
            , FSQL.makeArr(
                "external_id", "/product/69"
            )
        );

        DataSourceManager.with(conn -> {
            FSQLQuery.create(conn, sql)
                .bindNamed("comment", "Im updated")
                .bindNamed("score", 4)
                .bindNamed("external_id", "/product/69")
                .update();

            return null;
        });

        {
            Logger.warn("ACTUALLY USING findByExternalId");
            List<Review> reviews = reviewRepo.findByExternalId("/product/69");

            for (IReview r : reviews) {
                System.out.println("Fetched review: " + r.getComment() + " with score: " + r.getScore());
            }
        }
    }


    public void testProxy() throws Exception {
        //ReviewRepository repo = RepositoryProxyConstructor.create(ReviewRepository.class);

        try (var scope = Logger.scope("Testing proxy repository...")) {
            Logger.log("Created proxy repository for reviews");
            Review r1 = new Review();
            r1.setAuthorId(2L);
            r1.setComment("Proxied create");
            r1.setExternalId("/proxy/create");
            r1.setTitle("Proxy review title");
            r1.setScore(4);
            //r1.setTenantId(1L);
            reviewRepo.save(r1);
        }

        {
            var results = reviewRepo.findAll();
            printEntities(results, "review");

            reviewRepo.delete(results.getFirst());
        }

        {
            var resultsByScoreAndExternalId = reviewRepo.findByScoreAndExternalId(4, "2");
            printEntities(resultsByScoreAndExternalId, "reviews by author and external id");

            var results = reviewRepo.findAll();
            reviewRepo.deleteAll(results);
        }
    }

    public void printEntities(List<?> els, String... strings) {
        String entityName = (strings != null && strings.length > 0) ? strings[0] : "Object";
        Logger.log("Found " + els.size() + " (" + entityName + ")");

        try (var scope = Logger.scope("Elements")) {
            els.forEach(Logger::log);
        }
    }
}
