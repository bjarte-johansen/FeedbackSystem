package root.unused;

import static root.common.utils.Preconditions.checkArgument;
import static root.common.utils.Preconditions.checkNotNull;

class FSQLConditions {
    public String[] expressions;

    public static record Condition(String column, String op) {
        private static boolean isIdentifierStartChar(char c) {
            return Character.isLetter(c) || c == '_';
        }

        private static boolean isIdentifierPartChar(char c) {
            return Character.isLetterOrDigit(c) || c == '_' || c == '.';
        }

        public static Condition parse(String column) {
            checkArgument(column != null && !column.isEmpty());

            String op = null;
            String col = null;

            int n = column.length();
            int pos = 0;

            // skip whitespaces
            while (pos < n && Character.isWhitespace(column.charAt(pos))) pos++;

            // skip leading identifier characters
            if (pos < n && isIdentifierStartChar(column.charAt(pos))) pos++;

            // skip trailing identifier characters (fex for table.column)
            while (pos < n && isIdentifierPartChar(column.charAt(pos))) pos++;

            // skip whitespaces
            while (pos < n && Character.isWhitespace(column.charAt(pos))) pos++;

            col = column.substring(0, pos).trim();
            op = column.substring(pos).trim();

            return new Condition(col, op);
        }
    }

    Object[] conditions;

    // utility method to create condition array from varargs ["field op", placeholder, "field op", placeholder, "field op", placeholder]
    public static Object[] paired(Object... args) {
        checkNotNull(args, "args cannot be null");
        checkArgument((args.length & 1) != 0, "args length must be even");

        int n = args.length >>> 1;
        Object[] tmp = new Object[n];

        for (int i = 0; i < n; i++) {
            tmp[i] = args[i * 2];
        }

        return tmp;
    }

    // utility method to create condition array from varargs ["field op", "field op", "field op"]
    public static Object[] compacted(Object... args) {
        return (args == null) ? new Object[0] : args;
    }
}
