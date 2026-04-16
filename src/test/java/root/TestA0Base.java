package root;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import root.app.AppContext;
import root.app.AppRequestSchema;
import root.includes.logger.Logger;
import root.includes.logger.LoggerScope;
import root.repositories.ReviewRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("local")
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
    public static String TEST_SCHEMA_NAME = "test";
    public static String PREVIOUS_SCHEMA_NAME = null;

    @Autowired
    AppContext appContext;

    // private stuff
    LoggerScope __logger;

    @Autowired
    ReviewRepository reviewRepo;

    //@Autowired
    //DatabaseManager databaseManager;


    public TestA0Base() {
    }
    /**
     * Initializes the test environment by setting the "APP_ENV" system property to "test" before all tests are run.
     * This allows the application to use test-specific configurations, such as connecting to a test database instead
     * of a production
     */

    @BeforeAll
    static void init() throws Exception {

    }

    public static void loggerEnterExitBlock(String action, String blockName) {
        Logger.log("-".repeat(ENTER_EXIT_BLOCK_WIDTH));
        Logger.log(("-".repeat(8) + " ".repeat(4) + action + " : " + blockName + " ".repeat(4) + "-".repeat(ENTER_EXIT_BLOCK_WIDTH)).substring(0, 80));
        Logger.log("-".repeat(ENTER_EXIT_BLOCK_WIDTH));
    }

    public static void loggerEnterBlock(String blockName, int depth) {
        loggerEnterExitBlock("BEGIN", blockName);
    }
    public static void loggerExitBlock(String blockName, int depth) {
        loggerEnterExitBlock("END", blockName);
    }

    private void cleanDatabase(){
//        Logger.log("cleaning database...");
//        databaseManager.clean();
//        Logger.log("cleaning database... OK");
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
        appContext.init();
        System.setProperty("APP_ENV", "test");

        __logger = Logger.scope(
            "myBlock",
            TestA0Base::loggerEnterBlock,
            TestA0Base::loggerExitBlock
        );

        PREVIOUS_SCHEMA_NAME = AppRequestSchema.get();
        AppRequestSchema.set(TEST_SCHEMA_NAME);

        cleanDatabase();
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
        cleanDatabase();

        if(PREVIOUS_SCHEMA_NAME != null) {
            AppRequestSchema.remove();
        }else{
            AppRequestSchema.set(TEST_SCHEMA_NAME);
        }

        __logger.close();
    }

    @Test
    public void printReviews(){
        Logger.log("testing dummy dummy");

        var reviews = reviewRepo.findAll();
        reviews.forEach(System.out::println);
    }
}
