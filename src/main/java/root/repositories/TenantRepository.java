package root.repositories;

import root.ProxyRepository;
import root.RepositoryProxyConstructor;
import root.interfaces.ITenant;
import root.interfaces.TenantRepos;
import root.models.IReview;
import root.models.Review;
import root.models.Tenant;

import java.util.List;
import java.util.Optional;


public interface TenantRepository extends ProxyRepository<Tenant, Long> {
    public List<Review> findReviews() throws Exception;
}