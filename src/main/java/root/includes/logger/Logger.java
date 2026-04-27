package root.includes.logger;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiConsumer;

/*
ansi functions
 */
/*
    public static class AnsiStringUtils {
        private static final Pattern ANSI = Pattern.compile("\\u001B\\[[0-9;?]*[ -/]*[@-~]");

        public static String strip(String s) {
            return ANSI.matcher(s).replaceAll("");
        }
        public static int length(String s) {
            return strip(s).length();
        }
    }
*/

// TODO: depth should probably not be threadlocal, hard to say for sure, could be beneficial to look at it

public class Logger {
    protected static LoggerConfiguration cfg = new LoggerConfiguration();

    public static ThreadLocal<Integer> depth = ThreadLocal.withInitial(() -> 0);
    public static ConcurrentHashMap<Integer, String> indentCache = new ConcurrentHashMap<>();


    private static final String newlineString = System.lineSeparator();
    private static final String tabString = "\t";

    private static final String BLOCK_HEADER_BACKGROUND = "\033[48;2;40;40;40m";   // background
    private static final String BLOCK_BACKGROUND_RESET = "\033[0m";

    private static final ConcurrentLinkedDeque<Integer> scopeConfigQueue = new ConcurrentLinkedDeque<>();
    private static final ConcurrentLinkedDeque<LoggerScope> scopeStack = new ConcurrentLinkedDeque<>();

    public static int nextPrintIndent = 0;
    public static int nextCallerDepth = 0;

    public static final int EVENT_DEBUG = 0;
    public static final int EVENT_INFO = 1;
    public static final int EVENT_WARN = 2;
    public static final int EVENT_ERROR = 3;
    public static final int EVENT_DEFAULT = 1;

    public static LoggerConfiguration getConfig(){
        return cfg;
    }

    protected static Map<Integer, String> eventTypeLabels = Map.of(
        EVENT_DEBUG, "[debug]",
        EVENT_INFO, "[info]",
        EVENT_WARN, "[warn]",
        EVENT_ERROR, "[error]"
    );

    protected static MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    protected static LoggerInterface LOGGER_PROXY = (LoggerInterface) Proxy.newProxyInstance(
        LoggerInterface.class.getClassLoader(),
        new Class[]{LoggerInterface.class},
        (proxy, method, args) -> {
            MethodHandle mh = LOOKUP.findStatic(
                Logger.class,
                method.getName(),
                MethodType.methodType(method.getReturnType(), method.getParameterTypes())
            );
            return mh.invokeWithArguments(args == null ? new Object[0] : args);
            //return method.invoke(LOGGER_INSTANCE, args);
        }
    );

    /*
        Indent-handling in setup of try-with entering scope
    */

    public static String colorize(String s, String ansiColorCode) {
        if(!cfg.OVERRIDE_COLORIZE) return s;
        return ansiColorCode + s + AnsiColors.RESET;
    }
/*
    public static LoggerScope scopeSurroundingBlock(String title, boolean verbose) {
        var newScope = new TestLoggerScope(title, verbose);
        scopeStack.add(newScope);
        newScope.open();
        return newScope;
    }
 */
    public static LoggerScope scope(String title) {
        return scope(title, 1, true);
    }
    public static LoggerScope scope(String title, boolean verbose) {
        return scope(title, 1, verbose);
    }

    public static void withScope(String title, Runnable runnable) {
        try(var tmp = scope(title)) {
            runnable.run();
        }
    }

    public static LoggerScope scope(String title, BiConsumer<String, Integer> fnEnter, BiConsumer<String, Integer> fnLeave){
        fnEnter.accept(title, depth.get());
        enter();

        return () -> {
            leave();
            fnLeave.accept(title, depth.get());
        };
    }

