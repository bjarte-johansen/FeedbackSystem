package root.repositories;

import root.database.DB;
import root.database.GenericEntityPersistence;
import root.models.Tenant;

@Deprecated
public class TenantRepositoryCustomImpl {
    public Tenant create(Tenant tenant) throws Exception {
        return DB.with(conn -> {
            return GenericEntityPersistence.genericInsertAndUpdateId(conn, "tenants", tenant, "id");
        });
    }
    public void delete(Tenant tenant) throws Exception {
        DB.with(conn -> {
            GenericEntityPersistence.genericDelete(conn, "tenants", tenant, "id");

            return null;
        });
    }
}
