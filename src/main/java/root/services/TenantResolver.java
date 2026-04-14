package root.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import root.includes.logger.Logger;
import root.models.Tenant;
import root.repositories.TenantRepository;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static root.common.utils.Preconditions.checkArgument;

interface EvictableCache<K, V> {
    Tenant get(K k);
    void put(K k, V v);
    Optional<Tenant> computeIfAbsent(K k, Function<K, V> mappingFunction);

    void evictAll();
    void evict(K k);
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
 *
 * Uses cahee of hosts if possible, TODO: it doesnt get invalidated automaticly by removal of tenants/hosts
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