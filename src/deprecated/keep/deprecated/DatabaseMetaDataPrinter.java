package root.database.deprecated;


import java.sql.*;

@Deprecated
public class DatabaseMetaDataPrinter {


    /**
     * Prints database metadata information to the console, including product name, version, driver details,
     * and table/column information.
     * @throws Exception
     */
/*
    public static void printMetaData() throws Exception {
        DataSource.with(conn -> {
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

 */
}
