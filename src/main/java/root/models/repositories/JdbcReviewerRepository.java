package root.models.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import root.database.DB;
import root.database.FSQL;
import root.database.FSQLQuery;
import root.models.IReviewer;
import root.models.IReviewerRepository;
import root.models.Reviewer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


/**
 * JDBC implementation of the IReviewerRepository (name subject to change) interface.
 * This class provides methods to perform CRUD operations on Reviewer entities using JDBC.
 */

@Service
public class JdbcReviewerRepository implements IReviewerRepository {

    @Override
    public @NotNull IReviewer create(IReviewer reviewer) throws Exception{
        LinkedHashMap<String, Object> fields = FSQL.linkedNameValueMap(
            "tenant_id", reviewer.getTenantId(),
            "email", reviewer.getEmail(),
            "display_name", reviewer.getDisplayName(),
            "password_hash", reviewer.getPasswordHash(),
            "password_salt", reviewer.getPasswordSalt(),
            "created_at", reviewer.getCreatedAt(),
            "verified_at", reviewer.getVerifiedAt()
            );

        FSQLQuery.create(DB.getConnection(), FSQL.createInsertSql(DB.getConnection(), "reviewrs", fields))
            .bind(fields.values())
            .insertAndGetId();

        reviewer.setId(reviewer.getId());

        return reviewer;
    }

    public @NotNull IReviewer update(IReviewer reviewer) throws Exception{
        String query = "UPDATE reviewer SET tenant_id = ?, email = ?, display_name = ?, password_hash = ?, password_salt = ?, created_at = ?, verified_at = ? WHERE id = ?";
        FSQLQuery.create(DB.getConnection(), query)
            .bind(
                reviewer.getTenantId(),
                reviewer.getEmail(),
                reviewer.getDisplayName(),
                reviewer.getPasswordHash(),
                reviewer.getPasswordSalt(),
                reviewer.getCreatedAt(),
                reviewer.getVerifiedAt(),
                reviewer.getId()
            )
            .update();
        return reviewer;
    }

    @Override
    public Optional<IReviewer> findById(long reviewerId) throws Exception {
        return FSQLQuery.create(DB.getConnection(), "SELECT * FROM reviewer WHERE tenant_id = ? AND id = ?")
            .bind(reviewerId)
            .fetchOne(Reviewer.class).map(reviewer -> reviewer);
    }

    @Override
    public Optional<IReviewer> findByReviewerName(long tenantId, String username) throws Exception {
        return FSQLQuery.create(DB.getConnection(), "SELECT * FROM reviewer WHERE tenant_id = ? AND displayName = ?")
            .bind(tenantId, tenantId)
            .bind(username)
            .fetchOne(Reviewer.class)
            .map(reviewer -> reviewer);
    }

    @Override
    public Optional<IReviewer> findByEmail(long tenantId, String email) throws Exception {
        return FSQLQuery.create(DB.getConnection(), "SELECT * FROM reviewer WHERE tenant_id = ? AND email = ?")
            .bind(tenantId, email)
            .fetchOne(Reviewer.class)
            .map(reviewer -> reviewer);
    }

    @Override
    public int delete(IReviewer reviewer) throws Exception {
        return deleteById(reviewer.getTenantId(), reviewer.getId());
    }

    @Override
    public int deleteById(long tenantId, long reviewerId) throws Exception {
        return FSQLQuery.create(DB.getConnection(), "DELETE FROM reviewer WHERE tenant_id = ? AND id = ?")
            .bind(tenantId)
            .bind(reviewerId)
            .delete();
    }
}