package root.includes.logger;

public class StringUtils {
    /*
    @Deprecated
    private static String padRightAnsiString(String s, int new_width, char pad_char) {
        return padRight(s, AnsiStringUtils.length(s), new_width, pad_char);
    }
     */

    public static String padRight(String s, int old_width, int new_width, char pad_char) {
        if(s == null) return "";
        int c = new_width - old_width;
        if(c <= 0) return s;

        return s + String.valueOf(pad_char).repeat(c);
    }
}
