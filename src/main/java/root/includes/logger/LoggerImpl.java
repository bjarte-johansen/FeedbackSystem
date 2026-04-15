package root.includes.logger;

import java.util.Arrays;

//@Deprecated
public class LoggerImpl {
    private static String sprintfv(Object[] args) {
        if(args == null || args.length == 0)
            throw new IllegalArgumentException("Format string is required for formatted logging methods.");

        String fmt = String.valueOf(args[0]);
        Object[] rest = Arrays.copyOfRange(args, 1, args.length);

        return String.format(fmt, rest);
    }

    // actual logging methods, just call put with appropriate event type
    public LoggerInterface log(Object... args) { Logger.put(Logger.EVENT_DEFAULT, args); return Logger.LOGGER_PROXY; }
    public LoggerInterface debug(Object... args) { Logger.put(Logger.EVENT_DEBUG, args); return Logger.LOGGER_PROXY; }
    public LoggerInterface info(Object... args) { Logger.put(Logger.EVENT_INFO, args); return Logger.LOGGER_PROXY; }
    public LoggerInterface warn(Object... args) { Logger.put(Logger.EVENT_WARN, args); return Logger.LOGGER_PROXY; }
    public LoggerInterface error(Object... args) { Logger.put(Logger.EVENT_ERROR, args); return Logger.LOGGER_PROXY;}

    public LoggerInterface logf(Object... args) { Logger.put(Logger.EVENT_DEFAULT, sprintfv(args)); return Logger.LOGGER_PROXY;}
    public LoggerInterface debugf(Object... args) { Logger.put(Logger.EVENT_DEBUG, sprintfv(args)); return Logger.LOGGER_PROXY;}
    public LoggerInterface infof(Object... args) { Logger.put(Logger.EVENT_INFO, sprintfv(args)); return Logger.LOGGER_PROXY; }
    public LoggerInterface warnf(Object... args) { Logger.put(Logger.EVENT_WARN, sprintfv(args)); return Logger.LOGGER_PROXY; }
    public LoggerInterface errorf(Object... args) { Logger.put(Logger.EVENT_ERROR, sprintfv(args)); return Logger.LOGGER_PROXY; }


    public LoggerInterface tab() {
        Logger.nextPrintIndent++;
        return Logger.LOGGER_PROXY;
    }

    public LoggerInterface caller(int depth) {
        Logger.nextCallerDepth = depth;
        return Logger.LOGGER_PROXY;
    }
}
