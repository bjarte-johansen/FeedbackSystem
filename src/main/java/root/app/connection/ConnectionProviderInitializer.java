package root.app.connection;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import root.app.AppConfig;
import root.app.AppTextBanner;
import root.database.CustomDataSource;
import root.database.DataSourceManager;
import root.database.connectionproviders.CustomConnectionProvider;
import root.includes.logger.Logger;

@Component("appContext")
public class ConnectionProviderInitializer {
    private boolean isInitialized = false;


    /**
     * Initializes the application context, including data sources and connection providers.
     * Also prints the application text banner.
     */

    public ConnectionProviderInitializer() { }

    @PostConstruct
    public void initialize() {
        if(isInitialized) return;
        isInitialized = true;

        Logger.withScope("Compiling javascript (possibly)", () -> {
            //JsLoader.compileFile("src/main/resources/static/js/__generated__.js");
        });

        Logger.withScope("initializing AppContext instance", () -> {
            // print app text banner
            AppTextBanner.print();

            // initialize data sources and connection providers
            Logger.withScope("Initializing connection providers", this::initDatasourceAndConnectionProvider);
        });
    }


    /**
     * Initializes the data source and connection provider based on the application configuration.
     */

    private void initDatasourceAndConnectionProvider() {
        CustomDataSource ds;
        ds = new CustomDataSource(AppConfig.CURRENT_CONNECTION_PARAMS);
        ds.setMaxPoolSize(AppConfig.DB_MAX_CONNECTION_POOL_SIZE);
        ds.warm(2);

        // create and set multi-tenant connection provider, allows to set schema per thread (aka request)
        CustomConnectionProvider connectionProvider = new MultiTenantConnectionProvider(ds);
        DataSourceManager.setConnectionProvider(connectionProvider);
        DataSourceManager.setMultiTenant(true);

        Logger.log("Initialized " + connectionProvider.getClass().getSimpleName() + " connection provider");
    }
}