package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import root.includes.context.SchemaContext;
import root.database.DataSourceManager;
import root.includes.Functional;
import root.includes.logger.Logger;
import root.models.review.Review;
import root.models.tenant.Tenant;
import root.repositories.tenant.TenantRepository;

import java.util.List;

@Service
@DependsOn("appContext")
public class DatabaseService {
    @Autowired
    TenantRepository tenantRepo;

    @Autowired
    DatabaseDemoDataService databaseDemoDataService;

    public void resetDemoData() {
        Logger.withScope("resetDemoData", () -> {
            // reset public schema, this will cascade and reset tenant and tenant_domain tables as well
            // is logged internally
            databaseDemoDataService.resetPublicSchema();

            Logger.withScope("Resetting tenant schemas...", () -> {
                SchemaContext.scopeSchema("public", () -> {
                    tenantRepo.findAll().forEach(tenant -> {
                        SchemaContext.scopeSchema(
                            tenant.getSchemaName(),
                            databaseDemoDataService::resetTenantSchema
                        );
                    });
                });
            });
        });
    }

    public void executeDatabasePatches() throws Exception {
        // create lambda patcher
        Functional.ConnectionStatementRunnable patchAddReviewVoteTable = (_1, st) -> {
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
        Functional.ConnectionStatementRunnable patchAddStatusFieldForReview = (_1, st) -> {
            st.execute("ALTER TABLE review ADD COLUMN IF NOT EXISTS status SMALLINT NOT NULL DEFAULT 0");
            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status ON review (status)");
        };

        // create lambda patcher
        Functional.ConnectionStatementRunnable patchAddStatusIndexDescSortedForReview = (_1, st) -> {
            // drop old indexes if they exist, we will replace them with new ones that are sorted by id desc and filtered by status
            st.execute("DROP INDEX IF EXISTS idx_review_status_1_created;");
            st.execute("DROP INDEX IF EXISTS idx_review_status_equals_1;");

            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status_approved ON review(id DESC) WHERE status = " + Review.REVIEW_STATUS_APPROVED);
            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status_pending ON review(id DESC) WHERE status = " + Review.REVIEW_STATUS_PENDING);
            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status_rejected ON review(id DESC) WHERE status = " + Review.REVIEW_STATUS_REJECTED);
        };

        List<Functional.ConnectionStatementRunnable> patchers = List.of(
            patchAddReviewVoteTable,
            patchAddStatusFieldForReview,
            patchAddStatusIndexDescSortedForReview
        );

        // return lambda executor that runs the patchers
        // TODO: bug here, its implemented twice that we do the test schema for some reason, need to investigate and fix this
        SchemaContext.scopeSchema("public", () -> {
            for (var tenant : tenantRepo.findAll()) {
                patchTenant(tenant, patchers);
            }
        });
    }

    private void patchTenant(Tenant tenant, List<Functional.ConnectionStatementRunnable> patchers) throws Exception {
        // apply patch for relevant schema
        SchemaContext.scopeSchema(tenant.getSchemaName(), () -> {
            try (var conn = DataSourceManager.getConnection()) {
                try (var st = conn.createStatement()) {
                    for (var patcher : patchers)
                        patcher.run(conn, st);
                }
            }
        });
    }
}
