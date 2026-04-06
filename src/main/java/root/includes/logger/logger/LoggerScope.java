package root.includes.logger.logger;

@FunctionalInterface
public interface LoggerScope extends AutoCloseable{
    @Override
    void close();
}