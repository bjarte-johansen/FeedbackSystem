package root.app;

import root.database.CustomDataSource;

public class AppMultiTenantConnectionProvider extends root.database.connectionproviders.CustomMultiTenantConnectionProvider {
    public AppMultiTenantConnectionProvider(CustomDataSource ds) {
        super(ds);
    }

    @Override
    public String resolveTenantSchemaIdentifier() {
        String tenant_schema = AppRequestSchema.get();

        if (tenant_schema == null || tenant_schema.isBlank())
            throw new RuntimeException("Tenant schema not set in AppRequestContext, unable to resolve tenant connection");

        return tenant_schema;
    }
}
