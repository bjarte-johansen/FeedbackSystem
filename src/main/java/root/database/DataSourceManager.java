package root.database;

import root.database.connectionproviders.CustomConnectionProvider;
import root.includes.logger.Logger;

import java.sql.Connection;

import static root.common.utils.Preconditions.checkNotNull;

public class DataSourceManager {
    public static final DataSourceManager INSTANCE = new DataSourceManager();

    private CustomConnectionProvider connectionProvider = null;
    private boolean multiTenant = false;


    public static DataSourceManager getSingleton() {
        return INSTANCE;
    }


    /**
     * Sets a custom multi-tenant connection provider. This allows the application to use a custom implementation for
     * resolving tenant identifiers and providing connections for multi-tenant scenarios. The provided connection
     * provider must implement the CustomMultiTenantConnectionProvider interface.
     *
     * @param provider
     */

    public static void setConnectionProvider(CustomConnectionProvider provider) {
        checkNotNull(provider, "Multi-tenant connection provider cannot be null");

        var manager = getSingleton();
        manager.connectionProvider = provider;
    }

    /**
     * Retrieves the currently set multi-tenant connection provider. If no provider has been set, an
     * IllegalStateException is thrown.
     *
     * @return the currently set multi-tenant connection provider
     */
    public static CustomConnectionProvider getConnectionProvider() {
        var manager = getSingleton();
        if (manager.connectionProvider == null) {
            throw new IllegalStateException("Connection provider has not been set. Please initialize the connection provider before using it.");
        }
        return manager.connectionProvider;
    }


    /**
     * Enables or disables multi-tenancy support. When enabled, the application will use the configured multi-tenant
     * connection provider to resolve tenant identifiers and provide connections for multi-tenant scenarios. When
     * disabled, the application will use a default connection provider that does not support multi-tenancy. This method
     * should be called before any database operations are performed to ensure that the correct connection provider is
     * used.
     *
     * @param enable
     */

    public static void setMultiTenant(boolean enable) {
        var manager = getSingleton();
        manager.multiTenant = true;

        // TODO: Consider adding logic to switch between different connection providers based on the multi-tenant flag,
        //  if needed.
    }


    /**
     * return a database connection from the currently configured connection provider.
     *
     * @return a database connection
     */

    public static Connection getConnection() throws Exception {
        var manager = getSingleton();
        return manager.connectionProvider.getConnection();
    }


    /**
     * Functional interface representing a consumer that accepts a database connection and returns a result. This is
     * used as a parameter type for the with() method to allow executing database operations with a managed connection.
     *
     * @param <R>
     */

    public interface ReturningConnectionConsumer<R> {
        R run(Connection connection) throws Exception;
    }

    /**
     * Functional interface representing a consumer that accepts a database connection and does not return a result. This
     * is used as a parameter type for the withVoid() method to allow executing database operations with a managed
     * connection for operations that do not return a result.
     *
     */

    public interface VoidConnectionConsumer {
        void run(Connection connection) throws Exception;
    }


    /**
     * Utility method to execute a database operation with a managed connection. The provided function is executed with
     * a connection that is automatically closed after the operation completes, ensuring proper resource management.
     * <p>
     * Result is returned from the provided function.
     *
     * @param fn
     * @param <R>
     * @return
     */

    public static <R> R with(ReturningConnectionConsumer<R> fn) {
        try (Connection connection = getConnection()) {
            return fn.run(connection);
        } catch (Exception e) {
            Logger.log("An exception occurred while executing a database operation: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    /**
     * @see #with(ReturningConnectionConsumer) for executing database operations with a managed connection. This version
     * is used for operations that do not return a result.
     */

    public static void withVoid(VoidConnectionConsumer fn) {
        try (Connection connection = getConnection()) {
            fn.run(connection);
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while executing a database operation", e);
        }
    }
}