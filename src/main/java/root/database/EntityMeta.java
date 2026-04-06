package root.database;

import org.springframework.data.util.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EntityMeta {
    /*
    public class MappingFieldValuetIterator implements Iterator<Object> {
        private final Object o;
        private final FSQLColumnMapping[] mappings;
        private int index = 0;

        public MappingFieldValuetIterator(Object o, FSQLColumnMapping[] mappings) {
            this.o = o;
            this.mappings = mappings;
        }

        @Override
        public boolean hasNext() {
            return index < mappings.length;
        }

        @Override
        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            try {
                return mappings[index++].field.get(o);
            }catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
    */

    public record LocalPair<F, S>(F first, S second) {}

    private static final ConcurrentHashMap<Class<?>, EntityMeta> ENTITY_META_CACHE = new ConcurrentHashMap<>();

    private EntityMeta() {
    }

    /*
    public MappingFieldValuetIterator getFieldValueIterator(Object o, boolean includeId) {
        return new MappingFieldValuetIterator(o, includeId ? all : nonId);
    }
     */

    public Constructor<?> ctor;

    public Class<?> clazz;
    public String entityClassName;

    public FSQLColumnMapping[] all;
    public FSQLColumnMapping[] nonId;

    public String[] allColumnNames;
    public String[] allFieldNames;

    public String[] nonIdColumnNames;
    public String[] nonIdFieldNames;

    public Map<String, FSQLColumnMapping> byColumn;
    public Map<String, FSQLColumnMapping> byField;

    public FSQLColumnMapping idMapping;

    public Field[] declaredFields;

    public int extractFieldValuesFromColumnMappings(Object o, FSQLColumnMapping[] mappings, List<Object> dst) throws Exception {
        int n = mappings.length;

        for (int i = 0; i < n; i++) {
            dst.set(i, mappings[i].field.get(o));
        }

        return n;
    }

    public Object[] extractFieldValuesFromColumnMappings(Object o, FSQLColumnMapping[] mappings) {
        try {
            Object[] result = new Object[mappings.length];

            for (int i = 0; i < mappings.length; i++) {
                result[i] = mappings[i].field.get(o);
            }

            return result;
        }catch(IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object[] getFieldValues(Object o, boolean includeId) throws Exception {
        return extractFieldValuesFromColumnMappings(o, includeId ? all : nonId);
    }

    public String[] extractColumnNames(FSQLColumnMapping[] columnMappings) {
        String[] columnNames = new String[columnMappings.length];
        for (int i = 0; i < columnMappings.length; i++) {
            columnNames[i] = columnMappings[i].columnName;
        }
        return columnNames;
    }

    public synchronized  String[] getColumnNames(boolean includeId){
        if(includeId) {
            return getAllColumnNames();
        } else {
            return getNonIdColumnNames();
        }
    }
    public synchronized String[] getAllColumnNames() {
        if(allColumnNames == null) {
            allColumnNames = extractColumnNames(nonId);
        }
        return allColumnNames;
    }

    public synchronized String[] getNonIdColumnNames() {
        if(nonIdColumnNames == null) {
            nonIdColumnNames = extractColumnNames(nonId);
        }
        return nonIdColumnNames;
    }

    public Object[] getAllPropertyValues(Object o) throws Exception {
        return extractFieldValuesFromColumnMappings(o, all);
    }
    public Object[] getNonIdPropertyValues(Object o) throws Exception {
        return extractFieldValuesFromColumnMappings(o, nonId);
    }


    /**
     * Extracts column names and field values from the given object based on the provided column mappings, and returns
     * them as a pair of arrays.
     *
     * Results will be as Pair(String[] columnNames, Object[] fieldValues)
     *
     * @param o The object from which to extract field values.
     * @param mappings The column mappings that define how to extract column names and field values.
     * @return A LocalPair<String[], Object[]> containing an array of column names and an array of corresponding field
     * values.
     */

    public LocalPair<String[], Object[]> extractColumnNamesAndValuesArrayAsPair(Object o, FSQLColumnMapping[] mappings) throws Exception {
        String[] columnNames = new String[mappings.length];
        Object[] values = new Object[mappings.length];

        for (int i = 0; i < mappings.length; i++) {
            columnNames[i] = mappings[i].columnName;
            values[i] = mappings[i].field.get(o);
        }

        return new LocalPair<>(columnNames, values);
    }

    /*
     * static helper methods
     */

    public static EntityMeta create(Class<?> entityClazz) throws Exception {
        return ENTITY_META_CACHE.computeIfAbsent(entityClazz, EntityMeta::doCreate);
    }

    private static boolean isSerializableField(Field f) {
        int modifiers = f.getModifiers();
        return !(Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || f.isSynthetic());
    }

    private static EntityMeta doCreate(Class<?> clazz) {
        try {
            EntityMeta meta = new EntityMeta();

            meta.declaredFields = clazz.getDeclaredFields();
            meta.ctor = clazz.getDeclaredConstructor();

            // count valid fields (non-static, non-transient, non-synthetic)
            int count = 0;
            for (Field f : meta.declaredFields) {
                if(isSerializableField(f)) {
                    count++;
                }
            }

            meta.clazz = clazz;
            meta.entityClassName = clazz.getSimpleName();

            meta.all = new FSQLColumnMapping[count];
            meta.nonId = new FSQLColumnMapping[count - 1];

            meta.allColumnNames = new String[count];
            meta.allFieldNames = new String[count];

            meta.nonIdColumnNames = new String[count - 1];
            meta.nonIdFieldNames = new String[count - 1];

            meta.byColumn = new HashMap<>(count * 2);
            meta.byField = new HashMap<>(count * 2);

            meta.idMapping = null;

            int allIndex = 0;
            int nonIdIndex = 0;

            for (Field f : meta.declaredFields) {
                if(!isSerializableField(f)) {
                    continue;
                }

                // set accessible to bypass private/pro
                f.setAccessible(true);

                // get field name and type
                String fieldName = f.getName();
                Class<?> fieldType = f.getType();

                // create column mapping for the field
                FSQLColumnMapping cm = new FSQLColumnMapping(
                    fieldName,
                    f,
                    CachedCaseConverter.camelToSnake(fieldName),
                    FSQLUtils.createColumnReader(fieldType),
                    FSQLUtils.createColumnWriter(fieldType)
                );

                meta.all[allIndex] = cm;
                meta.allColumnNames[allIndex] = cm.columnName;
                meta.allFieldNames[allIndex] = cm.fieldName;

                if (fieldName.equals(Bugs.getDefaultEntityIdName())) {
                    meta.idMapping = cm;
                } else {
                    meta.nonId[nonIdIndex] = cm;
                    meta.nonIdColumnNames[nonIdIndex] = cm.columnName;
                    meta.nonIdFieldNames[nonIdIndex] = cm.fieldName;

                    nonIdIndex++;
                }

                allIndex++;

                meta.byField.put(cm.fieldName, cm);
                meta.byColumn.put(cm.columnName, cm);
            }

            if (meta.idMapping == null) {
                throw new IllegalArgumentException("Class " + clazz.getName() + " must have a field named '" + Bugs.getDefaultEntityIdName() + "' to be used as an ID field");
            }

            return meta;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        String NL = "\n";
        return "EntityMeta{"
            + "clazz=" + clazz.getName() + NL
            + ", idMapping=" + idMapping.fieldName + NL
            + ", allFields=" + String.join(", ", allFieldNames) + NL
            + ", allColumns=" + String.join(", ", allColumnNames) + NL
            + ", nonIdFields=" + String.join(", ", nonIdFieldNames) + NL
            + ", nonIdColumns=" + String.join(", ", nonIdColumnNames) + NL
            + ", byField=" + byField.keySet() + NL
            + ", byColumn=" + byColumn.keySet() + NL
            + ", ctor=" + ctor.toString() + NL
            + ", declaredFields=" + String.join(", ", java.util.Arrays.stream(declaredFields).map(Field::getName).toArray(String[]::new)) + NL
            +
            '}';
    }
}
