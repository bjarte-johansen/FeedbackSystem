package root.logger;

@FunctionalInterface
public interface LoggerScope extends AutoCloseable{
    @Override
    void close();
}