package root.models;

import java.util.LinkedHashMap;
import java.util.Objects;

@Deprecated
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
}