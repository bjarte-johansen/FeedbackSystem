package root.database.deprecated;

import java.sql.ResultSet;
import java.sql.SQLException;

@Deprecated
interface ITypedRowMapper<T> {
    T map(ResultSet rs) throws SQLException;
}

@Deprecated
interface IVoidRowMapper {
    void map(ResultSet rs) throws SQLException;
}
