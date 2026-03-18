package root.database;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModelMetaData {
    static final Map<Class<?>, ModelMetaData> CACHE = new ConcurrentHashMap<>();

    final String[] columns;
    final Class<?>[] types;
    final Field[] fields;

    public ModelMetaData(String[] columns, Class<?>[] types) {
        this.columns = columns;
        this.types = types;
    }

    static ModelMetaData create(Class<?> model) {
        CACHE.computeIfAbsent(model, m -> {
            Field[] f = m.getDeclaredFields();
            String[] columns = new String[f.length];
            Class<?>[] types = new Class<?>[f.length];

            for (int i = 0; i < f.length; i++) {
                columns[i] = CaseConverter.camelToSnake(f[i].getName());
                types[i] = f[i].getType();
            }

            return new ModelMetaData(columns, types);
        });
    }
}
