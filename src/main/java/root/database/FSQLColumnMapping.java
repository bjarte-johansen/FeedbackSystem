package root.database;

import java.lang.reflect.Field;

public class FSQLColumnMapping {
    public final Field field;
    public final String fieldName;
    public final Class<?> fieldType;
    public final String columnName;
    public final IColumnReader getter;
    public final IColumnWriter setter;

    /**
     * Creates a new column mapping.
     *
     * @param fieldName the name of the column in the database
     * @param field the field in the Java class to map to
     * @param columnName the name of the column in the database
     * @param getter the function to read the column value from a ResultSet
     * @param setter the function to write the column value to a PreparedStatement
     */

    public FSQLColumnMapping(String fieldName, Field field, String columnName, IColumnReader getter, IColumnWriter setter ) {
        this.field = field;
        this.fieldName = fieldName;
        this.fieldType = field.getType();
        this.columnName = columnName;
        this.getter = getter;
        this.setter = setter;
    }
}
