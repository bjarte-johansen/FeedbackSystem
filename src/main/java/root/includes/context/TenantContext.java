package root.includes.context;


import root.models.tenant.Tenant;

/**
 * class to hold tenant for request in memory
 *
 * TODO: it should be non-persistable and have password and salt removed, as well as other risks mitigated
 */

public class TenantContext {
    private static final ThreadLocal<Tenant> TL = new ThreadLocal<>();

    /** Get the tenant for the current thread. */
    public static Tenant get() { return TL.get(); }

    /** Set the tenant for the current thread */
    public static void set(Tenant t) { TL.set(t); }

    /** Remove the tenant for the current thread */
    public static void remove() { TL.remove(); }
}
