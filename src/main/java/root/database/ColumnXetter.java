package root.database;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.sql.ResultSet;

@Deprecated
class ColumnXetter {
    static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    static IColumnReader generateGetter(Class<?> t) {
        if (t == int.class) return ResultSet::getInt;
        if (t == long.class) return ResultSet::getLong;
        if (t == double.class) return ResultSet::getDouble;
        if (t == float.class) return ResultSet::getFloat;
        if (t == boolean.class) return ResultSet::getBoolean;
        if (t == byte.class) return ResultSet::getByte;
        if (t == short.class) return ResultSet::getShort;

        if (t == Integer.class) return (rs, i) -> rs.getObject(i, Integer.class);
        if (t == Long.class) return (rs, i) -> rs.getObject(i, Long.class);
        if (t == Double.class) return (rs, i) -> rs.getObject(i, Double.class);
        if (t == Float.class) return (rs, i) -> rs.getObject(i, Float.class);
        if (t == Boolean.class) return (rs, i) -> rs.getObject(i, Boolean.class);

        if (t == String.class) return ResultSet::getString;

        return ResultSet::getObject;
    }

    static IColumnWriter generateSetter(Field f) throws Throwable {
        f.setAccessible(true);

        MethodHandle setter = LOOKUP.unreflectSetter(f);
        return setter::invoke;
    }
}
