package root.includes;

import java.io.PrintStream;
import java.util.Locale;


/**
 * Dumb console that we can control without blocking Logger.log(...) which uses System.out, otherwise
 * we would have just replaced System.out.
 */

public class Console {
    public static void print(Object obj) {
        System.out.print(obj);
    }
    public static void println(Object obj){
        System.out.println(obj);
    }
    public static void println() {
        System.out.println();
    }

    public static PrintStream printf(String fmt, Object... args) {
        return System.out.printf(fmt, args);
    }
    public static PrintStream printf(Locale locale, String fmt, Object... args) {
        return System.out.printf(locale, fmt, args);
    }
}
