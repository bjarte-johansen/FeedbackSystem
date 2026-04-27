package root.app;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import root.database.DataSourceManager;
import root.database.FSQLQuery;
import root.database.QueryLogger;
import root.includes.context.SchemaContext;
import root.includes.Functional;
import root.includes.logger.Logger;
import root.__ignore__.no_test_extra.TryWithTimer;
import root.services.DatabaseService;

import java.util.List;

/**
 * This class runs on application startup and is used to run any setup tasks, such as database patching and demo data
 * resetting. It can also be used to run any test code that should be executed on startup.
 * <p>
 * It is done BEFORE any routes can be accessed, so it is a good place to run any setup code that should be executed
 * before the application is fully up and running.
 */

@Component
@DependsOn("appContext")
class Startup implements ApplicationRunner {
    private final DatabaseService databaseService;

    /**
     * Construct a new instance of class
     * @param databaseService
     */
    public Startup(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Logger.log("running app startup tasks...");

        /*
        TODO: DO NOT DELETE ANY CODE IN THIS BLOCK, I SAID DO NOT; SQL HAS BEEN COLLECTED
        TODO: BUT RUNS IN A DIFFERENT FUNCTION AS A SINGLE STATEMENT FOR RAW SPEED, 100ms vs 3000ms
         */

        // slow init ... fsql queries, one and one
        //doDatabaseInitSlowForTesting();

        // fast init ..., raw sql data batch queries
        Logger.warn("PS: USING AUTOINSERTED DEMODATA; DOES NOT RUN PATCHERS OR INSERTS, PLAIN SQL INSERT");
        doDatabaseInitFastForTesting();

        Logger.log("app startup tasks OK");
    }

    public void doDatabaseInitSlowForTesting(){
        // patch database
        //databaseService.executeDatabasePatches();

        Logger.log("AppStartup::run, SCHEMA: " + SchemaContext.get());

        // reset demo data
        QueryLogger.start();

        FSQLQuery.setQueryLogger(QueryLogger.getSingleton());
        TryWithTimer.withMeasurement("Old slow insertion with single SQL statement", databaseService::resetDemoData);
        FSQLQuery.setQueryLogger(null);

        List<String> loggedQueries = QueryLogger.stop();
        Logger.withScope("Logged queries during demo data reset:", () -> {
            for (String query : loggedQueries) {
                Logger.log(query);
            }
        });
    }

