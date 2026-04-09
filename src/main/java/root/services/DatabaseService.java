package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.App;
import root.app.AppRequestSchema;
import root.database.DataSourceManager;
import root.DatabaseManager;
import root.models.Review;
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
        App.ConnectionStatementRunnable patchAddREviewVoteTable = (c, st) -> {
            st.execute("""
CREATE TABLE IF NOT EXISTS review_vote (
	id		   BIGSERIAL PRIMARY KEY,
    review_id  BIGINT NOT NULL,
    session_id VARCHAR(64) NOT NULL,
    ip         INET NOT NULL,
    vote       SMALLINT NOT NULL,
    created_at TIMESTAMP DEFAULT now(),

    UNIQUE  (review_id, session_id)
);""");
        };

        // create lambda patcher
        App.ConnectionStatementRunnable patchAddStatusFieldForReview = (c, st) -> {
            st.execute("ALTER TABLE review ADD COLUMN IF NOT EXISTS status SMALLINT NOT NULL DEFAULT 0");
            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status ON review (status)");
        };

        // create lambda patcher
        App.ConnectionStatementRunnable patchAddStatusIndexDescSortedForReview = (c, st) -> {
            // drop old indexes if they exist, we will replace them with new ones that are sorted by id desc and filtered by status
            st.execute("DROP INDEX IF EXISTS idx_review_status_1_created;");
            st.execute("DROP INDEX IF EXISTS idx_review_status_equals_1;");

            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status_approved ON review(id DESC) WHERE status = " + Review.REVIEW_STATUS_APPROVED);
            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status_pending ON review(id DESC) WHERE status = " + Review.REVIEW_STATUS_PENDING);
            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status_rejected ON review(id DESC) WHERE status = " + Review.REVIEW_STATUS_REJECTED);
        };

        List<App.ConnectionStatementRunnable> patchers = List.of(
            patchAddREviewVoteTable,
            patchAddStatusFieldForReview,
            patchAddStatusIndexDescSortedForReview
        );

        // return lambda executer that runs the patchers

        for (var tenant : tenantRepo.findAll()) {
            // TODO: bug here, its implemented twice that we do the test schema for some reason, need to investigate and fix this
            try (var _2 = AppRequestSchema.withThreadSchema(tenant.getSchemaName())) {
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