    public static LoggerScope scope(String title, int depth, boolean verbose) {
        // magic logging
        if (cfg.USE_MAGIC_LOGGING && verbose) {
            var callerFrame = LoggerStackFrame.createFromCurrentStack(depth + 1);

            String magicString = MagicParameterTemplateProcessor.format("@classMethod @link", callerFrame);

            String s = switch(cfg.FORMAT_TYPE) {
                case LoggerFormatType.BEGIN_END -> String.format("BEGIN %s - %s:", title, magicString);
                case LoggerFormatType.YAML -> String.format("%s - %s:", title, magicString);
                case LoggerFormatType.BLOCK -> magicString + " {";
                case LoggerFormatType.TITLED_BLOCK -> String.format("%s - %s {", title, magicString);
                default -> null;
            };

            if(s != null) {
                System.out.print(getIndentPrefixStr());
                //System.out.println(colorize(s, LIGHT_GRAY));
                System.out.println(BLOCK_HEADER_BACKGROUND + colorize(s, AnsiColors.GREEN ) + ";" + BLOCK_BACKGROUND_RESET);
            }
        }

        // enter block
        enter();

        // return scope object that will leave block on close
        return () -> {
            // leave block
            leave();

            if(verbose) {

                // magic logging
                String s = switch (cfg.FORMAT_TYPE) {
                    case LoggerFormatType.BEGIN_END -> colorize("### ", AnsiColors.GREEN) + colorize("LEAVE", AnsiColors.LIGHT_GRAY);
                    case LoggerFormatType.BLOCK, LoggerFormatType.TITLED_BLOCK -> "}";
                    default -> null;
                };

                if(s != null) {
                    System.out.print(getIndentPrefixStr());
                    //System.out.println(colorize(s, LIGHT_GRAY));
                    System.out.println(BLOCK_HEADER_BACKGROUND + colorize(s, AnsiColors.GREEN ) + ";" + BLOCK_BACKGROUND_RESET);
                }
/*
                if (s != null) {
                    System.out.print(getIndentPrefixStr());
                    System.out.println(s);
                }
 */

                if (cfg.FORMAT_EXTRA_LINE_AFTER_BLOCK) {
                    System.out.println();
                }
            }
        };
    }

    public static LoggerInterface enter() { depth.set(depth.get() + 1); return LOGGER_PROXY;}
    public static LoggerInterface leave() { depth.set(depth.get() - 1); return LOGGER_PROXY; }



    /*
        Internal logging method that handles indentation and optional magic logging features.
        This method is synchronized to ensure thread safety when multiple threads are logging
        simultaneously.
    */

    protected static String getIndentPrefixStr(){
        return indentCache.computeIfAbsent(depth.get(), d -> "    ".repeat(Math.max(0, d)));
    }




    //




    /**
     * Internal logging methods that handles indentation and optional magic logging features.
     */

    protected static void beforeAppend() {
        // placeholder for any setup needed before appending log messages, such as acquiring locks or preparing resources
    }
    protected static void afterAppend() {
        // placeholder for any cleanup needed after appending log messages, such as releasing locks or cleaning up resources
    }

    protected static void __print(String s){
        System.out.print(s);
    }
    protected static void __append(String input, int frameDepthOffset){
        if(input == null) {
            __print(newlineString);
            return;
        }

        String indentStr = getIndentPrefixStr();
        String userOut;
        int inputLength = input.length();

        synchronized (Logger.class) {
            String magicOut = "";
            if(cfg.SYNTAX_HIGHLIGHT_ENABLED) {
                userOut = SyntaxHighlighter.highlight(input);
            }else{
                userOut = input;

            }
            StringBuilder out = new StringBuilder(512);

            if(cfg.USE_MAGIC_LOGGING) {
                var callerFrame = LoggerStackFrame.createFromCurrentStack(1 + frameDepthOffset + nextCallerDepth);
                nextCallerDepth = 0;

                magicOut = AnsiColors.DARK_GRAY + MagicParameterTemplateProcessor.format("@classMethod @link", callerFrame) + AnsiColors.RESET;
            }

            if(cfg.LAYOUT_ADJUST_MAGIC_RIGHT && !magicOut.isEmpty()) {
                int totalWidth = 104;

                userOut = StringUtils.padRight(userOut, inputLength, totalWidth, ' ');

                // $tab?$userOut?$magicOut$nl
                out.append(indentStr);

                if(nextPrintIndent > 0) {
                    out.append(tabString.repeat(nextPrintIndent));
                    nextPrintIndent = 0;
                }

                out.append(userOut);
                out.append(magicOut);
                out.append(newlineString);
            } else {
                out.append(indentStr);
                out.append(magicOut);
                out.append(newlineString);

                out.append(indentStr);
                out.append(tabString);

                if(nextPrintIndent > 0) {
                    out.append(tabString.repeat(nextPrintIndent));
                    nextPrintIndent = 0;
                }

                out.append(userOut);
                out.append(newlineString);
            }

            __print(out.toString());
        }
    }

