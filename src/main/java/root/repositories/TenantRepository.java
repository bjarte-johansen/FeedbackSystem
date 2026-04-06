package root.repositories;

import root.includes.proxyrepo.ProxyRepository;
import root.models.Review;
import root.models.Tenant;
import java.util.List;


public interface TenantRepository extends ProxyRepository<Tenant, Long> {
}