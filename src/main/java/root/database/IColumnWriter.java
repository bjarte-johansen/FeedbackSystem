package root.database;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface IColumnWriter {
    void write(Field f, Object o, Object v) throws Exception;
}