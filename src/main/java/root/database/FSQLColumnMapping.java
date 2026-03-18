package root.database;

import javax.persistence.criteria.CriteriaBuilder;
import java.lang.reflect.Field;

public class FSQLColumnMapping {
    public final String column;
    public final Field field;
    public final IColumnReader getter;
    public final IColumnWriter setter;

    /**
     * Creates a new column mapping.
     *
     * @param column the name of the column in the database
     * @param field the field in the Java class to map to
     * @param getter the function to read the column value from a ResultSet
     */
    public FSQLColumnMapping(String column, Field field, IColumnReader getter, IColumnWriter setter ) {
        this.column = column;
        this.field = field;
        this.getter = getter;
        this.setter = setter;
    }
}
