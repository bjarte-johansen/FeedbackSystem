/**
 * This file is part of the "Where You At?" application (https://whereyouat.app).
 *
 * IMPORTANT: To enable localhost development profile, add the following line to run config VM options:
 *      -Dspring.profiles.active=local to VM options (run configuration)
 * IMPORTANT: to enable any schema, set the schema in AppRequestSchema before any database access is made, for example
 * by adding the following line to the beginning of the startup method:
 * */


package root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import root.app.AppConfig;
import root.app.AppContext;

import root.app.AppRequestSchema;
import root.includes.EmailVerificationCodeSender;
import root.includes.VerificationCodeDigitsGenerator;
import root.includes.logger.Logger;
import root.repositories.ReviewRepository;
import root.repositories.TenantRepository;
import root.services.DatabaseService;

import java.sql.Connection;
import java.util.*;


@SpringBootApplication
public class App implements CommandLineRunner {
    @Autowired
    AppContext appContext;

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
        Logger.log("Application started with args: " + Arrays.toString(args));
    }


    @Bean
    @Order(0)
    CommandLineRunner startup(ReviewRepository reviewRep, DatabaseService databaseService, TenantRepository tenantRepo, ReviewRepository reviewRepository) throws Exception {
        return (args) -> {
            Logger.log("running app startup tasks...");

            // test code
            //EmailVerificationCodeSender.send("bjartej@hotmail.com", VerificationCodeDigitsGenerator.generate(6));

            // override configs for testing purposes, in production these would be set by the environment or
            // application properties
            AppConfig.OVERRIDE_TENANT = true;
            AppConfig.OVERRIDE_TENANT_SCHEMA = "test";
            AppConfig.OVERRIDE_TENANT_ID = 1;

            // set default schema for setup
            AppRequestSchema.set("test");

            // patch database
            databaseService.executeDatabasePatches();

            // reset demo data
            databaseService.resetDemoData();

            AppRequestSchema.remove();

            // old test
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