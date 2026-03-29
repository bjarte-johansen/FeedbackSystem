package root.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import root.RepositoryProxyConstructor;
import root.database.DB;
import root.database.GenericEntityPersistence;
import root.models.Review;
import root.models.Tenant;

import java.util.List;


@Repository
public class TenantRepositoryCustomImpl {
    @Autowired
    ReviewRepository reviewRepo;

    /**
     * WARNING: This method will return all reviews for the tenant, which could be a large amount of data. Use with caution.
     *
     * @param tenantId The ID of the tenant to find reviews for.
     */

    public List<Review> findReviewsByTenantId(long tenantId) throws Exception {
        return reviewRepo.findByTenantId(tenantId);
    }
}
