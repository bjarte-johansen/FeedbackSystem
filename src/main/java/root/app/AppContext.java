package root.app;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import root.app.connection.AppMultiTenantConnectionProvider;
import root.database.CustomDataSource;
import root.database.DataSourceManager;
import root.database.connectionproviders.CustomConnectionProvider;
import root.includes.logger.Logger;

@Component("appContext")
public class AppContext {
    //private boolean isInitialized = false;

    /**
     * Initializes the application context, including data sources and connection providers.
     * Also prints the application text banner.
     */

    public AppContext() {
        initialize();
    }

    public void init(){
        initialize();
    }

    @PostConstruct
    public void initialize() {
        Logger.log("Initializing Application Context");
        /*
        if(isInitialized) return;
        isInitialized = true;
        */

        try(var _ = Logger.scope("initializing AppContext instance")) {

            // print app text banner
            //AppTextBanner.print();

            try (var scope = Logger.scope("Initializing connection providers")) {
                // initialize data sources and connection providers
                initDatasourceAndConnectionProvider();
            }
        }
    }


    /**
     * Initializes the data source and connection provider based on the application configuration.
     */

    private void initDatasourceAndConnectionProvider() {
        try(var scope = Logger.scope("Initializing connection providers")) {
            CustomDataSource ds;
            ds = new CustomDataSource(AppConfig.CURRENT_CONNECTION_PARAMS);
            ds.setMaxPoolSize(AppConfig.DB_MAX_CONNECTION_POOL_SIZE);
            ds.warm(2);


            if (!AppConfig.USE_MULTI_TENANT) {
                initSingleTenantConnectionProvider(ds);
            } else {
                initMultiTenantConnectionProvider(ds);
            }
        }
    }


    /**
     * Initializes a multi-tenant connection provider that allows setting the schema per thread (request).
     * @param ds The custom data source to be used for the connection provider.
     */

    public void initMultiTenantConnectionProvider(CustomDataSource ds) {
        // create and set multi-tenant connection provider, allows to set schema per thread (aka request)
        CustomConnectionProvider connectionProvider = new AppMultiTenantConnectionProvider(ds);
        DataSourceManager.setConnectionProvider(connectionProvider);
        DataSourceManager.setMultiTenant(true);

        Logger.log("Initialized " + connectionProvider.getClass().getSimpleName() + " connection provider");
    }


    /**
     * Initializes a single-tenant connection provider that does not allow setting the schema per thread (request).
     * @param ds
     */
    public void initSingleTenantConnectionProvider(CustomDataSource ds) {
        // create and set single-tenant connection provider
        CustomConnectionProvider connectionProvider = new CustomConnectionProvider(ds);
        DataSourceManager.setConnectionProvider(connectionProvider);
        DataSourceManager.setMultiTenant(false);

        Logger.log("Initialized " + connectionProvider.getClass().getSimpleName() + " connection provider");
    }
}