package root.app;

import root.database.DataSourceConnectionParams;
import root.models.Tenant;
import root.services.PasswordService;


public class AppConfig {
    public static final DataSourceConnectionParams TEST = new DataSourceConnectionParams("jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905?currentSchema=test", "h184905", "pass", "test");
    public static final DataSourceConnectionParams PROD = new DataSourceConnectionParams("jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905?currentSchema=public", "h184905", "pass", "public");





    public static int DEFAULT_REVIEW_VOTE_EXPIRATION_IN_DAYS = 30;

    // controller settings
    // TODO: must be set to false for production, otherwise request parameters and stack traces will be printed in logs,
    //  which can lead to security issues and performance degradation.
    public static boolean CONTROLLER_PRINT_REQUEST_PARAMS = true;
    public static boolean CONTROLLER_PRINT_STACK_TRACE_ON_ERROR = true;

    // TODO: must be set to false for production
    public static boolean TESTING_MODE = true;

    public static boolean OVERRIDE_TENANT = true;
    public static String OVERRIDE_TENANT_SCHEMA = "test";
    public static long OVERRIDE_TENANT_ID = 1;
    /*
    public static Tenant OVERRIDE_TENANT_OBJECT = new Tenant(
        "test",
        "test.com",
        "test-api-key",
        "test@test.com",
        (new PasswordService()).hash("test"),
        "ignore",
        "test"
    );
     */

    // TODO: must be set to false in production, otherwise all reviews will be automatically approved without moderation.
    public static boolean AUTO_APPROVE_NEW_REVIEWS = true;

    // TODO: must be set to false for production
    public static boolean USE_TEST_TENANT = true;

    public static final String DEFAULT_ENTITY_ID_NAME = "id";
    public static final String DEFAULT_IDENTIFIER_QUOTE_STRING = "\"";

    // default pagination settings for reviews listing page
    public static int CLIENT_DEFAULT_MAX_VISIBLE_REVIEWS = 3;

    // table names
    public static final String REVIEW_TABLE_NAME = "review";
    public static final String REVIEWER_TABLE_NAME = "reviewer";
    public static final String TENANT_TABLE_NAME = "tenant";
    public static final String REVIEW_VOTE_TABLE_NAME = "review_vote";

    // used only for testing fsqlquery/proxy, not part of project
    // TODO: remove before delivery
    public static final String FANTASY_TABLE_NAME = "fantasy";
}
