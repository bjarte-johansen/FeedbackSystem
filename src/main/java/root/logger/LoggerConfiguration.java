package root.logger;

public class LoggerConfiguration {
    public boolean enabled                       = true;
    public boolean USE_MAGIC_LOGGING             = true;
    public boolean LAYOUT_ADJUST_MAGIC_RIGHT     = true;
    public int FORMAT_TYPE                       = LoggerFormatType.YAML;
    public boolean SHOW_EVENT_TYPE               = false;

    @Override
    public String toString() {
        return "LoggerConfiguration{" +
                "debug=" + enabled +
                ", USE_MAGIC_LOGGING=" + USE_MAGIC_LOGGING +
                ", LAYOUT_ADJUST_MAGIC_RIGHT=" + LAYOUT_ADJUST_MAGIC_RIGHT +
                ", FORMAT_TYPE=" + FORMAT_TYPE +
                ", SHOW_EVENT_TYPE=" + SHOW_EVENT_TYPE +
                '}';
    }
}
