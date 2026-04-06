package root.includes.quicktests.quicktests.repofun;

import java.lang.reflect.Modifier;


public class JsonStringifier {
    public static String stringify(Object o) {
        if (o == null) return "null";

        Class<?> c = o.getClass();
        var fields = c.getDeclaredFields();

        StringBuilder sb = new StringBuilder(64);
        sb.append('{');

        boolean first = true;
        for (var f : fields) {
            int m = f.getModifiers();
            if (Modifier.isStatic(m) || Modifier.isTransient(m) || f.isSynthetic()){
                continue;
            }

            try {
                f.setAccessible(true);

                if (!first) sb.append(',');
                first = false;

                sb.append('"').append(f.getName()).append("\":");

                Object v = f.get(o);

                if (v == null) {
                    sb.append("null");
                } else {
                    sb.append('"').append(escape(v.toString())).append('"');
                }
            } catch (IllegalAccessException e) {
                sb.append("\"?\"");
            }
        }

        return sb.append('}').toString();
    }

    static String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 32) {
                        sb.append("\\u");
                        String hex = Integer.toHexString(c);
                        for (int j = hex.length(); j < 4; j++) sb.append('0');
                        sb.append(hex);
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}
