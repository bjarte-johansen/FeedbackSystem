package root.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import root.database.FSQLQuery;
import root.includes.logger.Logger;
import root.models.NonPersistableTenant;
import root.models.Review;
import root.models.Tenant;

import java.util.List;
import java.util.Optional;


@Repository
public class TenantRepositoryCustomImpl implements TenantRepositoryInterface{
    /**
     * Get a tenant, mask its password hash and password salts by setting them to NULL. Be careful
     * with this result. It shall not be written to under any circumstances.
     *
     * @param id
     * @return
     */
    public Optional<Tenant> findSafeCacheableById(Long id){
        Tenant t = FSQLQuery.create("SELECT * FROM public.tenant WHERE id = ?")
            .bind(id)
            .fetchOne(Tenant.class)
            .orElse(null);
        if(t == null) return Optional.empty();

        t.setPasswordSalt("");
        t.setPasswordHash("");

        return Optional.of(t);
    }

    private Tenant findByHostExact(String host){
        String sql = "SELECT t.* FROM public.tenant_domain" +
            " d JOIN public.tenant t ON t.id = d.tenant_id" +
            " WHERE d.domain = ? LIMIT 1;";

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
