package root.app;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import root.database.CustomDataSource;
import root.database.DataSourceManager;
import root.database.connectionproviders.CustomConnectionProvider;
import root.logger.Logger;

import java.lang.reflect.Array;
import java.util.Arrays;

public class AppContext {
    private CustomDataSource ds;

    /**
     * Initializes the application context, including data sources and connection providers.
     * Also prints the application text banner.
     */

    public AppContext() {
        // print app text banner
        AppTextBanner.print();


        // initialize data sources and connection providers
        initDatasourceAndConnectionProvider();
    }

    public void initDatasourceAndConnectionProvider() {
        // FIXME:
        //  set to true to use test tenant schema, otherwise tentants schema which needs to be supplied in
        //  routes to controller via id or name that we encode to schema name
        AppConfig.USE_TEST_TENANT = true;

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


    /*
    main event triggers
     */

    //triggerChanged(entity.class, entity.getId(), whatChanged, whenChanged);

/*
    class SystemMessage{
        public String systemEventId;

        public SystemMessage(String systemEventId) {
            this.systemEventId = systemEventId;
        }

        @Override
        public String toString() {
            return "SystemMessage{id='" + systemEventId + "'}";
        }
    }

    class EventMarkReviewStatsDirtyArgs extends SystemMessage {
        public String externalId;

        public EventMarkReviewStatsDirtyArgs(String systemEventId, String externalId) {
            super(EventMarkReviewStatsDirtyArgs.class.getSimpleName());
            this.externalId = externalId;
        }

        @Override
        public String toString() {
            return "EventMarkReviewStatsDirtyArgs{externalId='" + externalId + "'}";
        }
    }

    class
*/

}