    protected static String getEventTypeAsString(int level) {
        if(cfg.SHOW_EVENT_TYPE){
            return eventTypeLabels.getOrDefault(level, "[default]");
        }
        return "";
    }

    protected static void log_ext(int frameDepthOffset, int level, Object details) {
        if (!cfg.enabled) return;

        String prefix = getEventTypeAsString(level);
        if(prefix != null && !prefix.isEmpty()) {
            prefix += "\t";
        }

        __append(prefix + String.valueOf(details), frameDepthOffset + 1);
    }

    protected static String stringify(Object... args){
        int n = (args == null) ? 0 : args.length;
        //System.out.print("stringify args length: " + n + "\n");
        if(n == 0) {
            return "";
        }else if(n == 1) {
            if(args[0] != null && args[0].getClass().isArray()) {
                Object array = args[0];
                StringBuilder sb = new StringBuilder(1024);
                for(int i=0; i < java.lang.reflect.Array.getLength(array); i++) {
                    Object element = java.lang.reflect.Array.get(args[0], i);
                    //System.out.println("array element " + i + ": " + stringify(element));
                    sb.append(stringify(element));
                }
                //return stringify(args[0]);
                return sb.toString();
            }

            return String.valueOf(args[0]);
        }else if(n == 2) {
            return String.valueOf(args[0]) + ": " + String.valueOf(args[1]);
        } else {
            StringJoiner joiner = new StringJoiner(" ");
            for (Object arg : args) {
                if (arg == null) {
                    arg = "null";
                }
                joiner.add(String.valueOf(arg));
            }
            return joiner.toString();
        }
    }

    protected static void put(int level, Object... args) {
        log_ext(1, level, stringify(args));
    }

    private static String format(Object[] args) {
        if(args == null || args.length == 0)
            throw new IllegalArgumentException("Format string is required for formatted logging methods.");

        String fmt = String.valueOf(args[0]);
        Object[] rest = Arrays.copyOfRange(args, 1, args.length);

        return String.format(fmt, rest);
    }

    // actual logging methods, just call put with appropriate event type
    public static LoggerInterface log(Object... args) { put(EVENT_DEFAULT, args); return LOGGER_PROXY; }
    public static LoggerInterface debug(Object... args) { put(EVENT_DEBUG, args); return LOGGER_PROXY; }
    public static LoggerInterface info(Object... args) { put(EVENT_INFO, args); return LOGGER_PROXY; }
    public static LoggerInterface warn(Object... args) { put(EVENT_WARN, args); return LOGGER_PROXY; }
    public static LoggerInterface error(Object... args) { put(EVENT_ERROR, args); return LOGGER_PROXY; }

    public static LoggerInterface logf(Object... args) { put(EVENT_DEFAULT, format(args)); return LOGGER_PROXY; }
    public static LoggerInterface debugf(Object... args) { put(EVENT_DEBUG, format(args)); return LOGGER_PROXY; }
    public static LoggerInterface infof(Object... args) { put(EVENT_INFO, format(args)); return LOGGER_PROXY; }
    public static LoggerInterface warnf(Object... args) { put(EVENT_WARN, format(args)); return LOGGER_PROXY; }
    public static LoggerInterface errorf(Object... args) { put(EVENT_ERROR, format(args)); return LOGGER_PROXY; }

    //protected static final LoggerImpl LOGGER_INSTANCE = new LoggerImpl();




    protected static LoggerInterface getLoggerImpl(){
        return LOGGER_PROXY;
    }

    public static LoggerInterface tab(){
        return tab(1);
    }
    public static LoggerInterface tab(int n){ nextPrintIndent += n; return LOGGER_PROXY; }

    public static LoggerInterface caller(int depth) {
        Logger.nextCallerDepth = depth;
        return LOGGER_PROXY;
    }
}

