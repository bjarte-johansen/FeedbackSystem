package root;

import root.logger.Logger;
import root.logger.LoggerScope;
import root.repofun.Fantasy;
import root.repofun.FantasyRepoCustom;
import root.repofun.FantasyRepoCustomImpl;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FantasyRepoTest {
    public static void report(String action, List<?> entities) {
        Logger.logf("%s: found list of %d entities", action, entities.size());

        entities.forEach(e -> Logger.tab().log(e));
    }

    public static void report(String action, boolean found) {
        String title = action + ": found boolean";
        Logger.log(action);
        Logger.tab().log(found ? "true" : "false");
    }

    public static void report(String action, int number) {
        String title = action + ": found number " + number;
        Logger.log(action);
        Logger.tab().log(number);
    }

    public static void report(String action, Object entity) {
        String title = action + ": found object";
        Logger.log(action);
        Logger.tab().log(entity);
    }

    public static void run(){

        FantasyRepoCustom repo = ProxyRepositoryFactory.createFantasyRepo(
            new FantasyRepoCustomImpl(),
            Map.of(
                "tableName", "fantasy",
                "modelClass", Fantasy.class
            )
        );

        testByMethodOverride(repo);
        testFindByTextEquals(repo);
        testRepoFindById(repo);
        testRepoCount(repo);
        testFindAll(repo);


        testFindByEmailLike(repo);
        testFindById(repo);
        testCountByEmailLike(repo);
        testCountByEmailLikeAndAge(repo);
        testExistsByEmail(repo);
        testDeleteById(repo);
    }

    private static void testByMethodOverride(FantasyRepoCustom repo) {
        try(var __ = Logger.scope("repo.sayHello")) {
            repo.sayHello();
        }
    }

    private static void testFindByTextEquals(FantasyRepoCustom repo) {
        try(var __ = Logger.scope("repo.findByTextEquals")){
            var found = repo.findByTextEquals("test");
            report("found", found);
        }
    }

    private static void testRepoFindById(FantasyRepoCustom repo) {
        try(var __ = Logger.scope("repo.findById")) {
            var found = repo.findById(1L);
            Logger.log("Entities found: " + found);
        }
    }

    private static void testRepoCount(FantasyRepoCustom repo) {
        try (var __ = Logger.scope("repo.count")) {
            var found = repo.count();
            Logger.log("Entities counted: " + found);
        }
    }

    private static void testFindAll(FantasyRepoCustom repo) {
        try(var __ = Logger.scope("repo.findAll")) {
            var all = repo.findAll();
            report("findAll", all);
        }
    }

    private static void testFindByEmailLike(FantasyRepoCustom repo) {
        try(var __ = Logger.scope("repo.findByEmailLike")) {
            Logger.log("Deleting any existing entries with email like %@onlyone.com to ensure clean test environment...");

            List<Fantasy> list = repo.findByEmailLike("%@onlyone.com");
            list.forEach(repo::delete);
        }
    }

    private static void testFindById(FantasyRepoCustom repo) {
        try (var scope = Logger.scope("repo.save")) {
            Fantasy f = new Fantasy();
            f.setText("test123");
            f.setScore(16);
            f.setEmail("testing@onlyone.com");
            f.setAge(32);
            f.setCreatedAt(Instant.now());
            Logger.log("Saving fantasy: ");
            Logger.tab().log(f);
            var saved = repo.save(f);

            var loaded = repo.findById(saved.getId());
            Logger.log("Saved fantasy: ");
            Logger.tab().log(loaded);
        }
    }

    private static void testCountByEmailLike(FantasyRepoCustom repo) {
        try(var scope = Logger.scope("repo.countByEmailLike")) {
            var found = repo.countByEmailLike("%@onlyone.com");
            report("findByEmailLike", found);
        }
    }

    private static void testCountByEmailLikeAndAge(FantasyRepoCustom repo) {
        try(var scope = Logger.scope("repo.countByEmailLikeAndAge (31)")) {
            var found = repo.countByEmailLikeAndAge("%@onlyone.com", 31);
            report("countByEmailLikeAndAge", found);
        }

        try(var scope = Logger.scope("repo.countByEmailLikeAndAge (someEmailVar, 32)")) {
            var found = repo.countByEmailLikeAndAge("%@onlyone.com", 32);
            report("countByEmailLikeAndAge", found);
        }
    }

    private static void testExistsByEmail(FantasyRepoCustom repo) {
        try (var scope = Logger.scope("repo.existsByEmail()")) {
            var found = repo.existsByEmailLike("%@test.com");
            report("existsByEmailLike", found);
        }
    }

    public static void testDeleteById(FantasyRepoCustom repo){
        try (var scope = Logger.scope("repo.deleteById")) {
            Fantasy f = new Fantasy();
            f.setText("abc");
            f.setScore(100);
            f.setEmail("should@notbeenseen.unlesswecrashed");
            f.setAge(100);
            f.setCreatedAt(Instant.now());
            Logger.log("Saving fantasy: ");
            Logger.tab().log(f);
            Fantasy saved = repo.save(f);

            //Fantasy f = new Fantasy();
            f.setText("abc");
            f.setScore(101);
            f.setEmail("should@notbeenseen.unlesswecrashed");
            f.setAge(101);
            f.setCreatedAt(Instant.now());
            Logger.log("Saving fantasy: ");
            Logger.tab().log(f);
            saved = repo.save(f);

            var found = repo.existsById(saved.getId());
            Logger.log("Exists fantasy: ");
            Logger.tab().log(found);

            var count = repo.countByEmailLike("should@notbeenseen.unlesswecrashed");
            Logger.log("Count fantasy where email is similar: ");
            Logger.tab().log(count);

            Logger.log("Attempting delete... ");
            repo.deleteById(f.getId());
            Logger.log("Checking result... ");

            found = repo.existsById(saved.getId());
            Logger.log("Exists fantasy: ");
            Logger.tab().log(found);
        }
    }
}
