package root.services.tenant;

import root.models.tenant.Tenant;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory cache for storing tenant resolution results based on host strings. It uses
 * a ConcurrentHashMap to allow for thread-safe access and modifications. The cache stores mappings from normalized host
 * strings to Optional<Tenant> objects, allowing it to represent both the presence and absence of a tenant for a given
 * host. The class provides methods to retrieve tenants from the cache, add new entries, compute values if absent, and
 * evict entries either for specific hosts or entirely. This caching mechanism can significantly improve performance by
 * avoiding repeated database lookups for the same host, especially in scenarios where the same hosts are frequently
 * accessed.
 */

public class TenantResolverCache {
    private static final Map<String, Tenant> BY_HOST = new ConcurrentHashMap<>();
    private static final Map<Long, Set<String>> HOSTS_BY_TENANT = new ConcurrentHashMap<>();

    private TenantResolverCache() {
    }


    /**
     * Retrieves the tenant associated with the given host from the cache.
     *
     * @param host normalized host string for which the tenant should be retrieved.
     * @return
     */

    public static Tenant get(String host) {
        return BY_HOST.getOrDefault(host, null);
    }


    /**
     * Caches the tenant associated with the given host.
     *
     * @param host normalized host string for which the tenant should be cached.
     * @param tenant the tenant to be cached for the specified host.
     */

    public static void put(String host, Tenant tenant) {
        if(tenant == null) return;

        BY_HOST.put(host, tenant);
        HOSTS_BY_TENANT
            .computeIfAbsent(tenant.getId(), k -> ConcurrentHashMap.newKeySet())
            .add(host);
    }


    /**
     * Retrieves the tenant associated with the given host from the cache if it exists; otherwise, computes it using
     * the
     *
     * @param host
     * @param mappingFunction
     * @return
     */

    public static Optional<Tenant> computeIfAbsent(String host, java.util.function.Function<String, Tenant> mappingFunction) {
        return Optional.ofNullable(BY_HOST.computeIfAbsent(host, mappingFunction));
    }


    /**
     * Evicts the cache entry for all hosts
     *
     */

    public static void evictAll() {
        BY_HOST.clear();
    }


    /**
     * Evicts the cache entry for the specified host.
     *
     * @param host normalized host string for which the cache entry should be removed.
     */

    public static void evict(String host) {
        Tenant tenant = BY_HOST.get(host);
        BY_HOST.remove(host);
        if(tenant == null) return;

        HOSTS_BY_TENANT.remove(tenant.getId());
    }

    public static void evict(long tenantId){
        Set<String> tenants = HOSTS_BY_TENANT.get(tenantId);
        tenants.forEach(TenantResolverCache::evict);
        //HOSTS_BY_TENANT.remove(tenantId);
    }
}
