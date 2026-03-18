package root.database;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.*;

@Deprecated
public class FSQLMapper<T> {
    public record ColumnMapping(Field field, MethodHandle setter, int columnIndex) {}

    public final Constructor<T> ctor;
    public final List<ColumnMapping> cols;
    public Map<String, ColumnMapping> nameMap;

    FSQLMapper(Constructor<T> constructor, List<ColumnMapping> cols) {
        this.ctor = constructor;
        this.cols = cols;

        buildNameMap();
    }

    void buildNameMap(){
        nameMap = new HashMap<String, ColumnMapping>();
        for (ColumnMapping cm : cols) {
            nameMap.put(cm.field.getName(), cm);
        }
    }

    T map(ResultSet rs) throws Throwable {
        T o = ctor.newInstance();
        for (ColumnMapping cm : cols) {
            Class<?> t = cm.field.getType();
            if(t.isPrimitive()) {
                if (t == int.class) cm.setter.invoke(o, rs.getInt(cm.columnIndex));
                else if (t == long.class) cm.setter.invoke(o, rs.getLong(cm.columnIndex));
                else if (t == double.class) cm.setter.invoke(o, rs.getDouble(cm.columnIndex));
                else if (t == float.class) cm.setter.invoke(o, rs.getFloat(cm.columnIndex));
                else if (t == boolean.class) cm.setter.invoke(o, rs.getBoolean(cm.columnIndex));
                else if (t == byte.class) cm.setter.invoke(o, rs.getByte(cm.columnIndex));
                else if (t == short.class) cm.setter.invoke(o, rs.getShort(cm.columnIndex));
                else if (t == char.class) cm.setter.invoke(o, rs.getString(cm.columnIndex).charAt(0));
                else throw new RuntimeException("Unsupported primitive type: " + t.getName());
            }else {
                var getter = FSQLUtils.createColumnReader(cm.field.getType());
                cm.setter.invoke(o, getter.read(rs, cm.columnIndex));
            }
        }
        return o;
    }
}
