/**
 * This file is part of the "Where You At?" application (https://whereyouat.app).
 *
 * IMPORTANT: To enable localhost development profile, add the following line to run config VM options:
 *      -Dspring.profiles.active=local to VM options (run configuration)
 * IMPORTANT: to enable any schema, set the schema in AppRequestSchema before any database access is made, for example
 * by adding the following line to the beginning of the startup method:
 * */


package root;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.sql.Connection;
import java.util.*;


@SpringBootApplication
public class App extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.setDefaultProperties(Map.of(
            "spring.profiles.active", "local"
        ));

        app.run(args);
    }

    // used when deployed as WAR in Tomcat
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(App.class);
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