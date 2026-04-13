package root.app;


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

public class AppRequestSchema {
    @FunctionalInterface
    public interface NoThrowCloseable extends AutoCloseable {
        @Override
        void close(); // no throws
    }

    private static final ThreadLocal<String> TENANT_SCHEMA = new ThreadLocal<String>();

    public static String get() {
        return TENANT_SCHEMA.get();
    }

    public static void set(String name){
        if(name != null && !name.isEmpty())
            SqlSchemaNameValidator.validateSchemaName(name);
        TENANT_SCHEMA.set(name);
    }

    public static void remove(){
        TENANT_SCHEMA.remove();
    }

    public static NoThrowCloseable withThreadSchema(String name) {
        try {
            SqlSchemaNameValidator.validateSchemaName(name);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        String previous  = TENANT_SCHEMA.get();
        TENANT_SCHEMA.set(name);

        return () -> {
            try {
                if (previous == null) {
                    TENANT_SCHEMA.remove();
                } else {
                    TENANT_SCHEMA.set(previous);
                }
            }catch(RuntimeException e){
                // log error but don't rethrow, to avoid masking original exception
                System.err.println("Error restoring previous tenant schema: " + e.getMessage());
            }
        };
    }
}

