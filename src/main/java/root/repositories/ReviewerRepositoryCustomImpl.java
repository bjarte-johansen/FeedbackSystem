package root.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import root.AppConfig;
import root.ProxyRepository;
import root.database.FSQL;
import root.database.FSQLQuery;
import root.database.SqlFactory;
import root.models.IReviewer;
import root.interfaces.IReviewerRepository;
import root.models.Reviewer;

import java.util.LinkedHashMap;
import java.util.Optional;


@Repository
public class ReviewerRepositoryCustomImpl {
    /*
    public Optional<IReviewer> findByReviewerName(long tenantId, String username) throws Exception {
        return FSQLQuery.create("SELECT * FROM " + AppConfig.REVIEWER_TABLE_NAME + " WHERE tenant_id = ? AND displayName = ?")
            .bind(tenantId, tenantId)
            .bind(username)
            .fetchOne(Reviewer.class)
            .map(reviewer -> reviewer);
    }
*/
/*
    public Optional<IReviewer> findByEmail(long tenantId, String email) throws Exception {
        return FSQLQuery.create("SELECT * FROM " + AppConfig.REVIEWER_TABLE_NAME + " WHERE tenant_id = ? AND email = ?")
            .bind(tenantId, email)
            .fetchOne(Reviewer.class)
            .map(reviewer -> reviewer);
    }

    public int deleteById(long tenantId, long reviewerId) throws Exception {
        return FSQLQuery.create("DELETE FROM " + AppConfig.REVIEWER_TABLE_NAME + " WHERE tenant_id = ? AND id = ?")
            .bind(tenantId)
            .bind(reviewerId)
            .delete();
    }
 */
}