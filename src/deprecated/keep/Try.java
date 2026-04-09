package root.includes;


/**
 * Utility class to wrap lambdas that throw checked exceptions and rethrow them as unchecked exceptions.
 */

@Deprecated
public class Try {
    /**
     * Functional interface for a lambda that takes no arguments and returns a result, and can throw checked exceptions.
     * @param <T>
     */
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    /**
     * Functional interface for a lambda that takes no arguments and returns no result, and can throw checked exceptions.
     */
    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    /**
     * Functional interface for a lambda that takes one argument and returns a result, and can throw checked exceptions.
     * @param <T>
     * @param <R>
     */
    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }

    /**
     * Functional interface for a lambda that takes two arguments and returns a result, and can throw checked exceptions.
     * @param <T>
     * @param <U>
     * @param <R>
     */
    @FunctionalInterface
    public interface ThrowingBiFunction<T, U, R> {
        R apply(T t, U u) throws Exception;
    }

    /**
     * Utility method to wrap a lambda that throws checked exceptions and rethrow them as unchecked exceptions.
     * @param fn
     */
    public static void wrap(ThrowingRunnable fn) {
        try { fn.run(); } catch (Exception e) { throw new RuntimeException(e); }
    }

    /**
     * Utility method to wrap a lambda that throws checked exceptions and rethrow them as unchecked exceptions.
     * @param fn
     * @return
     * @param <T>
     */
    public static <T> T wrap(ThrowingSupplier<T> fn) {
        try { return fn.get(); } catch (Exception e) { throw new RuntimeException(e); }
    }

    /**
     * Utility method to wrap a lambda that throws checked exceptions and rethrow them as unchecked exceptions.
     * @param t
     * @param fn
     * @return
     * @param <T>
     * @param <R>
     */
    public static <T,R> R wrap(T t, ThrowingFunction<T,R> fn) {
        try { return fn.apply(t); } catch (Exception e) { throw new RuntimeException(e); }
    }
};