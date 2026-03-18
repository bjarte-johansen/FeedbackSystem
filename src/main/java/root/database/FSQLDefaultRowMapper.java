package root.database;

import java.sql.ResultSet;

public class FSQLDefaultRowMapper {
    public static <T> T map(ResultSet rs, FSQLClassMapping<T> classMapping) throws Exception {
        T o = (T) classMapping.create();

        FSQLColumnMapping[] columnMapping = classMapping.columnMapping;
        for (int i = 1; i < columnMapping.length; i++) {
            FSQLColumnMapping cm = columnMapping[i];
            if(cm != null) {
                cm.setter.write(cm.field, o, cm.getter.read(rs, i));
            }else{
                System.out.println("WARNING [FSQLDefaultRowMapper]: No mapping for column " + i + ", column name: " + rs.getMetaData().getColumnName(i) + ", type: " + rs.getMetaData().getColumnTypeName(i));
            }
        }

        return o;
    }
}
