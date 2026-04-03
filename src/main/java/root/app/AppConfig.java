package root.app;

import root.database.DataSourceConnectionParams;

public class AppConfig {
    public static final DataSourceConnectionParams TEST = new DataSourceConnectionParams("jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905?currentSchema=test","h184905", "pass", "test");
    public static final DataSourceConnectionParams PROD = new DataSourceConnectionParams("jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905?currentSchema=public","h184905", "pass", "public");

    public static boolean USE_TEST_TENANT = false;

    public static final String DEFAULT_ENTITY_ID_NAME = "id";
    public static final String DEFAULT_IDENTIFIER_QUOTE_STRING = "\"";

    public static int DEFAULT_MAX_VISIBLE_REVIEWS = 3;

    // table names
    public static final String REVIEW_TABLE_NAME = "review";
    public static final String REVIEWER_TABLE_NAME = "reviewer";
    public static final String TENANT_TABLE_NAME = "tenant";

    // used only for testing fsqlquery/proxy, not part of project
    // TODO: remove before delivery
    public static final String FANTASY_TABLE_NAME = "fantasy";
}
