package root.app;


import root.App;
import root.includes.logger.Logger;
import root.includes.utils.SqlSchemaNameValidator;

/**
 * AppRequestContext is a context class for request contact schema that is resolver by host/domain
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
                Logger.log(e.getMessage());
                System.err.println("Error restoring previous tenant schema: " + e.getMessage());
                throw new RuntimeException("Error restoring previous tenant schema", e);
            }
        };
    }

    public static void withThreadSchema(String name, App.ThrowingRunnable runnable) {
        try(var ignore = withThreadSchema(name)) {
            try {
                runnable.run();
            }catch(Exception e) {
                Logger.log(e.getMessage());
                throw new RuntimeException("Error restoring previous tenant schema", e);
            }
        }
    }
}

