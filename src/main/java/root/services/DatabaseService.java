package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.App;
import root.app.AppRequestSchema;
import root.database.DataSourceManager;
import root.logger.Logger;
import root.quicktests.DatabaseManager;
import root.repositories.TenantRepository;

import java.util.List;

@Service
public class DatabaseService {
    @Autowired
    TenantRepository tenantRepo;

    @Autowired
    DatabaseManager databaseManager;


    public void resetDemoData() throws Exception{
        databaseManager.resetDemoData();
    }

    public void executeDatabasePatches() throws Exception {
        // create lambda patcher
        App.ConnectionStatementRunnable patchAddStatusFieldForReview = (c, st) -> {
            st.execute("ALTER TABLE review ADD COLUMN IF NOT EXISTS status SMALLINT NOT NULL DEFAULT 0");
            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status ON review (status)");
        };

        // create lambda patcher
        App.ConnectionStatementRunnable patchAddStatusIndexDescSortedForReview = (c, st) -> {
            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status_1_created ON review(created_at DESC) WHERE status = 1");
        };

        List<App.ConnectionStatementRunnable> patchers = List.of(
            patchAddStatusFieldForReview,
            patchAddStatusIndexDescSortedForReview
        );

        // return lambda executer that runs the patchers

        for (var tenant : tenantRepo.findAll()) {
            String schema = "test"; // TODO: use tenant.getSchemaName();
            try(var _1 = Logger.scope("Patching schema " + schema)) {
                // TODO: bug here, its implemented twice that we do the test schema for some reason, need to investigate and fix this
                try (var _2 = AppRequestSchema.withThreadSchema("test")) {
                    try (var conn = DataSourceManager.getConnection()) {
                        try (var st = conn.createStatement()) {
                            for (var patcher : patchers)
                                patcher.run(conn, st);
                        }
                    }
                }
            }
        }
    }
}
