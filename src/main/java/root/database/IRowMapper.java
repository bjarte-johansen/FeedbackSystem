package root.database;

import java.sql.ResultSet;
import java.sql.SQLException;

@Deprecated
public interface IRowMapper<T> {
    T map(ResultSet rs) throws SQLException;
}
