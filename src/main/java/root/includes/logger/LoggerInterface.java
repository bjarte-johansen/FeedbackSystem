package root.includes.logger;

public interface LoggerInterface {
    void log(Object... args);
    void debug(Object... args);
    void info(Object... args);
    void warn(Object... args);
    void error(Object... args);
    //LogProxy tab();
}
