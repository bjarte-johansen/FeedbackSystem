package root.database;

import java.util.ArrayList;
import java.util.List;

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
