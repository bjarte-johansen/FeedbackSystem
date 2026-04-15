package root.includes.logger;

public interface LoggerInterface {
    LoggerInterface log(Object... args);
    LoggerInterface debug(Object... args);
    LoggerInterface info(Object... args);
    LoggerInterface warn(Object... args);
    LoggerInterface error(Object... args);

    LoggerInterface enter();
    LoggerInterface leave();
    //LogProxy tab();
}
