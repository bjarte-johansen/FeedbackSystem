package root.database;

import java.sql.*;

public class DB implements AutoCloseable{
    private static Connection conn;

    public static Connection getConnection(){
        try {
            if(conn == null || conn.isClosed()) {
                conn = DataSource.getConnection();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return conn;
    }

    @Override
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            conn = null;
        }
    }
/*
    public static NamedSql.Parsed prepareSql(String sql, Map<String, Object> params, Object... args) {
        NamedSql.Parsed parsed = NamedSql.parse(sql, params, args);
        return parsed;
    }
 */

    public static void printMetaData() throws Exception {
        try (Connection connection = getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
