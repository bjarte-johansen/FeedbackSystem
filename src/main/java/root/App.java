package root;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import root.app.AppContext;

import root.app.AppRequestSchema;
import root.includes.logger.Logger;
import root.repositories.ReviewRepository;
import root.repositories.TenantRepository;
import root.services.DatabaseService;

import java.sql.Connection;
import java.util.*;


@SpringBootApplication
public class App implements CommandLineRunner {
    //private static AppContext appContext;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(App.class);
        app.setDefaultProperties(Map.of(
            "spring.profiles.active", "test"
        ));

        app.run(args);
    }

    @Override
    @Order(0)
    public void run(String... args) throws Exception {
        AppContext.getSingleton();

        Logger.log();
    }

    public static void initAppContext() {

    }

    @Bean
    @Order(0)
    CommandLineRunner startup(ReviewRepository reviewRep, DatabaseService databaseService, TenantRepository tenantRepo, ReviewRepository reviewRepository) throws Exception {

        return (args) -> {
            Logger.log("running app startup tasks...");

            // Initialize AppContext singleton (e.g. to initialize database connection pool and other shared resources)
            AppContext.getSingleton();
            AppRequestSchema.set("test");

            databaseService.executeDatabasePatches();
            databaseService.resetDemoData();

            AppRequestSchema.remove();;

            //WhereQueryBuilder.test();
        };
    }



    @FunctionalInterface
    public interface ConnectionStatementRunnable{
        void run(Connection c, java.sql.Statement st) throws Exception;
    }

    @FunctionalInterface
    public interface ConnectionStatementSchemaRunnable{
        void run(Connection c, java.sql.Statement st, String schema) throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingRunnable{
        void run() throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T>{
        void run(T t) throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T>{
        T run() throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingFunction<T, R>{
        R run(T t) throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingBiFunction<T, U, R>{
        R run(T t, U u) throws Exception;
    }

    /*
    public static void logExec(ThrowingRunnable runnable, String title) throws Exception {
        try(var _ = Logger.scope(title != null ? title : "{unnamed block}")) {
            runnable.run();
        }
    }
    */
}