package root.database.connectionproviders;

import root.database.connectionproviders.CustomConnectionProvider;
import root.database.CustomDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class CustomMultiTenantConnectionProvider extends CustomConnectionProvider {
    public static String DEFAULT_TENANT_IDENTIFIER = "public";

    public CustomMultiTenantConnectionProvider(CustomDataSource ds) {
        super(ds);
    }

    public String resolveTenantSchemaIdentifier(){
        throw new UnsupportedOperationException("resolveTenantSchemaIdentifier() must be implemented by subclass to determine tenant schema based on context");
    };

    @Override
    public void releaseConnection(Connection conn) throws SQLException, Exception {
        conn.setSchema(DEFAULT_TENANT_IDENTIFIER);
        ds.releaseConnection(conn);
    }

    @Override
    public Connection getConnection() throws SQLException, Exception {
        Connection conn = super.getConnection();

        String schema = resolveTenantSchemaIdentifier();
        conn.setSchema(schema);

        return conn;
    }
}
