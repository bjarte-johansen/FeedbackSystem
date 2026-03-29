package root.database;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Utility class for converting between different case styles (e.g., snake_case and camelCase). In part written by
 * chatgpt and modified by us.
 * <p>
 * The core logic is from older project + suggestions from chatgpt.
 */

public class CachedCaseConverter  {
    private static final ConcurrentHashMap<String, String> underscoreToPascalCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> camelToSnakeCache = new ConcurrentHashMap<>();

    public static String underscoreToPascal(String s) {
        return underscoreToPascalCache.computeIfAbsent(s, CaseConverter::underscoreToPascal);
    }

    public static String camelToSnake(String s) {
        return camelToSnakeCache.computeIfAbsent(s, CaseConverter::camelToSnake);
    }
}
