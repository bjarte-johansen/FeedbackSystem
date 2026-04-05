package root.database;

import root.interfaces.HasId;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class FSQLUtils {
    private static final ConcurrentHashMap<Class<?>, MethodHandle> CACHED_ENTITY_ID_GETTER = new ConcurrentHashMap<>();

    private static Field findField(Class<?> c, String name) {
        for (Class<?> cur = c; cur != null; cur = cur.getSuperclass()) {
            try {
                Field f = cur.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException ignored) {}
        }

        throw new RuntimeException("Field '" + name + "' not found in " + c);
    }

    private static MethodHandle unreflectGetterOf(Field f) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(
                f.getDeclaringClass(),
                MethodHandles.lookup()
            );

            return lookup.unreflectGetter(f);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to unreflect getter for field " + f, e);
        }
    }



    public static Object getEntityId(Object entity, String idFieldName) throws Exception {
        Class<?> clazz = entity.getClass();

        MethodHandle mh = CACHED_ENTITY_ID_GETTER.computeIfAbsent(clazz, c -> {
            try {
                var idField = findField(c, idFieldName);
                return unreflectGetterOf(idField);
            } catch (Exception e) {
                throw new RuntimeException("Failed to get entity id getter for class " + c.getName(), e);
            }
        });

        try {
            return mh.invoke(entity);
        }catch(Throwable t) {
            throw new RuntimeException("Failed to get entity id for class " + clazz.getName(), t);
        }
    }

    /*
    public static void setEntityId(Object instance, String idField, Long id) {
        try {
            Class<?> clazz = instance.getClass();
            var field = clazz.getDeclaredField(idField);
            field.setAccessible(true);
            field.set(instance, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set ID field: " + idField, e);
        }
    }
    */

    public static void setEntityId(Object entity, Object id, String idFieldName) throws Exception {
        if(entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        if(!(id instanceof Number)) {
            throw new IllegalArgumentException("Id value must be a number castable to long");
        }

        try {
            Class<?> clazz = entity.getClass();
            Field idField = clazz.getDeclaredField(idFieldName);
            idField.setAccessible(true);
            idField.set(entity, ((Number) id).longValue());
        } catch(NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("ID field '" + idFieldName + "' not found in class " + entity.getClass().getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Long[] extractEntityIds(Collection<? extends T> entities) {
        Long[] ids = new Long[entities.size()];
        int i = 0;
        for (var e : entities) {
            if (e == null)
                throw new IllegalArgumentException("Entity cannot be null");
            ids[i++] = ((HasId) e).getId();
        }
        return ids;
    }

    public static <T> Long[] extractEntityIds(Iterable<? extends T> entities) {
        // fallback (unknown size)
        ArrayList<Long> list = new ArrayList<>();
        for (T e : entities) {
            if (e == null)
                throw new IllegalArgumentException("Entity cannot be null");
            list.add(((HasId) e).getId());
        }
        return list.toArray(new Long[0]);
    }

    /// ///

    public static IColumnWriter createColumnWriter(Class<?> t) throws Exception {
        //return (f, o, v) -> ((Field) f).set(o, v);
        return Field::set;
    }

    public static IColumnReader createColumnReader(Class<?> t) throws SQLException {

        //getInt and be used directly for primitive types, as they return 0/false for null values, and we can check
        //wasNull() to return null instead for boxed types.

        // hottest first
        if (t == int.class)         return ResultSet::getInt;
        if (t == long.class)        return ResultSet::getLong;
        if (t == double.class)      return ResultSet::getDouble;
        if (t == boolean.class)     return ResultSet::getBoolean;

        // string
        if (t == String.class)      return ResultSet::getString;

        // boxed
        if (t == Integer.class)     return (rs, i) -> { var v = rs.getInt(i) ; return rs.wasNull() ? null : v; };
        if (t == Long.class)        return (rs, i) -> { var v = rs.getLong(i) ; return rs.wasNull() ? null : v; };
        if (t == Double.class)      return (rs, i) -> { var v = rs.getDouble(i) ; return rs.wasNull() ? null : v; };
        if (t == Boolean.class)     return (rs, i) -> { var v = rs.getBoolean(i) ; return rs.wasNull() ? null : v; };

        // time
        if (t == java.time.Instant.class)
            return (rs, i) -> { var v = rs.getObject(i, java.sql.Timestamp.class); return v != null ? v.toInstant() : null; };

        if (t == java.time.LocalDate.class)
            return (rs, i) -> rs.getObject(i, java.time.LocalDate.class);

        if (t == java.time.LocalTime.class)
            return (rs, i) -> rs.getObject(i, java.time.LocalTime.class);

        // fallback
        return ResultSet::getObject;
    }


/*
    hoisted from FSQL

    static void set(Field f, Object o, Object v) throws IllegalAccessException {
        Class<?> t = f.getType();

        if (v == null) {
            if (!t.isPrimitive()) f.set(o, null);
            return;
        }

        if (t == int.class)             f.setInt(o, ((Number) v).intValue());
        else if (t == long.class)       f.setLong(o, ((Number) v).longValue());
        else if (t == double.class)     f.setDouble(o, ((Number) v).doubleValue());
        else if (t == boolean.class)    f.setBoolean(o, (Boolean) v);
        else if (t == float.class)      f.setFloat(o, ((Number) v).floatValue());
        else if (t == short.class)      f.setShort(o, ((Number) v).shortValue());
        else if (t == byte.class)       f.setByte(o, ((Number) v).byteValue());
        else if (t == char.class)       f.setChar(o, (Character) v);
        else f.set(o, v);
    }
*/
}
