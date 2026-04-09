package root;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import root.includes.logger.Logger;
import root.includes.logger.LoggerScope;
import root.repositories.ReviewRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TestA0Base {
    public class LoggerScopeWrapper implements LoggerScope {
        private final String blockName;

        public LoggerScopeWrapper(String blockName) {
            this.blockName = blockName;
            loggerEnterExitBlock("enter", blockName);
        }

        @Override
        public void close() {
            loggerEnterExitBlock("exit", blockName);
        }
    }

    public static int ENTER_EXIT_BLOCK_WIDTH = 80;

    LoggerScope __logger;

    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    DatabaseManager databaseManager;


    /**
     * Initializes the test environment by setting the "APP_ENV" system property to "test" before all tests are run.
     * This allows the application to use test-specific configurations, such as connecting to a test database instead
     * of a production
     */

    @BeforeAll
    static void init() throws Exception {
        System.setProperty("APP_ENV", "test");
/*
        String env = System.getenv().getOrDefault("APP_ENV", "prod");

        Properties p = new Properties();
        try (var is = Files.newInputStream(
            Path.of("application-" + env + ".properties"))) {
            p.load(is);

            Logger.log("db.url", p.getProperty("db.url"));
            Logger.log("db.user", p.getProperty("db.user"));
            Logger.log("db.pass", p.getProperty("db.pass"));
        }
 */
    }

    public static void loggerEnterExitBlock(String action, String blockName) {
        System.out.println("-".repeat(ENTER_EXIT_BLOCK_WIDTH));
        System.out.println(("-".repeat(8) + " ".repeat(4) + action + " : " + blockName + " ".repeat(4) + "-".repeat(ENTER_EXIT_BLOCK_WIDTH)).substring(0, 80));
        System.out.println("-".repeat(ENTER_EXIT_BLOCK_WIDTH));
    }

    public static void loggerEnterBlock(String blockName, int depth) {
        loggerEnterExitBlock("BEGIN", blockName);
    }
    public static void loggerExitBlock(String blockName, int depth) {
        loggerEnterExitBlock("END", blockName);
    }

    /**
     * Sets up the test environment before each test by creating a new logger scope for the test and cleaning the
     * database to ensure a consistent starting point for each test.
     *
     * @param info the TestInfo object that provides information about the current test, such as its display name and
     * tags
     * @throws Exception any
     */

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {

        //loggerEnterExitBlock("enter", info.getDisplayName());

        __logger = Logger.scope(
            "myBlock",
            TestA0Base::loggerEnterBlock,
            TestA0Base::loggerExitBlock
        );

        databaseManager.clean();
    }

    /**
     * Cleans the database after each test by calling the DBTest.clean() method, which truncates the reviews, reviewers,
     * and tenants tables and restarts their identity sequences. It also closes the logger scope and prints a separator
     * line for better readability of the test output.
     *
     * @throws Exception any
     */
    @AfterEach
    public void afterEach(TestInfo info) throws Exception {
        databaseManager.clean();

        __logger.close();
    }

    @Test
    public void __dummy__dummy__dummy__(){

    }
}
