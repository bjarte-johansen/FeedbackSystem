package root;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import root.quicktests.DBTest;
import root.logger.Logger;
import root.logger.LoggerScope;
import root.repositories.ReviewRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TestA0Base {
    LoggerScope __logger;

    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    DBTest dbTest;


    /**
     * Initializes the test environment by setting the "APP_ENV" system property to "test" before all tests are run.
     * This allows the application to use test-specific configurations, such as connecting to a test database instead
     * of a production
     */

    @BeforeAll
    static void init() throws Exception {
        System.setProperty("APP_ENV", "test");

        String env = System.getenv().getOrDefault("APP_ENV", "prod");

        Properties p = new Properties();
        try (var is = Files.newInputStream(
            Path.of("application-" + env + ".properties"))) {
            p.load(is);

            Logger.log("db.url", p.getProperty("db.url"));
            Logger.log("db.user", p.getProperty("db.user"));
            Logger.log("db.pass", p.getProperty("db.pass"));
        }
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
        __logger = Logger.scope("TestA0Review.beforeEach");

        dbTest.clean();
    }

    /**
     * Cleans the database after each test by calling the DBTest.clean() method, which truncates the reviews, reviewers,
     * and tenants tables and restarts their identity sequences. It also closes the logger scope and prints a separator
     * line for better readability of the test output.
     *
     * @throws Exception any
     */
    @AfterEach
    public void afterEach() throws Exception {
        dbTest.clean();

        __logger.close();
        System.out.println("-".repeat(40));
    }
}
