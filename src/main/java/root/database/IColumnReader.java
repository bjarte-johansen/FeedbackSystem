package root.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IColumnReader {
    Object read(ResultSet rs, int i) throws Exception;
}
