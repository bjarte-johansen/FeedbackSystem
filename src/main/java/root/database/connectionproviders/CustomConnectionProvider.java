package root.database.connectionproviders;

import root.database.CustomDataSource;
import java.sql.Connection;

/**
 * A simple connection pool implementation that manages a pool of database connections. It creates new connections on
 * demand up to a specified maximum limit and reuses them when they are returned to the pool.
 */

public class CustomConnectionProvider {
    protected final CustomDataSource ds;

    public CustomConnectionProvider(CustomDataSource ds) {
        this.ds = ds;
    }

    public Connection getConnection() throws Exception {
        return ds.getConnection();
    }

    public void releaseConnection(Connection conn) throws Exception {
        conn.close();

        // TODO: check that we returned it to pool by counting debug messages for logPoolMessage or similar,
        //  very simple
    }
}
