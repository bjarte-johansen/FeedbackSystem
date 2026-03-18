package root.models.repositories;

import org.springframework.stereotype.Repository;
import root.database.DB;
import root.database.FSQL;
import root.database.FSQLQuery;
import root.models.FNV1A64HashGenerator;
import root.models.IReview;
import root.models.QueryOptions;
import root.models.Review;
import root.models.interfaces.IReviewRepository;

import javax.management.Query;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * JdbcReviewRepository is a repository class that implements the review repository interface.
 * It provides methods to perform CRUD operations on reviews in the database using JDBC.
 */


@Repository
public class JdbcReviewRepository implements IReviewRepository {
    protected static Connection getConnection() { return DB.getConnection(); }

    @Override
    public IReview save(IReview review) throws Exception {
        // Implementation to save review to the database

        if(review.getId() == null) {
            return create(review);
        } else {
            return update(review);
        }
    }


    @Override
    public IReview create(IReview review) throws Exception {
        // Implementation to save review to the database

        var fields = FSQL.linkedNameValueMap(
            "tenant_id", review.getTenantId(),
            "external_id", review.getExternalId(),
            "external_id_hash", review.getExternalIdHash(),
            "author_id", review.getAuthorId(),
            "author_name", review.getAuthorName(),
            "score", review.getScore(),
            "comment", review.getComment(),
            "created_at", review.getCreatedAt()
        );

        Long id = FSQLQuery.create(getConnection(), FSQL.createInsertSql(getConnection(), "reviews", fields))
            .bind(fields.values().toArray())
            .insertAndGetId();

        review.setId(id);

        return review;
    }


    @Override
    public IReview update(IReview review) throws Exception {
        // Implementation to save review to the database

        LinkedHashMap<String, Object> fields = FSQL.linkedNameValueMap(
            "tenant_id", review.getTenantId(),
            "external_id", review.getExternalId(),
            "external_id_hash", review.getExternalIdHash(),
            "author_id", review.getAuthorId(),
            "author_name", review.getAuthorName(),
            "score", review.getScore(),
            "comment", review.getComment(),
            "created_at", review.getCreatedAt()
        );

        // FSQLQuery implementation example
        FSQLQuery.create(getConnection(), FSQL.createUpdateSql(getConnection(), "reviews", fields, new Object[]{"id =", null}))
            .bind(fields)
            .bind(review.getId())
            .insertAndGetId();

        return review;
    }

    @Override
    public Optional<Review> findById(long tenantId, long reviewId) throws Exception {
        // Implementation to find review by ID from the database

        return FSQLQuery.create(getConnection(), "SELECT * reviews (id, tenant_id) VALUES (?, ?)")
            .bind(reviewId, tenantId)
            .fetchOne(Review.class);
    }

    @Override
    public List<IReview> findByExternalId(long tenantId, String externalId, Long externalIdHash, QueryOptions queryOptions) throws Exception{
        // Implementation to find reviews by resource ID from the database

        if(externalIdHash == null){
            externalIdHash = FNV1A64HashGenerator.generate(externalId);
        }

        return FSQLQuery.create(getConnection(), "SELECT * FROM reviews WHERE tenant_id = ? AND external_id_hash = ? AND external_id = ?")
            .bind(tenantId, externalIdHash, externalId)
            .fetchAll(Review.class)
            .stream()
            .map(review -> (IReview) review)
            .toList();
    }
/*
    @Override
    public void delete(IReview review) throws Exception {
        // Implementation to delete review from the database

        deleteById(review.getTenantId(),  review.getId());
    }
 */

    @Override
    public void deleteById(Long tenantId, Long reviewId) throws Exception {
        // Implementation to delete review by ID from the database
        // TODO: Implement database delete logic by ID
        // throw new TODOException();

        FSQLQuery.create(getConnection(), "DELETE FROM reviews WHERE tenant_id = ? AND id = ?")
            .bind(tenantId, reviewId)
            .delete();
    }

    @Override
    public void delete(IReview review) throws Exception {
        throw new Exception("Method not implemented yet");
    }

    @Override
    public List<Review> findByAuthorIdAndExternalId(long authorId, String path, QueryOptions queryOptions) throws Exception {
        throw new Exception("Method not implemented yet");
    }

    @Override
    public List<Review> findByAuthorIdAndExternalId(long authorId, String path) throws Exception {
        throw new Exception("Method not implemented yet");
    }

    @Override
    public List<Review> findByScoreAndExternalId(int score, String externalId) throws Exception {
        throw new Exception("Method not implemented yet");
    }

    @Override
    public List<Review> findAll() throws Exception {
        throw new Exception("Method not implemented yet");
    }
}
