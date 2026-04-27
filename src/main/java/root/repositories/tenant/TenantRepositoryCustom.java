package root.repositories.tenant;

import root.models.tenant.Tenant;

import java.util.Optional;

interface TenantRepositoryCustom {
    Optional<Tenant> findSafeCacheableById(Long id);
}
