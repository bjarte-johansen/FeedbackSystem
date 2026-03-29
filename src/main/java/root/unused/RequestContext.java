package root.unused;


public class RequestContext {
    private final long tenantId;
    private final String tenantDomain;

    public RequestContext(String tenantDomain, long tenantId) {
        this.tenantId = tenantId;
        this.tenantDomain = tenantDomain;
    }

    public long getTenantId(){
        return tenantId;
    }

}
