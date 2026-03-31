package root.database;

import root.logger.Logger;

import java.sql.*;
import java.util.function.Consumer;

public class DB {
    /**
     * Functional interface representing a consumer that accepts a database connection and returns a result. This is used
     * as a parameter type for the with() method to allow executing database operations with a managed connection.
     * @param <R>
     */

    public interface ConnectionConsumer<R>{
        R run(Connection connection) throws Exception;
    }


    /**
     * Utility method to execute a database operation with a managed connection. The provided function is executed
     * with a connection that is automatically closed after the operation completes, ensuring proper resource management.
     * @param fn
     * @return
     * @param <R>
     */

    public static <R> R with(ConnectionConsumer<R> fn) {
        try (Connection connection = DataSource.getConnection()) {
            return fn.run(connection);
        } catch (Exception e) {
            Logger.log("An exception occurred while executing a database operation: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    /**
     * Prints database metadata information to the console, including product name, version, driver details,
     * and table/column information.
     * @throws Exception
     */

    public static void printMetaData() throws Exception {
        DB.with(conn -> {
            DatabaseMetaData metaData = conn.getMetaData();

            System.out.println("Database Product Name: " + metaData.getDatabaseProductName());
            System.out.println("Database Product Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Driver Name: " + metaData.getDriverName());
            System.out.println("Driver Version: " + metaData.getDriverVersion());
            System.out.println();

            try (ResultSet rsTables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (rsTables.next()) {
                    String tableName = rsTables.getString("TABLE_NAME");
                    System.out.println("Table: " + tableName);

                    var rsCols = metaData.getColumns(null, null, tableName, "%");
                    while (rsCols.next()) {
                        String colName = rsCols.getString("COLUMN_NAME");
                        String colType = rsCols.getString("TYPE_NAME");
                        System.out.println("\tColumn: " + colName + ", Type: " + colType);
                    }
                    System.out.println();
                }

                System.out.println();
            }

            return null;
        });
    }
}
