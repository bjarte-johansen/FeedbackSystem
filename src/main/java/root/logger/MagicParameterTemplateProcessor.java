package root.logger;

import java.util.LinkedHashMap;
import java.util.function.Function;

class MagicParameterTemplateProcessor {
    private static final LinkedHashMap<String, Function<LoggerStackFrame, String>> PARAM_PROCESSOR_MAP = buildParamProcessingMap();
    private static final String[] PARAM_PROCESSOR_MAP_KEYS = buildParamProcessingMapKeyArray(PARAM_PROCESSOR_MAP);

    private static LinkedHashMap<String, Function<LoggerStackFrame, String>> buildParamProcessingMap() {
        var map = new LinkedHashMap<String, Function<LoggerStackFrame, String>>();
        map.put("@classMethod", LoggerStackFrame::getClassAndMethod);
        map.put("@class", LoggerStackFrame::getSimpleName);
        map.put("@method", LoggerStackFrame::getMethodName);
        map.put("@link", (F) -> F.getSourceLink(" @ "));
        map.put("@line", (F) -> String.valueOf(F.getLineNumber()));
        map.put("@file", LoggerStackFrame::getFileName);
        return map;
    }
    private static String[] buildParamProcessingMapKeyArray(LinkedHashMap<String, Function<LoggerStackFrame, String>> map) {
        String[] keys = new String[map.size()];
        int i = 0;
        for (String key : map.keySet()) {
            keys[i++] = key;
        }
        return keys;
    }

    private static boolean startsWithIgnoreCase(String src, int offset, String needle) {
        return src.regionMatches(true, offset, needle, 0, needle.length());
    }

    public static String format(String fmt, LoggerStackFrame F) {
        // TODO: optimize by scanning for '@' first and only doing region matches when found, to avoid unnecessary
        //  regionMatches calls when fmt is long and has few parameters (AKA theres no need to append characters one
        //  by one when there are no parameters, the common case for short formats like "@classMethod" or "@link")

        if (fmt == null || fmt.isEmpty()) return "";

        StringBuilder sb = new StringBuilder(fmt.length() + 64);

        outer:
        for (int i = 0, n = fmt.length(); i < n; ) {
            char ch = fmt.charAt(i);

            if (ch == '@') {
                for(var key : PARAM_PROCESSOR_MAP_KEYS) {
                    if (startsWithIgnoreCase(fmt, i, key)) {
                        sb.append(PARAM_PROCESSOR_MAP.get(key).apply(F));
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
