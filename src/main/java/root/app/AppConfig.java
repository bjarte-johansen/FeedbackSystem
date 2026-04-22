package root.app;

import org.springframework.stereotype.Component;
import root.database.DataSourceConnectionParams;

import javax.xml.crypto.Data;
import java.util.regex.Pattern;


@Component
public class AppConfig {
    //public static final DataSourceConnectionParams LOCALHOST = new DataSourceConnectionParams("jdbc:postgresql://localhost:5432/", "postgres", "postgres", "test");
    public static final DataSourceConnectionParams TEST = new DataSourceConnectionParams("jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905", "h184905", "pass", "test");
    public static final DataSourceConnectionParams PROD = new DataSourceConnectionParams("jdbc:postgresql://ider-database.westeurope.cloudapp.azure.com:5433/h184905", "h184905", "pass", "test");
    public static final DataSourceConnectionParams CURRENT_CONNECTION_PARAMS = TEST;

    // client settings
    public static boolean ENABLE_CLIENT_EMAIL_AND_PASSWORD_REQUIRED = false;

    // password stuff
    public static int MIN_PASSWORD_LENGTH = 8;
    public static String VALID_PASSWORD_REGEX = "^[A-Za-z0-9@#$%^&+=!.*_-]+$";

    // email stuff
    public static String VALID_EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$";

    // verification code stuff
    // note that VERIFICATION_CODE_EMAIL_FORMAT includes TWO %s, that are inserted during sending; the first
    // is the code, the second is host so user knows its from the right domain
    public static int VERIFICATION_CODE_EXPIRATION_SECONDS = 300;
    public static int MAX_VERIFICATION_CODE_ATTEMPTS = 5;
    public static String VERIFICATION_CODE_SECRET = "everyone-likes-a-spicy-meatball";
    public static String VERIFICATION_CODE_EMAIL_FORMAT = """
        <html>
          <body style="font-family: Arial, sans-serif;">
            <div style="font-size:24px;font-weight:bold;
                        background:#f2f2f2;padding:10px;
                        display:inline-block;border-radius:6px;margin-bottom:16px;">%s</div>
            <p>Bruk denne koden for å bekrefte omtalen din på %s</p>
            <p style="margin-top:20px;color:#555;">
                Vennlig hilsen FeedbackSystem DAT109
            </p>
          </body>
        </html>
        """;

    // schema validator, allows only letters, numbers and underscores, must start with a letter or underscore
    public static final Pattern VALID_SCHEMA_NAME_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

    // prevent double vote expiration time in days, after which a reviewer can vote again on the same review
    public static int DEFAULT_REVIEW_VOTE_EXPIRATION_IN_DAYS = 30;

    public static int DEFAULT_MIN_SCORE = 1;
    public static int DEFAULT_MAX_SCORE = 5;

    // controller settings
    // TODO: must be set to false for production, otherwise request parameters and stack traces will be printed in logs,
    //  which can lead to security issues and performance degradation.
    public static boolean CONTROLLER_PRINT_REQUEST_PARAMS = true;
    public static boolean CONTROLLER_PRINT_STACK_TRACE_ON_ERROR = true;

    public static String DEFAULT_INVALID_EXTERNAL_ID = "/no-valid-path-supplied";

    // max number of connections in the connection pool, should be set according to expected load and database limits
    public final static int DB_MAX_CONNECTION_POOL_SIZE = 30;

    // TODO: must be set to false for production
    //public static boolean TESTING_MODE = true;

    // default pagination settings for reviews listing page
    public final static int CLIENT_DEFAULT_MAX_VISIBLE_REVIEWS = 5;
    public final static int ADMIN_DEFAULT_MAX_VISIBLE_REVIEWS = Integer.MAX_VALUE;

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
    public static final String REVIEW_SETTINGS_TABLE_NAME = "review_settings";

    public static final String TENANT_TABLE_NAME = "tenant";
    public static final String TENANT_DOMAIN_TABLE_NAME = "tenant_domain";

    // used only for testing fsqlquery/proxy, not part of project

    // TODO: remove before delivery
    public static final String FANTASY_TABLE_NAME = "fantasy";
}
