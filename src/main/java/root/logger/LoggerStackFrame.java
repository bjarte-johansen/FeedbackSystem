package root.logger;

public class LoggerStackFrame {
    static public String DEFAULT_SOURCE_LINK_PREFIX = "@";
    static public String DEFAULT_CLASS_METHOD_DELIMITER = "::";

    private final StackWalker.StackFrame frame;

    public LoggerStackFrame(StackWalker.StackFrame frame) {
        this.frame = frame;
    }

    public static LoggerStackFrame createFromCurrentStack(int depth) {
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        return walker.walk(s -> s.skip(depth + 2).findFirst().map(LoggerStackFrame::new).orElse(null));
    }

    public String getClassName() {
        return frame.getClassName();
    }
    public String getClassAndMethod() { return getClassAndMethod(DEFAULT_CLASS_METHOD_DELIMITER); }
    public String getClassAndMethod(String delimiter) {
        return getSimpleName() + delimiter + getMethodName();
    }
    public String getMethodName() {
        return frame.getMethodName();
    }
    public String getFileName() {
        return frame.getFileName();
    }
    public int getLineNumber() {
        return frame.getLineNumber();
    }
    public String getSimpleName() { return extractSimpleName(); }
    public String getSourceLink(String prefix) {
        return prefix + "(" + getFileName() + ":" + getLineNumber() + ")";
    }

    protected String extractSimpleName(){
        /*
        if (hasDeclaringClass()) {
            return this..getDeclaringClass().getSimpleName();
        }*/

        String className = this.getClassName();
        int lastDotIndex = className.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return className.substring(lastDotIndex + 1);
        }
        return className;
    }

    public String toString() {
        return "LMStackFrame{" +
            "className='" + getClassName() + '\'' +
            ", simpleClassName='" + getSimpleName() + '\'' +
            ", methodName='" + getMethodName() + '\'' +
            ", fileName='" + getFileName() + '\'' +
            ", lineNumber=" + getLineNumber() +
            '}';
    }
}
