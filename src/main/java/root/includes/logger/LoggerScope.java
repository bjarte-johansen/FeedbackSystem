package root.includes.logger;

@FunctionalInterface
public interface LoggerScope extends AutoCloseable{
    @Override
    void close();
}