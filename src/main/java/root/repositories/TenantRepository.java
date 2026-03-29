package root.repositories;

import root.ProxyRepository;
import root.ProxyRepositoryFactory;
import root.models.Review;
import root.models.Tenant;

import java.util.List;


public interface TenantRepository extends ProxyRepository<Tenant, Long> {
    /**
     * WARNING: This method will return all reviews for the tenant, which could be a large amount of data. Use with caution.
     *
     * @param tenantId The ID of the tenant to find reviews for.
     */

    default List<Review> findReviewsByTenantId(long tenantId) throws Exception {
        ReviewRepository reviewRepo = ProxyRepositoryFactory.create(ReviewRepository.class);
        return reviewRepo.findByTenantId(tenantId);
    }
}