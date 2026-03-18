package root.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class TenantRepository {
    static long insert_tenant(Connection conn, long tenant_id, String name) throws Exception {
        return FSQLQuery.create(conn, "INSERT INTO tenant (id, name) VALUES (?, ?)")
            .bind(tenant_id, name)
            .insertAndGetId();
    }

    static int delete_tenant(Connection conn, long tenant_id) throws Exception {
        return FSQLQuery.create(conn, "DELETE FROM tenant WHERE (id = ?)")
            .bind(tenant_id)
            .delete();
    }
}
