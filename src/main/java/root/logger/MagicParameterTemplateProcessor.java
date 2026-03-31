package root.logger;

import java.util.LinkedHashMap;
import java.util.function.Function;

class MagicParameterTemplateProcessor {
    private static LinkedHashMap<String, Function<LoggerStackFrame, String>> PARAM_PROCESSOR_MAP = null;

    private static LinkedHashMap<String, Function<LoggerStackFrame, String>> getOrCreateParamProcessingMap() {
        if (PARAM_PROCESSOR_MAP == null) {
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

    private static boolean matchString(String src, int fromSrcIndex, String other) {
        return src.regionMatches(true, fromSrcIndex, other, 0, other.length());
    }

    public static <T> String format(String fmt, LoggerStackFrame F) {
        if (fmt == null || fmt.isEmpty()) return "";

        StringBuilder sb = new StringBuilder(fmt.length() + 64);

        // TODO: optimize by scanning for '@' first and only doing region matches when found, to avoid unnecessary
        //  regionMatches calls when fmt is long and has few parameters (AKA theres no need to append characters one
        //  by one when there are no parameters, the common case for short formats like "@classMethod" or "@link")


        LinkedHashMap<String, Function<LoggerStackFrame, String>> paramProcessors = getOrCreateParamProcessingMap();

        outer:
        for (int i = 0, n = fmt.length(); i < n; ) {
            char ch = fmt.charAt(i);

            if (ch == '@') {
                for (var entry : paramProcessors.entrySet()) {
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
