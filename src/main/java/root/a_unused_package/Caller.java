package root.a_unused_package;

public class Caller {
    static String getCalledSourceLink(int depth) {
        return StackWalker.getInstance()
            .walk(s -> s.skip(1 + depth).findFirst()
                .map(f ->
                    f.getClassName() + "@" +
                    f.getMethodName() + "(" +
                    f.getFileName() + ":" +
                    f.getLineNumber() + ")")
                .orElse("unknown"));
    }
}
