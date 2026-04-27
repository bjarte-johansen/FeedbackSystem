package root.includes;

import java.sql.Connection;

public class Functional {
    /**
     * functional interfaces that we decided not to move cause of time and resource expenditures.
     */

    @FunctionalInterface
    public interface ConnectionStatementRunnable {
        void run(Connection c, java.sql.Statement st) throws Exception;
    }

//    @FunctionalInterface
//    public interface ConnectionStatementSchemaRunnable {
//        void run(Connection c, java.sql.Statement st, String schema) throws Exception;
//    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }

//    @FunctionalInterface
//    public interface ThrowingConsumer<T> {
//        void accept(T t) throws Exception;
//    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

//    @FunctionalInterface
//    public interface ThrowingFunction<T, R> {
//        R apply(T t) throws Exception;
//    }

//    @FunctionalInterface
//    public interface ThrowingBiFunction<T, U, R> {
//        R apply(T t, U u) throws Exception;
//    }
}
