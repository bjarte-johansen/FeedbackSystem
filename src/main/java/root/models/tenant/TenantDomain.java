package root.models.tenant;

import jakarta.persistence.Table;
import root.interfaces.HasId;


/**
 * Represents a tenant domain in the system. Used for host/domain to tenant mapping.
 */

@Table(name = "tenant_domain")
public class TenantDomain implements HasId {
    private Long id;
    private String domain;
    private long tenantId;

    public TenantDomain() {}

    public TenantDomain(Long id, String domain, long tenantId) {
        this.id = id;
        this.domain = domain;
        this.tenantId = tenantId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getTenantId() {
        return tenantId;
    }
    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }

    public String toString() {
        return "TenantDomain{" +
                "id=" + id +
                ", domain='" + domain + '\'' +
                ", tenantId=" + tenantId +
                '}';
    }
}
