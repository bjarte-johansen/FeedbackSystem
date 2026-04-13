package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.App;
import root.app.AppRequestSchema;
import root.database.DataSourceManager;
import root.DatabaseManager;
import root.includes.logger.Logger;
import root.models.Review;
import root.models.Tenant;
import root.repositories.TenantRepository;

import java.util.List;

@Service
public class DatabaseService {
    @Autowired
    TenantRepository tenantRepo;

    @Autowired
    DatabaseManager databaseManager;

    public void migrateTenantRelatedToPublic(){
        /*
        CREATE TABLE public.tenant
            (LIKE test.tenant INCLUDING ALL);

        INSERT INTO public.tenant
        SELECT * FROM test.tenant;

        -- tenant_domain
        CREATE TABLE public.tenant_domain
            (LIKE test.tenant_domain INCLUDING ALL);

        INSERT INTO public.tenant_domain
        SELECT * FROM test.tenant_domain;

        SELECT setval(
            pg_get_serial_sequence('public.tenant','id'),
            (SELECT MAX(id) FROM public.tenant)
        );

        SELECT setval(
            pg_get_serial_sequence('public.tenant_domain','id'),
            (SELECT MAX(id) FROM public.tenant_domain)
        );

        TRUNCATE public.tenant, public.tenant_domain RESTART IDENTITY CASCADE;

        INSERT INTO public.tenant
        SELECT * FROM test.tenant;

        INSERT INTO public.tenant_domain
        SELECT * FROM test.tenant_domain;
        */
    }

    public void resetDemoData() {
        // reset public schema, this will cascade and reset tenant and tenant_domain tables as well
        Logger.log("Resetting public schema (clean + insert tenants + tenant domains)...");
        databaseManager.resetPublicSchema();
        Logger.log("Public schema reset OK");


        Logger.log("Resetting tenant schemas...");
        try(var _ = AppRequestSchema.withThreadSchema("public")) {
            for (var tenant : tenantRepo.findAll()) {

                Logger.log("Resetting tenant schema for tenant: " + tenant.getName() + " (" + tenant.getSchemaName() + ")");
                try (var _ = AppRequestSchema.withThreadSchema(tenant.getSchemaName())) {
                    databaseManager.resetTenantSchema();
                }
            }
        }
        Logger.log("Resetting tenant schemas OK");
    }

    public void executeDatabasePatches() throws Exception {
        // create lambda patcher
        App.ConnectionStatementRunnable patchAddReviewVoteTable = (_, st) -> {
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
        App.ConnectionStatementRunnable patchAddStatusFieldForReview = (_, st) -> {
            st.execute("ALTER TABLE review ADD COLUMN IF NOT EXISTS status SMALLINT NOT NULL DEFAULT 0");
            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status ON review (status)");
        };

        // create lambda patcher
        App.ConnectionStatementRunnable patchAddStatusIndexDescSortedForReview = (_, st) -> {
            // drop old indexes if they exist, we will replace them with new ones that are sorted by id desc and filtered by status
            st.execute("DROP INDEX IF EXISTS idx_review_status_1_created;");
            st.execute("DROP INDEX IF EXISTS idx_review_status_equals_1;");

            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status_approved ON review(id DESC) WHERE status = " + Review.REVIEW_STATUS_APPROVED);
            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status_pending ON review(id DESC) WHERE status = " + Review.REVIEW_STATUS_PENDING);
            st.execute("CREATE INDEX IF NOT EXISTS idx_review_status_rejected ON review(id DESC) WHERE status = " + Review.REVIEW_STATUS_REJECTED);
        };

        List<App.ConnectionStatementRunnable> patchers = List.of(
            patchAddReviewVoteTable,
            patchAddStatusFieldForReview,
            patchAddStatusIndexDescSortedForReview
        );

        // return lambda executor that runs the patchers
        // TODO: bug here, its implemented twice that we do the test schema for some reason, need to investigate and fix this
        try (var ignore1 = AppRequestSchema.withThreadSchema("public")) {
            for (var tenant : tenantRepo.findAll()) {
                patchTenant(tenant, patchers);
            }
        }
    }

    private void patchTenant(Tenant tenant, List<App.ConnectionStatementRunnable> patchers) throws Exception {
        try (var ignore = AppRequestSchema.withThreadSchema(tenant.getSchemaName())) {
            try (var conn = DataSourceManager.getConnection()) {
                try (var st = conn.createStatement()) {
                    for (var patcher : patchers)
                        patcher.run(conn, st);
                }
            }
        }
    }
}
