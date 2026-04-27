package root.repositories.tenant;

import root.includes.proxyrepo.ProxyRepository;
import root.models.tenant.TenantDomain;

import java.util.List;
import java.util.Optional;

public interface TenantDomainRepository extends ProxyRepository<TenantDomain, Long> {
    Optional<TenantDomain> findByDomain(String domain);
    List<TenantDomain> findByTenantId(long tenantId);
}