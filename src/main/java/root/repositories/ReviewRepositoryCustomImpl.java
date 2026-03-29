package root.repositories;

import org.springframework.stereotype.Repository;
import root.AppConfig;
import root.database.*;
import root.models.FNV1A64HashGenerator;
import root.models.QueryOptions;
import root.models.Review;

import java.util.*;

@Repository
public class ReviewRepositoryCustomImpl {
	public List<Review> findByExternalIdHashAndExternalId(Long externalIdHash, String externalId) throws Exception {
        // Implementation to find reviews by resource ID from the database

        long hashToUse = (externalIdHash == null) ? FNV1A64HashGenerator.generate(externalId) : externalIdHash;

        return FSQLQuery.create("SELECT * FROM " + AppConfig.REVIEW_TABLE_NAME + " WHERE external_id_hash = ? AND external_id = ?")
            .bind(hashToUse, externalId)
            .fetchAll(Review.class);
    }

	public List<Review> findByAuthorIdAndExternalId(long authorId, String path) throws Exception {
        throw new Exception("Method not implemented yet");
    }

	public List<Review> findByScoreAndExternalId(int score, String externalId) throws Exception {
        throw new Exception("Method not implemented yet");
    }

	public long countByExternalIdHashAndExternalId(Long externalIdHash, String externalId) throws Exception {
        return DB.with(conn -> {
            long hashToUse = (externalIdHash == null) ? FNV1A64HashGenerator.generate(externalId) : externalIdHash;

            return FSQLQuery.create(conn, SqlFactory.createCountSql(AppConfig.REVIEW_TABLE_NAME, new Object[]{"tenant_id =", "?", "external_id_hash = ", "?", "external_id = ", "?"}))
                .bind(hashToUse, externalId)
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