package root.logger;

import java.util.Arrays;

public class LoggerImpl {
    private static String format(Object[] args) {
        if(args == null || args.length == 0)
            throw new IllegalArgumentException("Format string is required for formatted logging methods.");

        String fmt = String.valueOf(args[0]);
        Object[] rest = Arrays.copyOfRange(args, 1, args.length);

        return String.format(fmt, rest);
    }

    // actual logging methods, just call put with appropriate event type
    public void log(Object... args) { Logger.put(Logger.EVENT_DEFAULT, args); }
    public void debug(Object... args) { Logger.put(Logger.EVENT_DEBUG, args); }
    public void info(Object... args) { Logger.put(Logger.EVENT_INFO, args); }
    public void warn(Object... args) { Logger.put(Logger.EVENT_WARN, args); }
    public void error(Object... args) { Logger.put(Logger.EVENT_ERROR, args); }

    public void logf(Object... args) { Logger.put(Logger.EVENT_DEFAULT, format(args)); }
    public void debugf(Object... args) { Logger.put(Logger.EVENT_DEBUG, format(args)); }
    public void infof(Object... args) { Logger.put(Logger.EVENT_INFO, format(args)); }
    public void warnf(Object... args) { Logger.put(Logger.EVENT_WARN, format(args)); }
    public void errorf(Object... args) { Logger.put(Logger.EVENT_ERROR, format(args)); }

    public LoggerImpl logProxy = null;

    public LoggerImpl tab() {
        Logger.nextPrintIndent++;
        if (logProxy == null) {
            logProxy = new LoggerImpl();
        }
        return logProxy;
    }
}
