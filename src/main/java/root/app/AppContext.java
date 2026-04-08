package root.app;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import root.database.CustomDataSource;
import root.database.DataSourceManager;
import root.database.connectionproviders.CustomConnectionProvider;
import root.includes.logger.Logger;

import static root.common.utils.Preconditions.checkArgument;

public class AppContext {
    private static final AppContext INSTANCE = new AppContext();
    private static ThreadLocal<Long> currentTenantId = new ThreadLocal<>();

    private CustomDataSource ds;

    /**
     * Initializes the application context, including data sources and connection providers.
     * Also prints the application text banner.
     */

    private AppContext() {
        initialize();
    }

    private void initialize() {
        // print app text banner
        AppTextBanner.print();

        // initialize data sources and connection providers
        initDatasourceAndConnectionProvider();
    }

    public static AppContext getSingleton() {
        return AppContext.INSTANCE;
    }

    public void initDatasourceAndConnectionProvider() {
        // FIXME:
        //  set to true to use test tenant schema, otherwise tentants schema which needs to be supplied in
        //  routes to controller via id or name that we encode to schema name
        ds = new CustomDataSource(AppConfig.TEST);

        if (AppConfig.USE_TEST_TENANT) {
            initSingleTenantConnectionProvider(ds);
        } else {
            initMultiTenantConnectionProvider(ds);
        }
    }

    public void initMultiTenantConnectionProvider(CustomDataSource ds) {
        // create and set multi-tenant connection provider, allows to set schema per thread (aka request)
        CustomConnectionProvider connectionProvider = new AppTenantConnectionProvider(ds);
        DataSourceManager.setConnectionProvider(connectionProvider);
        DataSourceManager.setMultiTenant(true);

        Logger.log("Initialized " + connectionProvider.getClass().getSimpleName() + " connection provider");
    }

    public void initSingleTenantConnectionProvider(CustomDataSource ds) {
        // create and set single-tenant connection provider
        CustomConnectionProvider connectionProvider = new CustomConnectionProvider(ds);
        DataSourceManager.setConnectionProvider(connectionProvider);
        DataSourceManager.setMultiTenant(false);

        Logger.log("Initialized " + connectionProvider.getClass().getSimpleName() + " connection provider");
    }

/*
    public ThreadLocal<Long> getCurrentTenantIdStorage() {
        return currentTenantId;
    }
 */

    public void setTenantId(Long tenantId) {
        currentTenantId.set(tenantId);
    }

    public Long getTenantId() {
        var tenantId = currentTenantId.get();
        checkArgument(tenantId != null, "Tenant ID is not set in AppContext. This should never happen if the RequestContextFilter is working correctly.");
        return tenantId;
    }

    public void removeTenantId() {
        currentTenantId.remove();
    }

    /**
     * Get the administrator ID from the current HTTP session.
     * @return
     */

    public static long getAdministratorId() {
        var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return 0;

        var session = attrs.getRequest().getSession(false);
        if (session == null) return 0;

        Object v = session.getAttribute("administrator_id");
        return (v instanceof Long) ? (Long) v : 0;
    }


    /**
     * Check if the current user is an administrator based on the session attribute.
     * @return true if the user is an administrator, false otherwise.
     */
    public static boolean isAdministrator(){
        return ((long) getAdministratorId()) != 0;
    }


    /**
     * Check if the current user is an administrator and throw an exception if not.
     * @return
     */

    public static boolean checkIsAdministrator(){
        if(!isAdministrator()){
            throw new RuntimeException("Handlingen krever at du er innlogget som administrator");
        }
        return true;
    }
}