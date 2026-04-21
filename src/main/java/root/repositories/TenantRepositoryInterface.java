package root.repositories;

import root.models.Tenant;

import java.util.Optional;

interface TenantRepositoryInterface {
    Optional<Tenant> findSafeCacheableById(Long id);
}
