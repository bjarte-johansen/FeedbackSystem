package root.common.utils;

public class KissWordWrapper {
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
