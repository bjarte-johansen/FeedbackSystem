package root;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import root.quicktests.DatabaseManager;
import root.common.utils.RandomPastInstant;
import root.logger.Logger;
import root.logger.LoggerScope;
import root.models.Review;
import root.repositories.ReviewRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
public class TestA0Review {
    LoggerScope __logger;

    @Autowired
    ReviewRepository reviewRepo;// = RepositoryProxyConstructor.create(ReviewRepository.class);

    @Autowired
    DatabaseManager databaseManager;


    /**
     * Initializes the test environment by setting the "APP_ENV" system property to "test" before all tests are run.
     * This allows the application to use test-specific configurations, such as connecting to a test database instead
     * of a production
     */

    @BeforeAll
    static void init() {
        System.setProperty("APP_ENV", "test");
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
        __logger = Logger.scope("BEGIN TEST TestA0Review.beforeEach");

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
    public void afterEach() throws Exception {
        databaseManager.clean();

        __logger.close();

        Logger.log("END OF TEST");
        Logger.log("");
        Logger.log("");

        //System.out.println("-".repeat(40));
    }

    protected void insertReview1() throws Exception {
        Review r = new Review();
        r.setExternalId("/product/2");
        r.setAuthorId(1L);
        r.setAuthorName("John Doe");
        r.setScore(123);
        r.setComment("abc def");
        r.setCreatedAt(RandomPastInstant.generate(java.time.Duration.ofDays(1), java.time.Duration.ofDays(30)));
        reviewRepo.save(r);
    }

    /**
     * Tests that a review can be created and that the total number of reviews increases accordingly, and that the IDs
     * of the created reviews are sequentially increasing, starting from 1.
     *
     * @throws Exception any
     */
    @Test
    public void createReview() throws Exception {
        int n = 5;

        assertEquals(0, reviewRepo.findAll().size());

        for (int i = 0; i < n; i++) {
            // insert a review
            insertReview1();

            var found = reviewRepo.findAll();

            // assert that the first review has id 1 and that there is now 1 review in total
            assertEquals(i + 1, found.size());
            assertEquals(i + 1, found.getLast().getId());
        }
    }

    /**
     * Tests that a review can be created and then retrieved by its ID, and that the retrieved review has the expected
     * values for all its fields.
     *
     * @throws Exception any
     */
    @Test
    public void readReviewWithFindById() throws Exception {
        assertEquals(0, reviewRepo.count());

        // insert a review
        Review rw = new Review();
        rw.setExternalId("/product/2");
        rw.setAuthorId(1L);
        rw.setAuthorName("John Doe");
        rw.setScore(123);
        rw.setTitle("title");
        rw.setComment("abc def");
        reviewRepo.save(rw);

        // assert that the first review has the expected values
        var r = reviewRepo.findById(rw.getId()).orElse(null);
        Assertions.assertNotNull(r);

        assertEquals("/product/2", r.getExternalId());
        assertEquals(1L, r.getAuthorId());
        assertEquals("John Doe", r.getAuthorName());
        assertEquals(123, r.getScore());
        assertEquals("abc def", r.getComment());
        assertEquals("title", r.getTitle());
    }


    /**
     * Tests that creating multiple reviews results in sequentially increasing IDs, starting from 1, and that the total
     * number of reviews increases accordingly.
     *
     * @throws Exception any
     */

    @Test
    public void createReviewAndCheckId() throws Exception {
        assertEquals(0, reviewRepo.findAll().size());

        int n = 10;
        for (int i = 0; i < n; i++) {
            insertReview1();
            assertEquals(i + 1, reviewRepo.findAll().getLast().getId());
        }
    }


    /**
     * Tests that deleting a list of reviews decreases the total number of reviews by the size of the list, and that
     * after deleting all reviews, there are 0 reviews in total.
     *
     * @throws Exception any
     */

    @Test
    public void deleteReviewList() throws Exception {
        assertEquals(0, reviewRepo.count());

        // insert 5 reviews
        int n = 5;
        for (int i = 0; i < n; i++) {
            insertReview1();
        }

        // assert that there are now n reviews in total
        assertEquals(n, reviewRepo.count());

        // findAll() into two sublists
        var list1 = reviewRepo.findAll().subList(0, 3);
        var list2 = reviewRepo.findAll().subList(3, n);

        // delete first sublist and assert that there are now 2 reviews in total
        reviewRepo.deleteAll(list1);
        assertEquals(2, reviewRepo.count());

        // delete second sublist and assert that there are now 0 reviews in total
        reviewRepo.deleteAll(list2);
        assertEquals(0, reviewRepo.count());
    }

    /**
     * Tests that deleting reviews one by one decreases the total number of reviews by 1 each time, and that after
     * deleting all reviews, there are 0 reviews in total.
     *
     * @throws Exception any
     */
    @Test
    public void deleteReviewInstance() throws Exception {
        int n = 5;

        // insert n reviews
        for (int i = 0; i < n; i++) {
            insertReview1();
        }

        // assert that there are now n reviews in total
        assertEquals(n, reviewRepo.count());

        // delete reviews one by one and assert that the number of reviews decreases by 1 each time
        for (int i = 0; i < n; i++) {
            var list = reviewRepo.findAll();
            reviewRepo.delete(list.getFirst());

            assertEquals(n - i - 1, reviewRepo.count());
        }

        // assert that there are now 0 reviews in total
        assertEquals(0, reviewRepo.count());
    }
}
