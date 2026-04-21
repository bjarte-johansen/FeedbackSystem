package root.services;

import root.models.Tenant;

import java.util.Map;
import java.util.Optional;
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

class TenantResolverCache {
    private static final Map<String, Optional<Tenant>> CACHE = new ConcurrentHashMap<>();

    private TenantResolverCache() {
    }


    /**
     * Retrieves the tenant associated with the given host from the cache.
     *
     * @param host normalized host string for which the tenant should be retrieved.
     * @return
     */

    public static Tenant get(String host) {
        return CACHE.getOrDefault(host, Optional.empty()).orElse(null);
    }


    /**
     * Caches the tenant associated with the given host.
     *
     * @param host normalized host string for which the tenant should be cached.
     * @param tenant the tenant to be cached for the specified host.
     */

    public static void put(String host, Tenant tenant) {
        CACHE.put(host, Optional.ofNullable(tenant));
    }


    /**
     * Retrieves the tenant associated with the given host from the cache if it exists; otherwise, computes it using
     * the
     *
     * @param host
     * @param mappingFunction
     * @return
     */

    public static Optional<Tenant> computeIfAbsent(String host, java.util.function.Function<String, Optional<Tenant>> mappingFunction) {
        return CACHE.computeIfAbsent(host, mappingFunction);
    }


    /**
     * Evicts the cache entry for all hosts
     *
     */

    public static void evictAll() {
        CACHE.clear();
    }


    /**
     * Evicts the cache entry for the specified host.
     *
     * @param host normalized host string for which the cache entry should be removed.
     */

    public static void evict(String host) {
        CACHE.remove(host);
    }
}
