package root.database;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.function.Function;

public class FSQLCachedFieldColumnLookup {
    public static boolean isSerializableField(Field f) {
        int modifiers = f.getModifiers();
        return !(Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || f.isSynthetic());
    }

    protected static Map<String, FSQLColumnMapping> buildEntityColumnMapping(Class<?> clazz) throws Exception {
        Map<String, FSQLColumnMapping> m = new HashMap<>();

        for (Field f : clazz.getDeclaredFields()) {
            if(isSerializableField(f)) {
                f.setAccessible(true);

                var t = f.getType();
                FSQLColumnMapping cm = new FSQLColumnMapping(
                    f.getName(),
                    f,
                    CachedCaseConverter.camelToSnake(f.getName()),
                    FSQLUtils.createColumnReader(t),
                    FSQLUtils.createColumnWriter(t));

                m.put(f.getName(), cm);
            }
        }

        return m;
    }

    protected static FSQLColumnMapping[] _buildColumnIndexToFieldMap(Class<?> clazz, ResultSetMetaData md, Function<String, String> columnNameTransform) throws Exception {
        Objects.requireNonNull(md, "ResultSetMetaData cannot be null");

        int cols = md.getColumnCount();
        Map<String, FSQLColumnMapping> fieldMap = buildEntityColumnMapping(clazz);

        FSQLColumnMapping[] columnMap = new FSQLColumnMapping[cols + 1];

        for (int i = 1; i <= cols; i++) {
            String columnName = md.getColumnName(i);

            if(columnNameTransform != null) {
                columnName = columnNameTransform.apply(columnName);
            }

            columnMap[i] = fieldMap.get( columnName );
        }

        return columnMap;
    }


    /**
     * Builds a mapping of column index to field for the given class and ResultSet metadata.
     *
     * @param clazz The class to map to
     * @param rs The ResultSet containing the metadata to map from
     * @return An array of FSQLColumnMapping where the index corresponds to the column index in the ResultSet (1-based)
     * @throws Exception if there is an error accessing the class fields or ResultSet metadata
     */
    public static FSQLColumnMapping[] build(Class<?> clazz, ResultSet rs) throws Exception {
        return _buildColumnIndexToFieldMap(clazz, rs.getMetaData(), CachedCaseConverter::underscoreToPascal);
    }
}