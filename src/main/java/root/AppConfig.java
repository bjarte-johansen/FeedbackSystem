package root;

public class AppConfig {
    public static final String DEFAULT_ENTITY_ID_NAME = "id";
    public static final String DEFAULT_IDENTIFIER_QUOTE_STRING = "\"";

    public static final String REVIEW_TABLE_NAME = "review";
    public static final String REVIEWER_TABLE_NAME = "reviewer";
    public static final String TENANT_TABLE_NAME = "tenant";

    // used only for testing fsqlquery/proxy, not part of project
    // TODO: remove before delivery
    public static final String FANTASY_TABLE_NAME = "fantasy";
}
