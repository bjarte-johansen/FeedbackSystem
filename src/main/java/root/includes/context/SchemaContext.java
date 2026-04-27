package root.includes.context;


import root.includes.SqlSchemaNameValidator;
import root.includes.Functional;

import java.util.Objects;

/**
 * AppRequestContext is a context class for request contact schema that is resolver by host/domain
 * schema = database schema, which is used for multi-tenancy. Each tenant has its own schema in the database,
 * and the application sets the search_path to the appropriate schema for each request (or connection).
 *
 * Multi tenant connection provider will query this to get the right schema to use
 */

public class SchemaContext {
    @FunctionalInterface
    public interface NoThrowCloseable extends AutoCloseable {
        @Override
        void close(); // no throws
    }

    private static final ThreadLocal<String> TENANT_SCHEMA = new ThreadLocal<String>();


    /**
     * Get schema to use for current thread/request. This is used to set the search_path for the database connection.
     * @return
     */

    public static String get() {
        return TENANT_SCHEMA.get();
    }


    /**
     * Set schema to be used for current thread/request. This is used to set the search_path for the database connection.
     *
     * @param name
     */

    public static void set(String name){
        SqlSchemaNameValidator.validateSchemaName(name);
        TENANT_SCHEMA.set(name);
    }


    /**
     * Remove schema for current thread/request
     */

    public static void remove(){
        TENANT_SCHEMA.remove();
    }


    /**
     * Sets schema name and returns an AutoClosable which restores schema on block-exit. Used to reduce amounts
     * of errors that can occur with manual set/restore patterns.
     *
     * @param name Schema name to set
     */

    public static NoThrowCloseable scopeSchema(String name) {
        String previous  = get();
        if (Objects.equals(previous, name)) return () -> {};

        set(name);
        return () -> {
            if (previous == null) remove();
            else set(previous);
        };
    }


    /**
     * Sets schema name and runs a ThrowingRunnable and restores schema on block-exit. Used to reduce amounts
     * of errors that can occur with manual set/restore patterns.
     *
     * @param name Schema name to set
     * @param runnable function to run
     */

    public static void scopeSchema(String name, Functional.ThrowingRunnable runnable) {
        try(var __ = scopeSchema(name)) {
            runnable.run();
        }catch(Exception e) {
            throw new RuntimeException("Error in scoped schema", e);
        }
    }


    /**
     * Runs supplier in scoped schema and returns result
     *
     * @param name Schema name to set
     * @param supplier function to run
     * @return result of lambda
     */

    public static <T> T scopeSchema(String name, Functional.ThrowingSupplier<T> supplier) {
        try (var __ = scopeSchema(name)) {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException("Error in scoped schema", e);
        }
    }
}

