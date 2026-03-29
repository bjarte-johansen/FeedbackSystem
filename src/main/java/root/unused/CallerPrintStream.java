package root.unused;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CallerPrintStream extends PrintStream {
    final private OutputStream oldOut;
    private static final StackWalker WALKER =
        StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public CallerPrintStream(OutputStream out) {
        super(out, true);
        oldOut = out;
    }

    private String caller() {
        return WALKER.walk(s -> s
            .skip(3) // skip PrintStream + this class
            .findFirst()
            .map(f -> f.getClassName() + ":" + f.getLineNumber())
            .orElse("?"));
    }

    private void prefix() {
        try {
            oldOut.write('[');
            writeStr(caller());
            oldOut.write(']');
            oldOut.write(' ');
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    private void writeStr(String s) {
        try {
            oldOut.write(s.getBytes(StandardCharsets.UTF_8));
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public void println(String x) {
        prefix();
        writeStr(x + "\n");
    }

    @Override
    public void print(String x) {
        prefix();
        writeStr(x);
    }

    @Override
    public void println(Object x) {
        prefix();
        writeStr(String.valueOf(x) + "\n");
    }

    @Override
    public void print(Object x) {
        prefix();
        writeStr(String.valueOf(x));
    }
}