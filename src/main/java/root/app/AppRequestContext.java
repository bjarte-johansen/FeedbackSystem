package root.app;

import java.util.regex.Pattern;

/**
 * AppRequestContext is a context class for holding request-specific information such as the database connection,
 * tenant ID, and tenant schema. It uses ThreadLocal to ensure that each thread has its own instance of these
 * variables, allowing for thread-safe access to request-specific data.
 *
 * In reality, used to hold schema/tenant/possibly conection info for the current request. Subject to change.
 *
 * schema = database schema, which is used for multi-tenancy. Each tenant has its own schema in the database,
 * and the application sets the search_path to the appropriate schema for each request (or connection).
 */

public class AppRequestContext {
    private static final Pattern VALID_SCHEMA_NAME_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
    public static ThreadLocal<String> TENANT_SCHEMA = new ThreadLocal<String>();

    public static void validateTenantSchemaName(String schemaName) {
        if (!VALID_SCHEMA_NAME_PATTERN.matcher(schemaName).matches())
            throw new IllegalArgumentException("Invalid schema name: " + schemaName);
    }

    public static void setTenantSchemaForThread(String schemaName){
        validateTenantSchemaName(schemaName);
        TENANT_SCHEMA.set(schemaName);
    }

    public static void clearTenantSchemaForThread(){
        TENANT_SCHEMA.remove();
    }

    public static AutoCloseable withTenantSchemaForThread(String tenantSchema) {
        validateTenantSchemaName(tenantSchema);

        String previousSchema = TENANT_SCHEMA.get();
        TENANT_SCHEMA.set(tenantSchema);

        return () -> {
            if (previousSchema == null) {
                TENANT_SCHEMA.remove();
            } else {
                TENANT_SCHEMA.set(previousSchema);
            }
        };
    }
}

