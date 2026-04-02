package root;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import root.app.AppConfig;
import root.app.AppContext;
import root.app.AppRequestContext;
import root.app.AppTextBanner;
import root.database.CustomDataSource;
import root.database.DataSourceManager;
import root.quicktests.DatabaseManager;

import root.logger.Logger;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SpringBootApplication
public class App{
    private static AppContext appContext;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(App.class);
        app.setDefaultProperties(Map.of(
            "spring.profiles.active", "test"
        ));

        app.run(args);
    }

    public static void initAppContext() {
        if(appContext != null) {
            Logger.warn("AppContext is already initialized, skipping re-initialization");
            return;
        }
        appContext = new AppContext();
    }

    @FunctionalInterface
    public interface DatabasePatcherFunction{
        void run(Connection c, java.sql.Statement st, String schema) throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingRunnable{
        void run() throws Exception;
    }

    public static void logExec(ThrowingRunnable runnable, String title) throws Exception {
        try(var _ = Logger.scope(title != null ? title : "{unnamed block}")) {
            runnable.run();
        }
    }

    @Bean
    @Order(0)
    CommandLineRunner startup(){

        return (args) -> {
            Logger.log("running app startup tasks...");

            initAppContext();
        };
    }


    @Bean
    @Order(1)
    CommandLineRunner dbPatcher(DatabaseManager databaseManager) throws SQLException, Exception {
        // create lambda patcher
        DatabasePatcherFunction patchAddStatusFieldForReview = (c, st, schema) -> {
            st.execute("ALTER TABLE review ADD COLUMN IF NOT EXISTS status SMALLINT NOT NULL DEFAULT 0");
            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status ON review (status)");
        };

        // create lambda patcher
        DatabasePatcherFunction patchAddStatusIndexDescSortedForReview = (c, st, schema) -> {
            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status_1_created ON review(created_at DESC) WHERE status = 1");
        };

        // return lambda executer that runs the patchers
        return args -> {
            logExec(() -> {
                // TODO: bug here, its implemented twice that we do the test schema for some reason, need to investigate and fix this

                // for(var schema : schemaList) {
                try(var ignore = AppRequestContext.withTenantSchemaForThread("test")) {
                    try (var conn = DataSourceManager.getConnection()) {
                        patchAddStatusFieldForReview.run(conn, conn.createStatement(), "test");
                        patchAddStatusIndexDescSortedForReview.run(conn, conn.createStatement(), "test");
                    }

                    // reset demo data on startup
                    databaseManager.resetDemoData();

                }
                // }
            },
            "Database Patching"
            );
        };
    }
}