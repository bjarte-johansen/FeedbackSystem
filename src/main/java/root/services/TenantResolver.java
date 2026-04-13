package root.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import root.app.AppConfig;
import root.app.AppContext;
import root.database.DataSourceManager;
import root.includes.logger.Logger;
import root.models.Tenant;
import root.repositories.TenantRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static root.common.utils.Preconditions.checkArgument;


/**
 * HostNameNormalizer is a utility class responsible for normalizing host strings extracted from HTTP requests. The
 * normalization process includes converting the host to lowercase, trimming leading and trailing whitespace, removing
 * any port numbers (e.g., ":8080"), and stripping trailing dots. This ensures that host strings are in a consistent
 * format for tenant resolution, allowing for accurate matching against tenant host configurations in the database. The
 * normalize method is static, making it easy to use throughout the TenantResolver class without needing to instantiate
 * HostNameNormalizer. By standardizing host strings, this class helps prevent issues related to case sensitivity,
 * extraneous whitespace, and port numbers that could interfere with tenant resolution logic.
 */

class HostNameNormalizer {
    // normalize host by converting to lowercase, trimming whitespace, and removing port numbers and trailing dots
    public static String normalize(String host) {
        if (host == null) return "";
        host = host.toLowerCase().trim();

        int colonIndex = host.indexOf(":");
        if (colonIndex != -1) {
            host = host.substring(0, colonIndex);
        }

        if (host.endsWith(".")) {
            host = host.substring(0, host.length() - 1);
        }

        return host;
    }
}


/**
 * TenantResolverCache is a simple in-memory cache for storing tenant resolution results based on host strings. It uses a
 * ConcurrentHashMap to allow for thread-safe access and modifications. The cache stores mappings from normalized host
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


/**
 * TenantResolver is responsible for determining the tenant based on the incoming HTTP request's host header. It
 * supports both exact host matches and wildcard subdomain matches. The resolution process involves: 1. Extracting the
 * host from the request, prioritizing the "X-Forwarded-Host" header, then the "Host" header, and finally falling back
 * to the server name. 2. Normalizing the host by converting it to lowercase, trimming whitespace, and removing any port
 * numbers or trailing dots. 3. Attempting to find a tenant with an exact host match in the TenantRepository. 4. If no
 * exact match is found, it iteratively checks for wildcard matches by replacing subdomains with a wildcard character
 * (*). 5. If a tenant is found at any step, it is returned; otherwise, null is returned. This class is designed to be
 * used as a Spring component and can be injected into controllers or other services that require tenant resolution
 * based on the request's host.
 * <p>
 * TODO: Consider adding caching for tenant resolution results to improve performance, especially if the same hosts are
 *  frequently accessed.
 */

@Component
public class TenantResolver {
    private final TenantRepository tenantRepo;


    /**
     * Constructor for TenantResolver. It takes a TenantRepository as a parameter, which is used to look up tenants
     * based on host information. The TenantRepository is typically a Spring Data repository that provides methods for
     * querying tenant data from the database.
     *
     * @param tenantRepo
     */

    public TenantResolver(TenantRepository tenantRepo) {
        this.tenantRepo = tenantRepo;
    }


    /**
     * Resolves the tenant based on the provided host string. It first normalizes the host and then checks the cache for
     * a previously resolved tenant. If not found in the cache, it attempts to find the tenant using the findByHost
     * method, which checks for both exact and wildcard matches. The result is cached for future lookups to improve
     * performance on subsequent requests with the same host.
     *
     * @param host normalized or non-normalized host string extracted from the HTTP request, which may include port
     * numbers or be in mixed case.
     * @return
     */

    public Tenant resolve(String host) {
        host = HostNameNormalizer.normalize(host);
        checkArgument(!host.isBlank(), "Host is missing or blank");

        return TenantResolverCache
            .computeIfAbsent(host, h -> { Logger.log("recomputed tenant"); return Optional.ofNullable(findByHost(h)); })
            .orElse(null);
    }


    /**
     * Resolves the tenant based on the host information in the HTTP request. It first normalizes the host and then
     * checks the cache for a previously resolved tenant. If not found in the cache, it attempts to find the tenant
     * using the findByHost method, which checks for both exact and wildcard matches
     *
     * @param req
     * @return
     */

    public Tenant resolve(HttpServletRequest req) {
        return resolve(getHost(req));
    }


    // get host from request
    private String getHost(HttpServletRequest req) {
        String h;

        h = req.getHeader("X-Forwarded-Host");
        if (h != null) return h.split(",")[0].trim();

        h = req.getHeader("Host");
        if (h != null) return h.split(",")[0].trim();

        return req.getServerName();
    }


    // expects normalized host
    private Tenant findByHost(String host) {
//  Logger.log("Resolving tenant for host: \"" + host + "\"");
//  if(true) return tenantRepo.findByHostExact(host);

        Tenant t = tenantRepo.findByHostExact(host);
        if (t != null) return t;

        String[] parts = host.split("\\.");
        for (int i = 1; i < parts.length - 1; i++) {
            String wildcard = "*." + String.join(".", Arrays.copyOfRange(parts, i, parts.length));

            t = tenantRepo.findByHostWildcard(wildcard);
            if (t != null) return t;
        }

        return null;
    }
}