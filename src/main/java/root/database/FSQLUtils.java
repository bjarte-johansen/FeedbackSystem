package root.database;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FSQLUtils {

    public static IColumnWriter createColumnWriter(Class<?> t) throws Exception {
        return (f, o, v) -> ((Field) f).set(o, v);
    }

    public static IColumnReader createColumnReader(Class<?> t) throws SQLException {

        // hottest first
        if (t == int.class)         return ResultSet::getInt;
        if (t == long.class)        return ResultSet::getLong;
        if (t == double.class)      return ResultSet::getDouble;
        if (t == boolean.class)     return ResultSet::getBoolean;

        // string
        if (t == String.class)      return ResultSet::getString;

        // boxed
        if (t == Integer.class)     return ResultSet::getInt;
        if (t == Long.class)        return ResultSet::getLong;
        if (t == Double.class)      return ResultSet::getDouble;
        if (t == Boolean.class)     return ResultSet::getBoolean;

        // time
        if (t == java.time.Instant.class)
            return (rs, i) -> rs.getObject(i, java.sql.Timestamp.class).toInstant();

        if (t == java.time.LocalDate.class)
            return (rs, i) -> rs.getObject(i, java.time.LocalDate.class);

        if (t == java.time.LocalTime.class)
            return (rs, i) -> rs.getObject(i, java.time.LocalTime.class);

        // fallback
        return ResultSet::getObject;
    }
}
