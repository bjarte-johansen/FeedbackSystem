package root.database;

import root.logger.Logger;

import java.sql.*;
import java.util.function.Consumer;

public class DB {
    public interface ConnectionConsumer<R>{
        R run(Connection connection) throws Exception;
    }

    public static <R> R with(ConnectionConsumer<R> fn) {
        try (Connection connection = DataSource.getConnection()) {
            return fn.run(connection);
        } catch (Exception e) {
            Logger.log("An exception occurred while executing a database operation: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

/*
    private static volatile String QUOTE = null;

    public static String getQuoteSymbol(){
        String q = QUOTE;
        if(q != null) return q;

        synchronized (DB.class) {
            if(QUOTE == null){
                try {
                    with(conn -> {
                        QUOTE = conn.getMetaData().getIdentifierQuoteString().trim();
                        return null;
                    });
                } catch (Exception e) {
                    throw new RuntimeException("Failed to get identifier quote string from database metadata.");
                }
            }
            return QUOTE;
        }
    }
*/
    /*
    public static String quoteIdentifier(String s) {
        String q = getQuoteSymbol();
        if(q.isEmpty()) return s;

        return q + s + q;
    }
     */
/*
    public static Connection getConnection(){
        try {
            //Logger.log("CHANGE getConnection ENV TO PROD/TEXT BASED ON ENV SETTINGS");

            return DataSource.getConnection(DataSource.TEST);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
*/

/*
    public static NamedSql.Parsed prepareSql(String sql, Map<String, Object> params, Object... args) {
        NamedSql.Parsed parsed = NamedSql.parse(sql, params, args);
        return parsed;
    }
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
