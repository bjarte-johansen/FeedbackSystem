package root.interfaces;

import root.ProxyRepository;
import root.models.IReview;
import root.models.Tenant;

import java.util.List;

/**
 * Tenant Repository Interface
 */
@Deprecated
public interface TenantRepos extends ProxyRepository<Tenant, Integer> {
    /**
     * WARNING: This method will return all reviews for the tenant, which could be a large amount of data. Use with caution.
     *
     * @param tenantId The ID of the tenant to find reviews for.
     */
    List<IReview> findReviewsByTenantId(long tenantId) throws Exception;

}
