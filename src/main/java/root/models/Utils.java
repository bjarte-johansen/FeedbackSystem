package root.models;

import java.util.LinkedHashMap;
import java.util.Objects;

class Utils{
    public static LinkedHashMap<String, Object> emptyLinkedNameValueMap() {
        return new LinkedHashMap<>();
    }

    public static LinkedHashMap<String, Object> linkedNameValueMap(Object... args) {
        Objects.requireNonNull(args, "Arguments cannot be null");

        int n = args.length;
        if(n % 2 != 0) throw new IllegalArgumentException("Expected even number of arguments");

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        for (int i=0; i < n; i += 2) {
            map.put((String) args[i], args[i + 1]);
        }
        return map;
    }

    /*
    public static String join(String delim, String... values) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            sb.append(values[i]);
            if (i < values.length - 1)
                sb.append(delim);
        }

        return sb.toString();
    }
     */

    /*
    public static LinkedHashMap<String, Object> linkedMap(Pair<String, Object>... pairs) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        for (Pair<String, Object> pair : pairs) {
            map.put(pair.first, pair.second);
        }
        return map;
    }

    public static final class Pair<K, V> {
        public final K first;
        public final V second;

        public Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }
    }
    */
}