package root.repositories;

import root.models.Tenant;

import java.util.Optional;

interface TenantRepositoryCustom {
    Optional<Tenant> findSafeCacheableById(Long id);
}
