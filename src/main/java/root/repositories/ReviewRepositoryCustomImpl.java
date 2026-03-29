package root.repositories;

import org.springframework.stereotype.Repository;
import root.AppConfig;
import root.database.*;
import root.logger.Logger;
import root.models.FNV1A64HashGenerator;
import root.models.IReview;
import root.models.QueryOptions;
import root.models.Review;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

@Repository
public class ReviewRepositoryCustomImpl {
	public Optional<Review> findById(long tenantId, long reviewId) throws Exception {
        return FSQLQuery.create("SELECT * FROM " + AppConfig.REVIEW_TABLE_NAME + " WHERE (id = ? AND tenant_id = ?)")
            .bind(reviewId, tenantId)
            .fetchOne(Review.class);
    }

	public List<IReview> findByExternalId(long tenantId, String externalId, Long externalIdHash, QueryOptions queryOptions) throws Exception {
        // Implementation to find reviews by resource ID from the database

        long hashToUse = (externalIdHash == null) ? FNV1A64HashGenerator.generate(externalId) : externalIdHash;

        return FSQLQuery.create("SELECT * FROM " + AppConfig.REVIEW_TABLE_NAME + " WHERE tenant_id = ? AND external_id_hash = ? AND external_id = ?")
            .bind(tenantId, hashToUse, externalId)
            .fetchAll(Review.class)
            .stream()
            .map(review -> (IReview) review)
            .toList();
    }

	public void deleteById(Long tenantId, Long reviewId) throws Exception {
        // Implementation to delete review by ID from the database

        DB.with(conn -> {
            String sql = SqlFactory.createDeleteSql(AppConfig.REVIEW_TABLE_NAME, FSQL.makeArr("tenant_id =", "?", "id =", "?"));

            FSQLQuery.create(conn, sql)
                .bind(tenantId, reviewId)
                .delete();

            return null;
        });
    }

	public List<Review> findByAuthorIdAndExternalId(long authorId, String path, QueryOptions queryOptions) throws Exception {
        throw new Exception("Method not implemented yet");
    }

	public List<Review> findByAuthorIdAndExternalId(long authorId, String path) throws Exception {
        throw new Exception("Method not implemented yet");
    }

	public List<Review> findByScoreAndExternalId(int score, String externalId) throws Exception {
        throw new Exception("Method not implemented yet");
    }

	public long countByExternalId(long tenantId, String externalId, Long externalIdHash) throws Exception {
        return DB.with(conn -> {
            long hashToUse = (externalIdHash == null) ? FNV1A64HashGenerator.generate(externalId) : externalIdHash;

            return FSQLQuery.create(conn, SqlFactory.createCountSql(AppConfig.REVIEW_TABLE_NAME, new Object[]{"tenant_id =", "?", "external_id_hash = ", "?", "external_id = ", "?"}))
                .bind(tenantId, hashToUse, externalId)
                .selectCount();
        });
    }

	public List<Review> findByTenantId(long tenantId) throws Exception {
        return DB.with(conn -> {
            return FSQLQuery.create(conn, "SELECT * FROM " + AppConfig.REVIEW_TABLE_NAME + " WHERE tenant_id = ?")
                .bind(tenantId)
                .fetchAll(Review.class);
        });
    }
}