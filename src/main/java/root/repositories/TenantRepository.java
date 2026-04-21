package root.repositories;

import root.includes.proxyrepo.ProxyRepository;
import root.models.Tenant;
import java.util.Optional;

public interface TenantRepository extends ProxyRepository<Tenant, Long> {
    Optional<Tenant> findById(Long id);
    Optional<Tenant> findSafeCacheableById(Long id);

    Optional<Tenant> findCacheableByName(String name);
    Optional<Tenant> findByEmail(String email);

    Tenant findByHostExact(String host);
    Tenant findByHostWildcard(String host);
}