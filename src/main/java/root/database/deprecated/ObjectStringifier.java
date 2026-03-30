package root.database.deprecated;

import java.lang.reflect.*;


/**
 * A simple utility to stringify an object by reflecting on its fields.
 * This is a very basic implementation and may not handle all cases (like circular references, etc.).
 * It is intended for debugging purposes and should not be used in production code.
 * It is also marked as deprecated to indicate that it is not recommended for use and may be removed in future versions.
 *
 * Possibly by chatGPT, but I don't remember exactly. It was a quick utility to help with debugging and logging,
 * but it is not intended for production use.
 */

@Deprecated
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
