package root.database;

import root.ReviewRepository;
import root.models.repositories.JdbcReviewRepository;
import root.models.repositories.JdbcReviewerRepository;
import root.models.Review;
import root.models.Reviewer;

import java.util.Map;

import static root.database.FSQL.makeArr;



public class SPIImpl {
    private final JdbcReviewRepository reviewRepo;

    public SPIImpl(JdbcReviewRepository reviewRepo) throws Exception{
        this.reviewRepo = reviewRepo;
    }

    public static void delete_all_reviews_by_tenant_id(long tenantId) throws Exception {
        FSQL.table_delete_where(DB.getConnection(), "reviews", makeArr(tenantId));
    }

    public static Reviewer insert_reviewer(long tenant_id, String email, String displayName, String passwordHash) throws Exception {
        Reviewer r = new Reviewer();
        r.setTenantId(tenant_id);
        r.setEmail(email);
        r.setDisplayName(displayName);
        r.setPasswordHash(passwordHash);

        var repo = new JdbcReviewerRepository();
        repo.create(r);

        return r;
    }

    public static Review insert_review(long tenant_id, String external_id, long author_id, String comment, int score) throws Exception {
        Review r = new Review();
        r.setTenantId(tenant_id);
        r.setExternalId(external_id);
        r.setAuthorId(author_id);
        r.setComment(comment);
        r.setScore(score);

        var repo = ReviewRepository.create(DB.getConnection(), Map.of("modelClass", Review.class, "tableName", "reviews"));
        repo.create(r);

        return r;
    }

}
