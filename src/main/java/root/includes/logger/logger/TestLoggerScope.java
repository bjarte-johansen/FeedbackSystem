package root.includes.logger.logger;

@Deprecated
public class TestLoggerScope implements LoggerScope {
    private final boolean verbose;
    private final String title;

    public TestLoggerScope(String title, boolean verbose) {
        this.title = title;
        this.verbose = verbose;
    }

    public void open() {
        if(verbose) {
            Logger.log("Entering scope: " + title);
        }
        Logger.enter();
    }

    @Override
    public void close() {
        Logger.leave();
        if(verbose) {
            Logger.log("Leaving scope: " + title);
        }
    }
}
