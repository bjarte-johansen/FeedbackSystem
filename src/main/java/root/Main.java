package root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import root.app.AppContext;
import root.database.*;
import root.logger.*;
import root.models.Review;
import root.quicktests.FantasyRepoTest;
import root.repofun.FantasyRepository;
import root.repositories.TenantRepository;


@Configuration
@ComponentScan("root")
class AppConfigMain {}


@Service
public class Main {
    public static boolean PRINT_META_DATA_AT_STARTUP = false;

    private static AppContext appContext;

    @Autowired
    FantasyRepoTest fantasyRepoTest;


    //
    public static void printWarnings(){
        System.out.println("Project warnings:");
        System.out.println("jsonb support is not implemented yet and will be added in the future. For now, you can use the JdbcReviewRepository directly for testing purposes.");
        System.out.println();
    }

    public static void testLogger(){
        try(var ignore = Logger.scope(
            "myBlock",
            (title, depth) -> System.out.println("# enter scope: " + title + ", depth: " + depth),
            (title, depth) -> System.out.println("# exit scope: " + title + ", depth: " + depth + "\n")
        )) {
            Logger.log("This is a log message.");
            Logger.warn("This is a warning message.");
            Logger.error("This is an error message.");
            Logger.tab(1).log("This is a tab message.");
            Logger.caller(4).log("This is the caller functions information.");
        }

        try(var ignore = Logger.scope("Testing Logger...")) {
            Logger.log("This is a log message.");
            Logger.warn("This is a warning message.");
            Logger.error("This is an error message.");
            Logger.tab(1).log("This is a tab message.");
            Logger.caller(4).log("This is the caller functions information.");
        }
    }

    public static void main(String[] args) throws Throwable{
        var ctx = new AnnotationConfigApplicationContext(AppConfigMain.class);

        //var bean = ctx.getBean(FantasyRepository.class); // @Autowired works

        appContext = new AppContext();
        appContext.initSingleTenantConnectionProvider();

        FantasyRepoTest fantasyRepoTest = ctx.getBean(FantasyRepoTest.class);
        fantasyRepoTest.run();

        if(true) return;

        /*
        testLogger();
        if(true) return;
         */


        /*
        if(PRINT_META_DATA_AT_STARTUP)
            DatabaseMetaDataPrinter.printMetaData();

         */



        EntityMeta meta = EntityMeta.create(Review.class);


        Logger.log("--------------------------------------------------------------------");;
        try(var ignore = Logger.scope("Printing EntityMeta for Review...")) {
            Logger.log("EntityMeta for Review:");
            Logger.log("Meta.toString(), " + meta.toString());
            Logger.log("-".repeat(20));
            Logger.log("--------------------------------------------------------------------");;
        }

        // TODO: DO NOT REMOVE
        printWarnings();



        var tenantRepo = RepositoryProxyConstructor.create(TenantRepository.class);
        var l = tenantRepo.findAll();
        l.forEach(System.out::println);
    }
}