package root.database;


public class CaseConverter {

    /**
     * Convert snake_case to camelCase
     * In part written by chatgpt and modified by us. The core logic is from older project + suggestions from chatgpt.
     *
     * @param s The snake_case string to convert.
     * @return The converted camelCase string.
     */

    public static String underscoreToPascal(String s) {
        int n = s.length();
        StringBuilder out = new StringBuilder(n);

        boolean upper = false;

        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);

            if (c == '_') {
                upper = true;
            }else {
                out.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }

        return out.toString();
    }


    /**
     * Convert camelCase to snake_case
     * In part written by chatgpt and modified by us. The core logic is from older project + suggestions from chatgpt.
     *
     * @param s The camelCase string to convert.
     * @return The converted snake_case string.
     */

    public static String camelToSnake(String s) {
        StringBuilder out = new StringBuilder(s.length() + 8);

        for (int i = 0, n = s.length(); i < n; i++) {
            char c = s.charAt(i);

            if (c >= 'A' && c <= 'Z') {
                if (i > 0) {
                    out.append('_');
                }

                c = Character.toLowerCase(c);
            }

            out.append(c);
        }

        return out.toString();
    }
}
