package root.repositories;

import org.springframework.stereotype.Service;
import root.ProxyRepository;
import root.database.FSQL;
import root.database.FSQLQuery;
import root.database.SqlFactory;
import root.models.IReviewer;
import root.interfaces.IReviewerRepository;
import root.models.Reviewer;

import java.util.LinkedHashMap;
import java.util.Optional;


@Service
public class ReviewerRepositoryCustomImpl implements IReviewerRepository {

    @Override
    public IReviewer create(IReviewer reviewer) throws Exception{
        LinkedHashMap<String, Object> fields = FSQL.linkedNameValueMap(
            "tenant_id", reviewer.getTenantId(),
            "email", reviewer.getEmail(),
            "display_name", reviewer.getDisplayName(),
            "password_hash", reviewer.getPasswordHash(),
            "password_salt", reviewer.getPasswordSalt()
            //"created_at", reviewer.getCreatedAt(),
            //"verified_at", reviewer.getVerifiedAt()
            );

        String sql = SqlFactory.createInsertSql("reviewers", fields);

        FSQLQuery.create(sql)
            .bindArray(fields.values().toArray())
            .insertAndGetId();

        reviewer.setId(reviewer.getId());

        return reviewer;
    }

    public IReviewer update(IReviewer reviewer) throws Exception{
        // TODO: check that all fields are present and valid
        //  if database changes it must be reflected here

        //try(var conn = DB.getConnection()) {
        String sql = "UPDATE reviewers SET tenant_id = ?, email = ?, display_name = ?, password_hash = ?, password_salt = ?, created_at = ?, verified_at = ? WHERE id = ?";

        FSQLQuery.create(sql)
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
        return FSQLQuery.create("SELECT * FROM reviewers WHERE tenant_id = ? AND id = ?")
            .bind(reviewerId)
            .fetchOne(Reviewer.class).map(reviewer -> reviewer);
    }

    @Override
    public Optional<IReviewer> findByReviewerName(long tenantId, String username) throws Exception {
        return FSQLQuery.create("SELECT * FROM reviewers WHERE tenant_id = ? AND displayName = ?")
            .bind(tenantId, tenantId)
            .bind(username)
            .fetchOne(Reviewer.class)
            .map(reviewer -> reviewer);
    }

    @Override
    public Optional<IReviewer> findByEmail(long tenantId, String email) throws Exception {
        return FSQLQuery.create("SELECT * FROM reviewers WHERE tenant_id = ? AND email = ?")
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
        return FSQLQuery.create("DELETE FROM reviewers WHERE tenant_id = ? AND id = ?")
            .bind(tenantId)
            .bind(reviewerId)
            .delete();
    }
}