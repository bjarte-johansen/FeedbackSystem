package root.includes.logger;


import root.includes.logger.colorschemes.AnsiColorScheme;
import root.includes.logger.colorschemes.IntelliJDarkAnsi;

public class SyntaxHighlighter {
    public static AnsiColorScheme colorScheme = new IntelliJDarkAnsi();

    public static AnsiColorScheme setColorScheme(AnsiColorScheme colorScheme) {
        AnsiColorScheme prev = SyntaxHighlighter.colorScheme;
        SyntaxHighlighter.colorScheme = colorScheme;
        return prev;
    }

    protected static void append(StringBuilder sb, String s, int start, int end, String color) {
        sb.append(color).append(s, start, end).append(colorScheme.getReset());
    }

    public static String highlight(String s) {
        StringBuilder out = new StringBuilder(s.length() * 4);

        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);

            // ----- STRING -----
            if (c == '"') {
                int start = i++;
                while (i < s.length()) {
                    if (s.charAt(i) == '"' && s.charAt(i - 1) != '\\') {
                        i++;
                        break;
                    }
                    i++;
                }

                append(out, s, start, i, colorScheme.getString());
            }

            // ----- SYMBOL -----
            else if (isSymbolOrPunct(c)) {
                append(out, String.valueOf(c), 0, 1, colorScheme.getSymbol());
                i++;
            }

            // ----- NUMBER -----
            else if (Character.isDigit(c)) {
                int start = i++;
                while (i < s.length() && Character.isDigit(s.charAt(i)))
                    i++;
                append(out, s, start, i, colorScheme.getNumber());
            }

            // ----- IDENTIFIER -----
            else if (Character.isLetter(c) || c == '_') {
                int start = i++;
                while (i < s.length()) {
                    char cc = s.charAt(i);
                    if (!Character.isLetterOrDigit(cc) && cc != '_') break;
                    i++;
                }
                append(out, s, start, i, colorScheme.getIdentifier());
            }

            else {
                // other
                out.append(c);
                i++;
            }
        }

        return out.toString();
    }

    private static final int SYMBOL_OR_PUNCT_MASK =
        (1 << Character.MATH_SYMBOL)
            | (1 << Character.CURRENCY_SYMBOL)
            | (1 << Character.MODIFIER_SYMBOL)
            | (1 << Character.OTHER_SYMBOL)
            | (1 << Character.DASH_PUNCTUATION)
            | (1 << Character.START_PUNCTUATION)
            | (1 << Character.END_PUNCTUATION)
            | (1 << Character.CONNECTOR_PUNCTUATION)
            | (1 << Character.OTHER_PUNCTUATION);

    public static boolean isSymbolOrPunct(char c) {
        return (SYMBOL_OR_PUNCT_MASK & (1 << Character.getType(c))) != 0;
    }
}
