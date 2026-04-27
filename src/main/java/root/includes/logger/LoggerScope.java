package root.includes.logger;

@FunctionalInterface
public interface LoggerScope extends AutoCloseable{
    @Override
    void close();

    // methods for scope can be added here, if they call Logger functions they need to manipulate the depth
}