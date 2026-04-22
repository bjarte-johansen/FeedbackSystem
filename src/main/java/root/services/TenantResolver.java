package root.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import root.models.Tenant;
import root.repositories.TenantRepository;

import java.util.Arrays;

import static com.google.common.base.Preconditions.*;


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

        Tenant t = TenantResolverCache.get(host);
        if(t != null) return t;

        t = findByHost(host);
        if(t != null) TenantResolverCache.put(host, t);

        return t;
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
        String host = HostNameResolver.getHost(req);
        return resolve(host);
    }





    // expects normalized host
    private Tenant findByHost(String host) {
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