    public void doDatabaseInitFastForTesting(){
        String sql = """
        SET search_path TO test, public;
        
        TRUNCATE TABLE tenant RESTART IDENTITY CASCADE;
        TRUNCATE TABLE tenant_domain RESTART IDENTITY CASCADE;
        TRUNCATE TABLE review_settings RESTART IDENTITY CASCADE;

        INSERT INTO Tenant(name, domain, api_key, email, password_hash, password_salt, schema_name, enable_listing, enable_submit)
        VALUES ('Tenant1', 'tenant1.test.com', 'tenant-1-api-key', 'tenant1@test.com', '$2a$10$02gG0fQltKtHCB950h3q5.jL9z4mEh/cdoHmOIaKtw2PD0s1H5F7i', '', 'test', true, true);

        INSERT INTO tenant_domain(domain, tenant_id)
        VALUES ('localhost', 1);

        SELECT * FROM Tenant;

        TRUNCATE TABLE review RESTART IDENTITY CASCADE;
        TRUNCATE TABLE reviewer RESTART IDENTITY CASCADE;
        TRUNCATE TABLE review_vote RESTART IDENTITY CASCADE;

        INSERT INTO Reviewer(email, display_name, password_hash, password_salt, created_at, verified_at)
        VALUES ('test@test.com', 'Leif', '$2a$10$lYMyE6mEiBMZRSSSK6zfZOy1SJZVOt4a37PNGpsh7g9/J5/WXnWMK', '', '2026-04-16 00:28:28.1839471', '2026-04-16 00:28:28.1839471');

        INSERT INTO Reviewer(email, display_name, password_hash, password_salt, created_at, verified_at)
        VALUES ('alice@example.com', 'Alice', 'hash1', '', '2026-04-16 00:28:28.1839471', '2026-04-16 00:28:28.1839471');

        INSERT INTO Reviewer(email, display_name, password_hash, password_salt, created_at, verified_at)
        VALUES ('bob@example.com', 'Bob', 'hash2', '', '2026-04-16 00:28:28.1839471', '2026-04-16 00:28:28.1839471');

        INSERT INTO Reviewer(email, display_name, password_hash, password_salt, created_at, verified_at)
        VALUES ('charlie@example.com', 'Charlie', 'hash3', '', '2026-04-16 00:28:28.1839471', '2026-04-16 00:28:28.1839471');

        INSERT INTO Reviewer(email, display_name, password_hash, password_salt, created_at, verified_at)
        VALUES ('diana@example.com', 'Diana', 'hash4', '', '2026-04-16 00:28:28.1839471', '2026-04-16 00:28:28.1839471');

        INSERT INTO Reviewer(email, display_name, password_hash, password_salt, created_at, verified_at)
        VALUES ('eve@example.com', 'Eve', 'hash5', '', '2026-04-16 00:28:28.1839471', '2026-04-16 00:28:28.1839471');

        -- Reviews for /product/1
        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'ChunkyPotato423', 5, 'Amet laboris.', 'Do tempor dolor nostrud tempor. ut ullamco sed minim do commodo nostrud et nisi. ipsum nisi labore. elit.', '2024-04-26 00:28:28.684207', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'Clumsybun117', 5, 'Ut ut.', 'Aliqua dolore labore nisi dolor magna tempor nostrud tempor dolore ut dolore incididunt ad eiusmod ut eiusmod aliquip lorem ex aliqua eiusmod veniam dolor ad elit.', '2024-05-06 00:28:28.684207', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'SassyZombie55', 5, 'Nisi nisi.', 'Enim nisi incididunt tempor ea magna et quis sed laboris eiusmod. eiusmod. nostrud exercitation aliquip dolore do sit adipiscing ipsum consectetur amet. nisi lorem dolor.', '2024-05-16 00:28:28.684207', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'SneakySloth984', 5, 'Enim enim eiusmod.', 'Nisi adipiscing nisi. quis sed. do. ut ut. consectetur. commodo eiusmod amet nisi commodo ea aliqua ea do enim. laboris laboris. tempor nisi labore.', '2024-05-26 00:28:28.684207', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'QuirkyTaco25', 5, 'Ipsum ea minim.', 'Consequat veniam sit elit ut exercitation dolor ut adipiscing. consectetur eiusmod nisi sed sit amet ea dolor ullamco ut. do. ipsum sit. nostrud aliqua. adipiscing ad consectetur.', '2024-06-05 00:28:28.684207', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 2, 'QuirkyPotato469', 4, 'Eiusmod lorem ex sed.', 'Incididunt magna. dolor dolor tempor incididunt lorem. dolor incididunt ad amet enim.', '2024-06-15 00:28:28.684207', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 3, 'ClumsyCactus258', 3, 'Et nisi.', 'Nisi. ea amet tempor ut. ad ut amet elit tempor ea tempor tempor ad ipsum eiusmod consectetur eiusmod. exercitation adipiscing veniam. amet sit ut. ut ullamco sit.', '2024-06-25 00:28:28.684207', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'BouncyWaffle675', 4, 'Aliqua quis consectetur.', 'Ut nisi ex. aliqua adipiscing ea sed ut. incididunt ex ut ut.', '2024-07-05 00:28:28.684207', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'SneakyUnicorn653', 2, 'Ex. laboris.', 'Ipsum sit labore. ut sit. do exercitation amet sed nostrud sit dolor ad labore minim ut commodo sed eiusmod labore ex lorem. lorem et lorem.', '2024-07-15 00:28:28.684207', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'BouncyHamster422', 5, 'Ea incididunt lorem.', 'Consequat incididunt nostrud. magna aliqua incididunt. amet. magna ea veniam. minim ipsum veniam consequat do lorem ad ut eiusmod.', '2024-07-25 00:28:28.684207', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'NerdySloth371', 1, 'Minim quis consequat magna.', 'Labore elit magna. do. eiusmod amet sit sed magna commodo ut veniam. tempor labore ut aliquip labore. do ut ex ex. ipsum. incididunt sed veniam tempor commodo ex sed.', '2024-08-04 00:28:28.684207', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'FluffyPenguin477', 4, 'Ut sed.', 'Ipsum. elit elit veniam elit tempor minim magna ut dolore. labore. amet et. nostrud sit. ex ad ut sit adipiscing dolore tempor lorem sed dolore ipsum eiusmod nostrud ea.', '2024-08-14 00:28:28.684207', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'SassyUnicorn788', 3, 'Eiusmod ea et et.', 'Ut nisi. ipsum nostrud ut ut exercitation dolor. ut dolor minim incididunt quis labore consequat dolore enim ullamco aliqua quis ipsum dolore ad.', '2024-08-24 00:28:28.684207', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'ZanyDragon398', 5, 'Ea ullamco aliqua ipsum.', 'Minim adipiscing dolore. ipsum tempor sit adipiscing tempor consectetur enim nostrud. ut.', '2024-09-03 00:28:28.684207', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'GrumpyGoblin693', 5, 'Do ipsum. consequat laboris.', 'Aliquip. ullamco amet ut. aliqua. adipiscing minim consequat ut consequat veniam ipsum. consectetur commodo sed lorem dolor. aliqua incididunt.', '2024-09-13 00:28:28.684207', 2, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'SillySloth279', 4, 'Nisi aliquip magna.', 'Nisi elit commodo eiusmod ad amet sit ea magna ut consectetur sit minim.', '2024-09-23 00:28:28.6852064', 2, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/1', 1, 'GoofyLlama178', 3, 'Dolor labore.', 'Sit lorem elit dolor consectetur ex. ad amet. do. consectetur eiusmod nisi ut ad sit. exercitation et sed.', '2024-10-03 00:28:28.6852064', 3, 0, 0);

        -- Reviews for 'en-litt-annen-sti'
        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('en-litt-annen-sti', 2, 'DizzyBanana425', 2, 'Elit elit.', 'Do quis quis exercitation labore nisi amet veniam ut ex ex ut sit tempor ipsum ex nisi.', '2024-10-13 00:28:28.6852064', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('en-litt-annen-sti', 2, 'ZanyOctopus19', 4, 'Nisi elit dolore sit.', 'Minim commodo labore nostrud ex exercitation consequat lorem. dolor nisi lorem ullamco commodo et incididunt consectetur sed. adipiscing veniam labore consectetur ut adipiscing ut ullamco.', '2024-10-23 00:28:28.6852064', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('en-litt-annen-sti', 2, 'SneakyGiraffe947', 4, 'Ut sit.', 'Ad consequat consequat ad enim dolor minim ipsum aliquip. nostrud enim adipiscing. nostrud ut laboris nisi.', '2024-11-01 23:28:28.6852064', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('en-litt-annen-sti', 2, 'GreasyGiraffe579', 3, 'Lorem. ut veniam.', 'Adipiscing dolor magna ut et. lorem. consequat nisi et dolore nisi. tempor commodo.', '2024-11-11 23:28:28.6852064', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('en-litt-annen-sti', 2, 'JollyNoodle938', 3, 'Do aliquip adipiscing nisi.', 'Labore aliquip consectetur et aliquip. consectetur tempor enim ullamco sit do nisi nostrud ea elit sed commodo sed ipsum nostrud. incididunt ut veniam.', '2024-11-21 23:28:28.6852064', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('en-litt-annen-sti', 2, 'Dizzybun859', 5, 'Ex tempor nisi aliquip.', 'Veniam ea laboris. nisi. labore aliquip consequat labore ut nostrud. ut ullamco ullamco.', '2024-12-01 23:28:28.6852064', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('en-litt-annen-sti', 1, 'ClumsyPenguin936', 2, 'Sit. quis veniam ut.', 'Do labore tempor consequat. adipiscing do incididunt. et minim veniam laboris veniam exercitation ut magna quis sed incididunt. elit enim. elit eiusmod. ut consequat minim. magna.', '2024-12-11 23:28:28.6852064', 2, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('en-litt-annen-sti', 1, 'SaltyDragon362', 4, 'Do magna labore.', 'Consectetur. ullamco dolore et ea. dolore sit nostrud. sit amet quis exercitation ut consectetur eiusmod.', '2024-12-21 23:28:28.6852064', 2, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('en-litt-annen-sti', 1, 'BouncyWaffle200', 4, 'Magna et adipiscing. et.', 'Do. quis. ipsum ut elit ut do aliqua aliqua exercitation magna. ut sed exercitation consequat aliqua ad. minim lorem ipsum aliquip labore ut ut ad ut. do.', '2024-12-31 23:28:28.6852064', 3, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('en-litt-annen-sti', 1, 'GigglyGiraffe444', 2, 'Minim aliqua.', 'Consequat sed exercitation ea adipiscing nostrud ut tempor. consectetur ea consectetur aliqua. labore commodo aliquip dolore adipiscing. incididunt eiusmod. eiusmod ipsum. do dolore nisi adipiscing.', '2025-01-10 23:28:28.6852064', 3, 0, 0);

        -- Reviews for /product/2
        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/2', 1, 'GrumpyPineapple929', 1, 'Ipsum minim elit.', 'Quis ex do dolore sed ut. minim incididunt commodo lorem dolor quis commodo minim. sit dolor.', '2025-01-20 23:28:28.6852064', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/2', 3, 'SillyGoblin720', 4, 'Ipsum dolor.', 'Commodo amet nostrud incididunt elit quis ut nostrud et labore ut. incididunt do lorem ut laboris et magna.', '2025-01-30 23:28:28.6852064', 1, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/2', 1, 'SpicyOctopus352', 5, 'Labore ullamco sit ex.', 'Ex et ipsum dolore aliqua ex exercitation exercitation ea ad laboris quis enim quis nisi nisi elit exercitation exercitation.', '2025-02-09 23:28:28.6852064', 2, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/2', 1, 'SneakyPotato403', 5, 'Elit commodo lorem.', 'Sit consequat elit enim amet elit sed. magna sit dolor consequat sed incididunt.', '2025-02-19 23:28:28.6852064', 2, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/2', 1, 'ZanyGoblin924', 4, 'Amet. aliqua ipsum aliqua.', 'Exercitation. ipsum exercitation sed ea aliquip ex do nisi consequat aliquip ea consectetur minim. consectetur labore.', '2025-03-01 23:28:28.6852064', 3, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/2', 1, 'Greasybun806', 3, 'Sed ut.', 'Ad ipsum ad magna amet commodo do. commodo dolor dolor. labore lorem dolor laboris sed.', '2025-03-11 23:28:28.6852064', 3, 0, 0);

        INSERT INTO Review(external_id, author_id, author_name, score, title, comment, created_at, status, like_count, dislike_count)
        VALUES ('/product/2', 1, 'SpicyPenguin56', 2, 'Minim nisi lorem.', 'Consequat exercitation ipsum ad ut minim ut. ut ut dolore sed dolore aliquip ut exercitation nisi ut sit ut consectetur.', '2025-03-21 23:28:28.6852064', 3, 0, 0);
        
        INSERT INTO review_settings (external_id, enable_listing, enable_submit) 
        VALUES('/visning-disabled', false, true);
        INSERT INTO review_settings (external_id, enable_listing, enable_submit) 
        VALUES('/ny-omtale-disabled', true, false);        
        """;

        Functional.ThrowingRunnable fn = () -> {
            Logger.withScope("Trying real fast insertion", () -> {
                try (var _2 = new TryWithTimer("Real fast insertion with single SQL statement")) {
                    DataSourceManager.with(conn -> {
                        try (var stmt = conn.createStatement()) {
                            stmt.execute(sql);
                        }
                        return null;
                    });
                }
            });
        };

        SchemaContext.scopeSchema("public", fn);
    }
}
