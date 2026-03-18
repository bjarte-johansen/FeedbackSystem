package root;

import root.database.*;
import root.logger.*;
import root.models.FNV1A64HashGenerator;
import root.models.IReview;
import root.models.interfaces.IReviewRepository;
//import root.models.repositories.JdbcReviewRepository;
import root.models.Review;
import root.models.repositories.JdbcReviewRepository;

import java.sql.*;
import java.util.List;
import java.util.Map;


//import static java.lang.StringTemplate.STR;


public class Main {
    static Connection conn = DB.getConnection();


    public static void execTest() throws Exception {
        JdbcReviewRepository repo = new JdbcReviewRepository();

        //String externalId = "/product/69";
        //long externalIdHash = FNV1A64HashGenerator.generate(externalId);

        Review review = new Review();
        {

            review.setAuthorId(3L);
            review.setComment("Im old");
            review.setExternalId("/product/69");
            //review.setExternalIdHash(externalIdHash);
            review.setScore(4);
            review.setTenantId(1L);

            repo.create(review);
        }



        FSQLQuery.create(conn, "UPDATE reviews SET comment = :comment, score = :score WHERE tenant_id = :tenant_id AND external_id_hash = :external_id_hash AND external_id = :external_id")
            .bindNamed("comment", "Im updated")
            .bindNamed("score", 4)
            .bindNamed("tenant_id", 1L)
            .bindNamed("external_id_hash", FNV1A64HashGenerator.generate("/product/69"))
            .bindNamed("external_id", "/product/69")
            .update();

/*
        FSQLQuery.create(conn, "UPDATE reviews SET comment = :comment WHERE id = :id")
            .bindNamed("comment", "Im updated with named param")
            .bindNamed("id", review.getId())
            .update();

 */

        {
            List<IReview> reviews = repo.findByExternalId(1L, "/product/69", null, null);

            for(IReview r : reviews){
                System.out.println("Fetched review: " + r.getComment() + " with score: " + r.getScore());
            }

        }
    }


    public static void testProxy() throws Exception{
        IReviewRepository repo = ReviewRepository.create(conn, Map.of(
            "tableName", "reviews",
            "modelClass", Review.class
        ));

        try(var scope = Logger.scope("Testing proxy repository...")) {
            Logger.log("Created proxy repository for reviews");
            Review r1 = new Review();
            r1.setAuthorId(2L);
            r1.setComment("Proxied create");
            r1.setExternalId("/proxy/create");
            r1.setScore(4);
            r1.setTenantId(1L);
            repo.create(r1);
        }

        {
            //var results = repo.findByScoreAndExternalId(4, "2");
            var results = repo.findAll();
            Logger.log("Found " + results.size() + " reviews by author and external id");
            try (var scope = Logger.scope("Iterating results...")) {
                results.forEach(Logger::log);
            }

            repo.delete(results.get(0));
        }


        {
            var results = repo.findByScoreAndExternalId(4, "2");
            Logger.log("Found " + results.size() + " reviews by author and external id");
            try (var scope = Logger.scope("Iterating results...")) {
                results.forEach(Logger::log);
            }
        }
    }

    public static void main(String[] args) throws Throwable{
        //testScanString();

        //System.setOut(new CallerPrintStream(System.out));

        String[] test = new String[]{
            "findByAuthorIdAndExternalId",
            "findByAuthorIdAndScoreLessOrEqualOrAuthorName",
            "findByScoreAndExternalId"
        };

        var tokens = RepoProxyMethodScanner.tokenize(test[1]);


        boolean b = true;
        if(b) {
            System.out.println(Logger.getConfig());

            DBTest.clean();

            //execTest();


            DB.printMetaData();

            try {
                DBTest.run();

                try(var p = Logger.scope("Running proxy test...")){
                    testProxy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                DBTest.clean();
            }
        }
    }
}