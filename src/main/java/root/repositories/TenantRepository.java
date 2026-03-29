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
    ITenant create(ITenant tenant) throws Exception;

    void update(ITenant tenant) throws Exception;

    List<Tenant> findAll();

    Optional<ITenant> findById(long tenantId) throws Exception;

    List<IReview> findReviewsByTenantId(long tenantId);

    void deleteById(long tenantId);

    void deleteAll();
}