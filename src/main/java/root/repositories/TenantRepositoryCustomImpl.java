package root.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import root.database.FSQLQuery;
import root.includes.logger.Logger;
import root.models.Review;
import root.models.Tenant;

import java.util.List;


@Repository
public class TenantRepositoryCustomImpl {
    private Tenant findByHostExact(String host){
        String sql = "SELECT t.* FROM public.tenant_domain d JOIN public.tenant t ON t.id = d.tenant_id WHERE d.domain = ? LIMIT 1;";

        return FSQLQuery.create(sql)
            .bind(host)
            .fetchOne(Tenant.class)
            .orElse(null);
    }

    // todo, move this to tenant repo
    private Tenant findByHostWildcard(String host){
        // in this implementation, we only support wildcard at the start of the domain, e.g. *.example.com,
        // but not example.* or exa*mple.com, we use the same sql as findByHostExact, but with a wildcard domain value
        return findByHostExact(host);
    }
}
