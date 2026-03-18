package root.database;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Deprecated
class ObjectMapper<T> {

    public Constructor<T> ctor;
    public List<Field> fields;

    ObjectMapper(Constructor<T> ctor, List<Field> fields) {
        this.ctor = ctor;
        this.fields = fields;
    }

    public static <T> Field[] getFields(Class<T> clazz, boolean setAccessible) throws Throwable {
        Field[] fields = clazz.getDeclaredFields();

        if (setAccessible) {
            for (Field f : fields) f.setAccessible(true);
        }

        return fields;
    }

    public static <T> ObjectMapper<T> create(Class<T> clazz) throws Throwable {
        Constructor<T> ctor = clazz.getDeclaredConstructor();
        List<Field> fields = Arrays.asList(getFields(clazz, true));

        return new ObjectMapper<>(ctor, fields);
    }
}
