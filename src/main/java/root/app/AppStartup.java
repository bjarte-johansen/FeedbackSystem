package root.app;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import root.App;
import root.includes.logger.Logger;
import root.repositories.TenantRepository;
import root.services.DatabaseService;

/**
 * This class runs on application startup and is used to run any setup tasks, such as database patching and demo data
 * resetting. It can also be used to run any test code that should be executed on startup.
 * <p>
 * It is done BEFORE any routes can be accessed, so it is a good place to run any setup code that should be executed
 * before the application is fully up and running.
 */

@Component
class AppStartup implements ApplicationRunner {
    private final DatabaseService databaseService;

    public AppStartup(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        Logger.log("running app startup tasks...");

        // test code
        //EmailVerificationCodeSender.send("bjartej@hotmail.com", VerificationCodeDigitsGenerator.generate(6));

        // patch database
        //databaseService.executeDatabasePatches();

        // reset demo data
        databaseService.resetDemoData();

        Logger.log("app startup tasks OK");
    }
}
