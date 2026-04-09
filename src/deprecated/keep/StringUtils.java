package root.common.utils;

import java.util.Objects;

@Deprecated
public class StringUtils {
    public static String DEFAULT_QUOTE_STRING = "\"";

    public static String dquoteString(String str, String q) {
        return quotedString(str, "\"");
    }
    public static String squoteString(String str, String q) {
        return quotedString(str, "'");
    }
    public static String quotedString(String str) {
        return quotedString(str, DEFAULT_QUOTE_STRING);
    }
    public static String quotedString(String str, String q) {
        return q + str + q;
    }

    //

    public static String embedParens(String str) { return "(" + str + ")"; }
    public static String embedBrackets(String str) { return "[" + str + "]"; }
    public static String embedBraces(String str) { return "{" + str + "}"; }
    public static String embedAngleBrackets(String str) { return "<" + str + ">"; }

    public static String embed(String str, String left, String right) { return left + str + right; }
    public static String embed(String str, String wrapper) { return wrapper + str + wrapper; }

    public static String embedTicks(String str){ return "`" + str + "`"; }
    public static String embedTag(String str, String tag) { return "<" + tag + ">" + str + "</" + tag + ">"; }

    public static String embedQuoted(String str, String q) { return q + str + q; }
    public static String embedDoubleQuoted(String str){ return "\"" + str + "\""; }
    public static String embedSingleQuoted(String str){ return '\'' + str + '\''; }





    /*
     String methods
     */

    static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
    static boolean isEmpty(String s) {
        return s == null || s.isBlank();
    }



    /*
    checks if two strings are equal, treating null as empty string
     */

    static boolean equals(String a, String b){
        return (a == b) || (a != null && a.equals(b));
    }

    static boolean equalsIgnoreCase(String a, String b){
        return (a == b) || (a != null && a.equalsIgnoreCase(b));
    }

    static String defaultIfNull(String s, String def) {
        return (s != null) ? s : def;
    }

    static String trim(String s) {
        return (s == null) ? null : s.strip();
    }

    static String emptyOrBlank(String s){
        return (s == null || s.isBlank()) ? null : s;
    }

    static String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
