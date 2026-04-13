package root.repositories;

import root.includes.proxyrepo.ProxyRepository;
import root.models.Tenant;
import java.util.Optional;


public interface TenantRepository extends ProxyRepository<Tenant, Long> {
    Optional<Tenant> findByEmail(String email);

    Tenant findByHostExact(String host);
    Tenant findByHostWildcard(String host);
}