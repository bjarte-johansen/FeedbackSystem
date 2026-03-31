package root.unused;

@Deprecated
public class RequestContext {
    public final long tenantId;
    public final String tenantDomain;


    public RequestContext(String tenantDomain, long tenantId) {
        this.tenantId = tenantId;
        this.tenantDomain = tenantDomain;
    }

    public long getTenantId(){
        return tenantId;
    }

}
