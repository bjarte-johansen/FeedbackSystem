package root.database;

import java.lang.reflect.*;

public class ObjectStringifier {
    public static String stringify(Object o) throws IllegalAccessException {
        if (o == null) return "null";

        Class<?> c = o.getClass();

        if (c == String.class) return "\"" + o + "\"";
        if (Number.class.isAssignableFrom(c) || c == Boolean.class) return o.toString();

        if (c.isArray()) {
            int n = Array.getLength(o);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < n; i++) {
                if (i > 0) sb.append(",");
                sb.append( stringify(Array.get(o, i)) );
            }
            return sb.append("]").toString();
        }

        StringBuilder sb = new StringBuilder("{");
        Field[] f = c.getFields();

        for (int i = 0; i < f.length; i++) {
            Field field = f[i];
            Object v = field.get(o);

            if (i > 0) sb.append(",");
            sb.append("\"").append(field.getName()).append("\":");
            sb.append( stringify(v) );
        }

        return sb.append("}").toString();
    }
}
