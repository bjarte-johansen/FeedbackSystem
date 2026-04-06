package root.common.utils;

/**
 * A simple word wrapper that breaks lines at spaces without breaking words.
 * It does not handle hyphenation or other complex cases, but it is fast and simple.
 */

public class KissWordWrapper {
    /**
     * Wraps the input string to the specified maximum column width, breaking lines at spaces.
     *
     * @param s
     * @param maxColumnWidth
     * @return
     */
    public static String wordwrap(String s, int maxColumnWidth) {
        StringBuilder out = new StringBuilder(s.length());
        int col = 0;

        for (String word : s.split(" ")) {
            int len = word.length();

            if (col != 0 && col + 1 + len > maxColumnWidth) {
                out.append('\n');
                col = 0;
            }

            if (col != 0) {
                out.append(' ');
                col++;
            }

            out.append(word);
            col += len;
        }

        return out.toString();
    }
}
