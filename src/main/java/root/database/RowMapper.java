package root.database;

import java.sql.ResultSet;
import java.sql.SQLException;

interface RowMapper<T> {
    T map(ResultSet rs) throws SQLException;
}
