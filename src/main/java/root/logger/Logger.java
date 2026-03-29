package root.logger;


import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Logger {
    protected static LoggerConfiguration cfg = new LoggerConfiguration();

    public static ThreadLocal<Integer> depth = ThreadLocal.withInitial(() -> 0);
    public static ConcurrentHashMap<Integer, String> indentCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, String> SpaceCharCache = new ConcurrentHashMap<>();

    private static final String newlineString = System.lineSeparator();
    private static final String tabString = "\t";

    private static final String BLOCK_HEADER_BACKGROUND = "\033[48;2;40;40;40m";   // background
    private static final String BLOCK_BACKGROUND_RESET = "\033[0m";

    public static int nextPrintIndent = 0;

    public static final String GREEN        = "\u001B[32m";
    public static final String DARK_GRAY    = "\u001B[90m";
    public static final String LIGHT_GRAY   = "\u001B[37m";
    public static final String RESET        = "\u001B[0m";
    public static final String UNDERLINE    = "\u001B[4m";
    public static final String BOLD         = "\u001B[1m";
    public static final String REVERSE      = "\u001B[7m";

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

    public static class MagicParameterTemplateProcessor {
        private static LinkedHashMap<String, Function<LoggerStackFrame, String>> PARAM_PROCESSOR_MAP = null;

        private static LinkedHashMap<String, Function<LoggerStackFrame, String>> getOrCreateParamProcessingMap(){
            if(PARAM_PROCESSOR_MAP == null) {
                PARAM_PROCESSOR_MAP = new LinkedHashMap<>();
                PARAM_PROCESSOR_MAP.put("@classMethod", LoggerStackFrame::getClassAndMethod);
                PARAM_PROCESSOR_MAP.put("@class", LoggerStackFrame::getSimpleName);
                PARAM_PROCESSOR_MAP.put("@method", LoggerStackFrame::getMethodName);
                PARAM_PROCESSOR_MAP.put("@link", (F) -> F.getSourceLink(" @ "));
                PARAM_PROCESSOR_MAP.put("@line", (F) -> String.valueOf(F.getLineNumber()));
                PARAM_PROCESSOR_MAP.put("@file", LoggerStackFrame::getFileName);
            }

            return PARAM_PROCESSOR_MAP;
        }
        private static boolean matchString(String src, int fromSrcIndex, String other){
            return src.regionMatches(true, fromSrcIndex, other, 0, other.length());
        }

        public static <T> String format(String fmt, LoggerStackFrame F) {
            if(fmt == null || fmt.isEmpty()) return "";

            StringBuilder sb = new StringBuilder(fmt.length() + 64);

            // TODO: optimize by scanning for '@' first and only doing region matches when found, to avoid unnecessary
            //  regionMatches calls when fmt is long and has few parameters (AKA theres no need to append characters one
            //  by one when there are no parameters, the common case for short formats like "@classMethod" or "@link")


            LinkedHashMap<String, Function<LoggerStackFrame, String>> paramProcessors = getOrCreateParamProcessingMap();

            outer:
            for (int i = 0, n = fmt.length(); i < n; ) {
                char ch = fmt.charAt(i);

                if (ch == '@') {
                    for(var entry : paramProcessors.entrySet()) {
                        String key = entry.getKey();
                        if (matchString(fmt, i, key)) {
                            sb.append(entry.getValue().apply(F));
                            i += key.length();
                            continue outer;
                        }
                    }
                }

                sb.append(ch);
                i++;
            }

            return sb.toString();
        }
    }


    /*
        Indent-handling in setup of try-with entering scope
    */

    public static String colorize(String s, String color) {
        return color + s + RESET;
    }

    public static LoggerScope scope(String title){
        // magic logging
        if (cfg.USE_MAGIC_LOGGING) {
            var callerFrame = LoggerStackFrame.createFromCurrentStack(0);

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
                System.out.println(BLOCK_HEADER_BACKGROUND + colorize(s, GREEN ) + ";" + BLOCK_BACKGROUND_RESET);
            }
        }

        // enter block
        enter();

        // return scope object that will leave block on close
        return () -> {
            // leave block
            leave();

            // magic logging
            String s = switch(cfg.FORMAT_TYPE) {
                case LoggerFormatType.BEGIN_END -> colorize("### ", GREEN) + colorize("LEAVE", LIGHT_GRAY);
                case LoggerFormatType.BLOCK, LoggerFormatType.TITLED_BLOCK -> "}";
                default -> null;
            };

            if(s != null) {
                System.out.print(getIndentPrefixStr());
                System.out.println(s);
            }

            if(cfg.FORMAT_EXTRA_LINE_AFTER_BLOCK) {
                System.out.println();
            }
        };
    }

    public static void enter() { depth.set(depth.get() + 1); }
    public static void leave() { depth.set(depth.get() - 1); }



    /*
        Internal logging method that handles indentation and optional magic logging features.
        This method is synchronized to ensure thread safety when multiple threads are logging
        simultaneously.
    */

    protected static String getIndentPrefixStr(){
        return indentCache.computeIfAbsent(depth.get(), d -> "    ".repeat(Math.max(0, d)));
    }


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

    //

    /*
    @Deprecated
    private static String padRightAnsiString(String s, int new_width, char pad_char) {
        return padRight(s, AnsiStringUtils.length(s), new_width, pad_char);
    }
     */

    static String padRight(String s, int old_width, int new_width, char pad_char) {
        if(s == null) return "";
        int c = new_width - old_width;
        if(c <= 0) return s;

        return s + SpaceCharCache.getOrDefault(c, ("" + pad_char).repeat(c));
    }


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
        int inputLength = input.length();

        synchronized (Logger.class) {
            String magicOut = "";
            String userOut = SyntaxHighlighter.highlight(input);
            StringBuilder out = new StringBuilder(512);

            if(cfg.USE_MAGIC_LOGGING) {
                var callerFrame = LoggerStackFrame.createFromCurrentStack(1 + frameDepthOffset);
                magicOut = DARK_GRAY + MagicParameterTemplateProcessor.format("@classMethod @link", callerFrame) + RESET;
            }

            if(cfg.LAYOUT_ADJUST_MAGIC_RIGHT && !magicOut.isEmpty()) {
                int totalWidth = 104;

                userOut = padRight(userOut, inputLength, totalWidth, ' ');

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
    public static void log(Object... args) { put(EVENT_DEFAULT, args); }
    public static void debug(Object... args) { put(EVENT_DEBUG, args); }
    public static void info(Object... args) { put(EVENT_INFO, args); }
    public static void warn(Object... args) { put(EVENT_WARN, args); }
    public static void error(Object... args) { put(EVENT_ERROR, args); }

    public static void logf(Object... args) { put(EVENT_DEFAULT, format(args)); }
    public static void debugf(Object... args) { put(EVENT_DEBUG, format(args)); }
    public static void infof(Object... args) { put(EVENT_INFO, format(args)); }
    public static void warnf(Object... args) { put(EVENT_WARN, format(args)); }
    public static void errorf(Object... args) { put(EVENT_ERROR, format(args)); }

    public static LoggerImpl logProxy = null;

    public static LoggerImpl tab(){
        return tab(1);
    }
    public static LoggerImpl tab(int n){
        nextPrintIndent += n;
        if(logProxy == null) {
            logProxy = new LoggerImpl();
        }
        return logProxy;
    }
}

