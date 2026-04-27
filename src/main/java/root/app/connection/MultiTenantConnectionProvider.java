package root.app.connection;

import root.includes.context.SchemaContext;
import root.database.CustomDataSource;

public class MultiTenantConnectionProvider extends root.database.connectionproviders.CustomMultiTenantConnectionProvider {
    public MultiTenantConnectionProvider(CustomDataSource ds) {
        super(ds);
    }

    @Override
    public String resolveTenantSchemaIdentifier() {
        String tenant_schema = SchemaContext.get();

        if (tenant_schema == null || tenant_schema.isBlank())
            throw new RuntimeException("Tenant schema not set in AppRequestContext, unable to resolve tenant connection");

        return tenant_schema;
    }
}
