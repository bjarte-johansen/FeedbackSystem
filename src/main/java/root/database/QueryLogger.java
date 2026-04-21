package root.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple querylogger to store SQL queries that are added by add function, has simple functionality to add,
 * clear, check if empty and get query as list of strings
 *
 * This is an internal class and javadoc is therefore omitted
 */

public class QueryLogger {
    private final static QueryLogger INSTANCE = new QueryLogger();
    public static ThreadLocal<List<String>> TL = new ThreadLocal<>();

    public static void add(String sql) {
        TL.get().add(sql);
    }
    public static void start(){
        TL.set(new ArrayList<>(50));
    }
    public static List<String> stop() {
        return TL.get();
    }
    public static List<String> get() {
        return TL.get();
    }
    public static boolean isEmpty(){
        return TL.get().isEmpty();
    }
    public static QueryLogger getSingleton(){
        return INSTANCE;
    }
}
