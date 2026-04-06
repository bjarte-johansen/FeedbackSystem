package root.database.deprecated;

import java.sql.ResultSet;
import java.sql.SQLException;

@Deprecated
interface IVoidRowMapper {
    void map(ResultSet rs) throws SQLException;
}
