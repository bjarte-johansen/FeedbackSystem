package root.database;

import root.includes.logger.Logger;

import java.sql.ResultSet;

public class FSQLDefaultRowMapper {
    public static <T> T map(ResultSet rs, FSQLClassMapping<T> classMapping) throws Exception {
        T o = (T) classMapping.create();

        FSQLColumnMapping[] columnMapping = classMapping.columnMapping;
        for (int i = 1; i < columnMapping.length; i++) {
            FSQLColumnMapping cm = columnMapping[i];
            if(cm != null) {
                Object v = cm.getter.read(rs, i);
                if(rs.wasNull()) {
                    v = null;
                }
                cm.setter.write(cm.field, o, v);
            }else{
                if(FSQL.SHOW_SQL_MAPPING_ERRORS)
                    Logger.log("WARNING [FSQLDefaultRowMapper]: No mapping for column " + i + ", column name: " + rs.getMetaData().getColumnName(i) + ", type: " + rs.getMetaData().getColumnTypeName(i));
            }
        }

        return o;
    }
}
