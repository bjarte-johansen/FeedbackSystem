package root.database;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class ModelMetaData {
    private static final Map< Class<?>, ModelMetaData > CACHE = new ConcurrentHashMap<>();
    private static final String DEFAULT_ID_FIELD_NAME = "id";

    final Class<?> model;
    final String[] columns;
    final Class<?>[] types;
    final Field[]  fields;
    final Field entityId;

    public ModelMetaData(Class<?> model, String[] columns, Class<?>[] types, Field[] fields, Field idField) {
        this.model = model;
        this.columns = columns;
        this.types = types;
        this.fields = fields;
        this.entityId = idField;
    }

    static ModelMetaData create(Class<?> model, String idName) {
        String finalIdName = (idName == null) ? DEFAULT_ID_FIELD_NAME : idName;

        return CACHE.computeIfAbsent(model, m -> {
            Field[] fields = m.getDeclaredFields();
            String[] columns = new String[fields.length];
            Class<?>[] types = new Class<?>[fields.length];
            Field entityId = null;

            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                String fieldName = f.getName();

                if(entityId == null && fieldName.equals(finalIdName)) {
                    entityId = f;
                 }

                columns[i] = CaseConverter.camelToSnake(fieldName);
                types[i] = f.getType();
            }

            if(entityId == null) {
                throw new RuntimeException("Failed to find id field with name " + finalIdName + " in class " + model.getName());
            }

            return new ModelMetaData(model, columns, types, fields, entityId);
        });
    }
}
