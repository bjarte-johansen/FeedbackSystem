package root.database;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface IColumnReader {
    Object read(ResultSet rs, int i) throws Exception;
}
