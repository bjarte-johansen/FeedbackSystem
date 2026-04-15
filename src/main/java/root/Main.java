package root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import root.app.AppConfig;
import root.app.AppContext;
import root.database.*;
import root.includes.logger.Logger;
import root.models.Review;
import root.includes.proxyrepo.RepositoryProxyConstructor;
import root.includes.quicktests.FantasyRepoTest;
import root.repositories.TenantRepository;



@Configuration
@ComponentScan("root")
@Service
public class Main {
    public static boolean PRINT_META_DATA_AT_STARTUP = false;

    @Autowired
    AppContext appContext;

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

        try(var _ = Logger.scope("Testing Logger...")) {
            Logger.log("This is a log message.");
            Logger.warn("This is a warning message.");
            Logger.error("This is an error message.");
            Logger.tab(1).log("This is a tab message.");
            Logger.caller(4).log("This is the caller functions information.");
        }

        Logger.log("try logger().log('something').enter()/leave()");
        Logger.log("a").enter();
        Logger.log("a.a");
        Logger.log("a.b");
        Logger.log("a.c").enter();
        Logger.leave().log("a.c.a");
        Logger.leave().log("a.c");
        Logger.log("a");
    }

    CommandLineRunner run() {
        return args -> {
            //appContext = AppContext.getSingleton();
            appContext.initSingleTenantConnectionProvider(
                new CustomDataSource(AppConfig.CURRENT_CONNECTION_PARAMS)
            );
        };
    }

    public static void main(String[] args) throws Throwable{
        var ctx = new AnnotationConfigApplicationContext(Main.class);

        //var bean = ctx.getBean(FantasyRepository.class); // @Autowired works


/*
        FantasyRepoTest fantasyRepoTest = ctx.getBean(FantasyRepoTest.class);
        fantasyRepoTest.run();
*/

        testLogger();

        if(true) return;


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