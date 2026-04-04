package root.app;

import root.database.CustomDataSource;
import root.database.DataSourceManager;
import root.database.connectionproviders.CustomConnectionProvider;
import root.logger.Logger;

import java.lang.reflect.Array;
import java.util.Arrays;

public class AppContext {
    private CustomDataSource ds;

    public AppContext() {
        // print app text banner
        AppTextBanner.print();

        // FIXME:
        //  set to true to use test tenant schema, otherwise tentants schema which needs to be supplied in
        //  routes to controller via id or name that we encode to schema name
        AppConfig.USE_TEST_TENANT = true;

        try {
            if (AppConfig.USE_TEST_TENANT) {
                // initialize data sources and multi-tenant connection provider
                try (var _1 = Logger.scope("initialize single-tenant (scheme = test)) connection provider")) {
                    initSingleTenantConnectionProvider();
                }
            } else {
                // initialize data sources and multi-tenant connection provider
                try (var _1 = Logger.scope("initialize multi-tenant connection provider")) {
                    initMultiTenantConnectionProvider();
                }
            }
        } catch(Exception e) {
            Logger.log(e.getMessage());
            throw new RuntimeException("Failed to initialize connection provider", e);
        }
    }

    public void initMultiTenantConnectionProvider() throws Exception{
        // create datasource (database datasource)
        ds = new CustomDataSource(AppConfig.TEST);

        // create and set multi-tenant connection provider, allows to set schema per thread (aka request)
        var connectionProvider = new AppTenantConnectionProvider(ds);
        DataSourceManager.setConnectionProvider(connectionProvider);
        DataSourceManager.setMultiTenant(true);

        Logger.log("Multi-tenant connection provider initialized");
    }

    public void initSingleTenantConnectionProvider() throws Exception {
        // create datasource (database datasource)
        ds = new CustomDataSource(AppConfig.TEST);

        // create and set single-tenant connection provider
        var connectionProvider = new CustomConnectionProvider(ds);
        DataSourceManager.setConnectionProvider(connectionProvider);
        DataSourceManager.setMultiTenant(false);

        Logger.log("Single-tenant connection provider initialized");
    }


    /*
    main event triggers
     */

    public static final String EVENT_REVIEW_CRUD_OPERATION = "review.crud_op";
    public static final String EVENT_TENANT_CRUD_OPERATION = "tenant.crud_op";
    public static final String EVENT_REVIEWER_CRUD_OPERATION = "reviewer.crud_op";
    public static final String EVENT_REVIEW_AGGREGATE_CRUD_OPERATION = "review_aggregate.crud_op";
    public static final String EVENT_MARK_REVIEW_STATS_DIRTY = "review.mark_stats_dirty";

    public static String FeedbackEventIdToString(String eventId) {
        return eventId;
    }

    public static Class<?> eventTypeToEntityClass(String eventId) {
        return switch(eventId) {
            case EVENT_REVIEW_CRUD_OPERATION -> root.models.Review.class;
            case EVENT_TENANT_CRUD_OPERATION -> root.models.Tenant.class;
            case EVENT_REVIEWER_CRUD_OPERATION -> root.models.Reviewer.class;
            case EVENT_REVIEW_AGGREGATE_CRUD_OPERATION -> root.models.ReviewAggregate.class;
            case EVENT_MARK_REVIEW_STATS_DIRTY -> null;
            default -> null;
        };
    }
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