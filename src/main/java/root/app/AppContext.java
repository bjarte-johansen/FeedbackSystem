package root.app;

import root.database.CustomDataSource;
import root.database.DataSourceManager;
import root.database.connectionproviders.CustomConnectionProvider;
import root.logger.Logger;

public class AppContext {
    private CustomDataSource ds;

    public AppContext() {
        // print app text banner
        AppTextBanner.print();

        // FIXME:
        //  set to true to use test tenant schema, otherwise tentants schema which needs to be supplied in
        //  routes to controller via id or name that we encode to schema name
        AppConfig.USE_TEST_TENANT = true;

        if(AppConfig.USE_TEST_TENANT){
            // initialize data sources and multi-tenant connection provider
            try (var _1 = Logger.scope("initialize data sources and test-schema connection provider")) {
                initSingleTenantConnectionProvider();
            }
        }else {
            // initialize data sources and multi-tenant connection provider
            try (var _1 = Logger.scope("initialize data sources and multi-tenant connection provider")) {
                initMultiTenantConnectionProvider();
            }
        }
    }

    public void initMultiTenantConnectionProvider() {
        try {
            // create datasource (database datasource)
            ds = new CustomDataSource(AppConfig.TEST);

            // create and set multi-tenant connection provider, allows to set schema per thread (aka request)
            var connectionProvider = new AppTenantConnectionProvider(ds);
            DataSourceManager.setConnectionProvider(connectionProvider);
            DataSourceManager.setMultiTenant(true);

            Logger.log("Multi-tenant connection provider initialized with custom datasource");
        }catch (Exception e){
            Logger.log(e.getMessage());
            throw new RuntimeException("Failed to initialize multi-tenant connection provider", e);
        }
    }

    public void initSingleTenantConnectionProvider() {
        try {
            // create datasource (database datasource)
            ds = new CustomDataSource(AppConfig.TEST);

            // create and set multi-tenant connection provider, allows to set schema per thread (aka request)
            var connectionProvider = new CustomConnectionProvider(ds);
            DataSourceManager.setConnectionProvider(connectionProvider);
            DataSourceManager.setMultiTenant(false);

            Logger.log("Test schema connection provider initialized with custom datasource");
        }catch (Exception e){
            Logger.log(e.getMessage());
            throw new RuntimeException("Failed to initialize multi-tenant connection provider", e);
        }
    }
}