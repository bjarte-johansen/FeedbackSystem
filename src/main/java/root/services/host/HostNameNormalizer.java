package root.services.host;

/**
 * HostNameNormalizer is a utility class responsible for normalizing host strings extracted from HTTP requests. The
 * normalization process includes converting the host to lowercase, trimming leading and trailing whitespace, removing
 * any port numbers (e.g., ":8080"), and stripping trailing dots. This ensures that host strings are in a consistent
 * format for tenant resolution, allowing for accurate matching against tenant host configurations in the database. The
 * normalize method is static, making it easy to use throughout the TenantResolver class without needing to instantiate
 * HostNameNormalizer. By standardizing host strings, this class helps prevent issues related to case sensitivity,
 * extraneous whitespace, and port numbers that could interfere with tenant resolution logic.
 */

public class HostNameNormalizer {
    // normalize host by converting to lowercase, trimming whitespace, and removing port numbers and trailing dots
    public static String normalize(String host) {
        if (host == null) return "";
        host = host.toLowerCase().trim();

        int protocolIndex = host.indexOf("://");
        if(protocolIndex != -1){
            host = host.substring(protocolIndex + 3);
        }

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
