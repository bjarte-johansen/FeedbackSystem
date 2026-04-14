package root.app;

import org.springframework.stereotype.Component;
import root.database.DataSourceConnectionParams;


@Component
public class AppConfig {
    /*
    public static final DataSourceConnectionParams TEST = new DataSourceConnectionParams("jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905?currentSchema=test", "h184905", "pass", "test");
    public static final DataSourceConnectionParams PROD = new DataSourceConnectionParams("jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905?currentSchema=public", "h184905", "pass", "public");
     */
    public static final DataSourceConnectionParams TEST = new DataSourceConnectionParams("jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905", "h184905", "pass", "test");
    public static final DataSourceConnectionParams PROD = new DataSourceConnectionParams("jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905?currentSchema=public", "h184905", "pass", "public");
    public static final DataSourceConnectionParams CURRENT_CONNECTION_PARAMS = TEST;

    // client settings
    public static boolean ENABLE_CLIENT_EMAIL_AND_PASSWORD_REQUIRED = false;

    // password stuff
    public static int MIN_PASSWORD_LENGTH = 8;
    public static String VALID_PASSWORD_REGEX = "^[A-Za-z0-9@#$%^&+=!.*_-]+$";

    // email stuff
    public static String VALID_EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$";

    // prevent double vote expiration time in days, after which a reviewer can vote again on the same review
    public static int DEFAULT_REVIEW_VOTE_EXPIRATION_IN_DAYS = 30;

    // controller settings
    // TODO: must be set to false for production, otherwise request parameters and stack traces will be printed in logs,
    //  which can lead to security issues and performance degradation.
    public static boolean CONTROLLER_PRINT_REQUEST_PARAMS = true;
    public static boolean CONTROLLER_PRINT_STACK_TRACE_ON_ERROR = true;

    // max number of connections in the connection pool, should be set according to expected load and database limits
    public final static int DB_MAX_CONNECTION_POOL_SIZE = 30;

    // TODO: must be set to false for production
    public static boolean TESTING_MODE = true;

    // default pagination settings for reviews listing page
    public static int CLIENT_DEFAULT_MAX_VISIBLE_REVIEWS = 5;
    public static int ADMIN_DEFAULT_MAX_VISIBLE_REVIEWS = 10;

    // TODO: must be set to false in production, otherwise all reviews will be automatically approved without moderation.
    public static boolean AUTO_APPROVE_NEW_REVIEWS = true;

    // TODO: must be set to false for production
    public static final boolean USE_MULTI_TENANT = true;

    // set database specific stuff, mostly for JDBC adapter and metadata queries
    public static final String DEFAULT_ENTITY_ID_NAME = "id";
    public static final String DEFAULT_IDENTIFIER_QUOTE_STRING = "\"";

    // table names
    public static final String REVIEW_TABLE_NAME = "review";
    public static final String REVIEWER_TABLE_NAME = "reviewer";
    public static final String REVIEW_VOTE_TABLE_NAME = "review_vote";

    public static final String TENANT_TABLE_NAME = "tenant";
    public static final String TENANT_DOMAIN_TABLE_NAME = "tenant_domain";

    // used only for testing fsqlquery/proxy, not part of project

    // TODO: remove before delivery
    public static final String FANTASY_TABLE_NAME = "fantasy";
}
