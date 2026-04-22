package root.services;

import jakarta.servlet.http.HttpServletRequest;

import static com.google.common.base.Preconditions.checkArgument;

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
 * Uses cahee of hosts if possible, TODO: it doesnt get invalidated automaticly by removal of tenants/hosts
 * <p>
 * TODO: Consider adding caching for tenant resolution results to improve performance, especially if the same hosts are
 *  frequently accessed.
 */


public class HostNameResolver {
    // get host from request
    public static String getHost(HttpServletRequest req) {
        checkArgument(req != null, "Request cannot be null");
        String h;

        h = req.getHeader("X-Forwarded-Host");
        if (h != null) return h.split(",")[0].trim();

        h = req.getHeader("Host");
        if (h != null) return h.split(",")[0].trim();

        return req.getServerName();
    }

    public static String getNormalizedHost(HttpServletRequest req) {
        return HostNameNormalizer.normalize(getHost(req));
    }
}
