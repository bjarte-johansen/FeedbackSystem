package root.quicktests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.RepositoryProxyConstructor;
import root.logger.Logger;
import root.repofun.Fantasy;
import root.repofun.FantasyRepository;

import java.time.Instant;
import java.util.List;

@Component
public class FantasyRepoTest {
    @Autowired
    FantasyRepository repo;// = RepositoryProxyConstructor.create(FantasyRepository.class);

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

    public void run(){
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

    private void testByMethodOverride(FantasyRepository repo) {
        try(var __ = Logger.scope("repo.sayHello")) {
            repo.sayHello();
        }
    }

    private void testFindByTextEquals(FantasyRepository repo) {
        try(var __ = Logger.scope("repo.findByTextEquals")){
            var found = repo.findByTextEquals("test");
            report("found", found);
        }
    }

    private void testRepoFindById(FantasyRepository repo) {
        try(var __ = Logger.scope("repo.findById")) {
            var found = repo.findById(1L);
            Logger.log("Entities found: " + found);
        }
    }

    private void testRepoCount(FantasyRepository repo) {
        try (var __ = Logger.scope("repo.count")) {
            var found = repo.count();
            Logger.log("Entities counted: " + found);
        }
    }

    private void testFindAll(FantasyRepository repo) {
        try(var __ = Logger.scope("repo.findAll")) {
            var all = repo.findAll();
            report("findAll", all);
        }
    }

    private void testFindByEmailLike(FantasyRepository repo) {
        try(var __ = Logger.scope("repo.findByEmailLike")) {
            Logger.log("Deleting any existing entries with email like %@onlyone.com to ensure clean test environment...");

            List<Fantasy> list = repo.findByEmailLike("%@onlyone.com");
            list.forEach(repo::delete);
        }
    }

    private void testFindById(FantasyRepository repo) {
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

    private void testCountByEmailLike(FantasyRepository repo) {
        try(var scope = Logger.scope("repo.countByEmailLike")) {
            var found = repo.countByEmailLike("%@onlyone.com");
            report("findByEmailLike", found);
        }
    }

    private void testCountByEmailLikeAndAge(FantasyRepository repo) {
        try(var scope = Logger.scope("repo.countByEmailLikeAndAge (31)")) {
            var found = repo.countByEmailLikeAndAge("%@onlyone.com", 31);
            report("countByEmailLikeAndAge", found);
        }

        try(var scope = Logger.scope("repo.countByEmailLikeAndAge (someEmailVar, 32)")) {
            var found = repo.countByEmailLikeAndAge("%@onlyone.com", 32);
            report("countByEmailLikeAndAge", found);
        }
    }

    private void testExistsByEmail(FantasyRepository repo) {
        try (var scope = Logger.scope("repo.existsByEmail()")) {
            var found = repo.existsByEmailLike("%@test.com");
            report("existsByEmailLike", found);
        }
    }

    public void testDeleteById(FantasyRepository repo){
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
