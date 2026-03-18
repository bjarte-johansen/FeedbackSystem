package root;

import java.io.*;
import java.util.Set;

public class CallerPrintStream extends PrintStream {

    private static final StackWalker WALKER =
        StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public CallerPrintStream(OutputStream out) {
        super(out, true);
    }

    private String caller() {
        return WALKER.walk(s -> s
            .skip(3) // skip PrintStream + this class
            .findFirst()
            .map(f -> f.getClassName() + ":" + f.getLineNumber())
            .orElse("?"));
    }

    private void prefix() {
        super.print("[" + caller() + "] ");
    }

    @Override
    public void println(String x) {
        prefix();
        super.println(x);
    }

    @Override
    public void print(String x) {
        prefix();
        super.print(x);
    }

    @Override
    public void println(Object x) {
        prefix();
        super.println(x);
    }

    @Override
    public void print(Object x) {
        prefix();
        super.print(x);
    }
}