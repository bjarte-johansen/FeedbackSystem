package root.repositories;

import root.includes.proxyrepo.ProxyRepository;
import root.models.TenantDomain;

import java.util.Optional;

public interface TenantDomainRepository extends ProxyRepository<TenantDomain, Long> {
    Optional<TenantDomain> findByDomain(String domain);
}