package root.app;

import org.springframework.context.annotation.Configuration;
import root.database.DataSourceConnectionParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public class AppConfig {
    public static final DataSourceConnectionParams TEST = new DataSourceConnectionParams("jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905?currentSchema=test","h184905", "pass", "test");
    public static final DataSourceConnectionParams PROD = new DataSourceConnectionParams("jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905?currentSchema=public","h184905", "pass", "public");

    public static String SESSION_ROOT_KEY = "feedback_app_session";
    public static String SESSION_REVIEW_LIKE_MAP_KEY = "review_user_like_map";

    //
    public static boolean CONTROLLER_PRINT_REQUEST_PARAMS = true;
    public static boolean CONTROLLER_PRINT_STACK_TRACE_ON_ERROR = true;

    // TODO: must be set to false in production, otherwise all reviews will be automatically approved without moderation.
    public static boolean AUTO_APPROVE_NEW_REVIEWS = true;

    // TODO: must be set to false for production
    public static boolean USE_TEST_TENANT = false;

    public static final String DEFAULT_ENTITY_ID_NAME = "id";
    public static final String DEFAULT_IDENTIFIER_QUOTE_STRING = "\"";

    public static int DEFAULT_MAX_VISIBLE_REVIEWS = 3;

    public static int DEFAULT_REVIEW_SCORE_SCALE_MIN = 1;
    public static int DEFAULT_REVIEW_SCORE_SCALE_MAX = 5;

    // table names
    public static final String REVIEW_TABLE_NAME = "review";
    public static final String REVIEWER_TABLE_NAME = "reviewer";
    public static final String TENANT_TABLE_NAME = "tenant";

    // used only for testing fsqlquery/proxy, not part of project
    // TODO: remove before delivery
    public static final String FANTASY_TABLE_NAME = "fantasy";
}
