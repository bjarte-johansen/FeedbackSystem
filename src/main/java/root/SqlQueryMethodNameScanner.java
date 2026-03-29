package root;

import root.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SqlQueryMethodNameScanner {
    private static final boolean EMBED_EXTRA_PARENS_FOR_READABILITY = true;
    public static final int FLAG_NONE = 0;
    public static final int FLAG_EXTRA_PARENS = 1 << 1;
    public static final int FLAG_NO_THROW_ON_ERROR = 1 << 2;

    private static boolean checkFlag(int flags, int flag) {
        return (flags & flag) != 0;
    }

    private static Map<String, String> makeStringStringMap(String... els) {
        if (els.length % 2 != 0) throw new IllegalArgumentException("Expected even number of elements");
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < els.length; i += 2) {
            map.put(els[i], els[i + 1]);
        }
        return map;
    }

    private final static String[] ops = {
        "LessThan", "LessThanEqual",
        "GreaterThan", "GreaterThanEqual",
        "Like", "StartingWith", "EndingWith", "Containing",
        "IsNull", "IsNotNull",
        "NotEquals", "Equals",
        "False", "True"
    };

    final private static Map<String, String> opTranslationMap = makeStringStringMap(
        "LessThan", "<",
        "LessThanEqual", "<=",
        "GreaterThan", ">",
        "GreaterThanEqual", ">=",
        "Like", "LIKE",
        "StartingWith", "LIKE",
        "EndingWith", "LIKE",
        "Containing", "LIKE",
        "IsNull", "IS NULL",
        "IsNotNull", "IS NOT NULL",
        "Equals", "=",
        "NotEquals", "<>"
    );

    private final static String[] methodStarts = {
        "findAll", "findBy", "findCount", "find",
        "deleteAll", "deleteBy", "delete",
        "existsBy", "exists",
        "updateBy", "update",
        "countBy", "count"
    };


    public String sourceString = null;
    public String methodType = null;
    public String whereStr = "";
    public int paramCount = 0;
    public boolean success = false;

    public SqlQueryMethodNameScanner() {
    }

    public SqlQueryMethodNameScanner(String src) {
        scan(src, FLAG_EXTRA_PARENS);
    }

    public SqlQueryMethodNameScanner(String src, int flags) {
        scan(src, flags);
    }

    private static String[] extractFieldAndOperator(String expr) {
        if (expr.isBlank()) throw new IllegalArgumentException("Expression cannot be blank");

        for (String op : ops) {
            if (expr.endsWith(op)) {
                String field = expr.substring(0, expr.length() - op.length());
                return new String[]{field, op};
            }
        }

        return new String[]{expr, null};
    }

    private static String extractOperator(String opExpr) {
        return opTranslationMap.get(opExpr);
    }

    private static String fieldToColumn(String field) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < field.length(); i++) {
            char c = field.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append('_');
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    private static List<String> splitAndOr(String subexpr) {
        /* method written by chatGPT and modified */

        Matcher m = Pattern.compile("(And|Or)").matcher(subexpr);
        List<String> result = new ArrayList<>(32);
        int last = 0;

        while (m.find()) {
            if (m.start() > last) result.add(subexpr.substring(last, m.start()));
            result.add(m.group().toUpperCase()); // And / Or
            last = m.end();
        }

        if (last < subexpr.length()) result.add(subexpr.substring(last));
        return result;
    }

    public SqlQueryMethodNameScanner scan(String methodName, int flags) {
        StringBuilder whereClause = new StringBuilder();

        boolean useExtraParens = checkFlag(flags, FLAG_EXTRA_PARENS) || EMBED_EXTRA_PARENS_FOR_READABILITY;

        String leftParen = useExtraParens ? "(" : "";
        String rightParen = useExtraParens ? ")" : "";

        success = false;
        sourceString = methodName;

        for (String start : methodStarts) {
            if (methodName.startsWith(start)) {
                methodType = methodName.substring(0, start.length());
                List<String> combinerParts = splitAndOr( methodName.substring(start.length()) );

                for (var combinerPart : combinerParts) {
                    if (combinerPart.equals("AND") || combinerPart.equals("OR")) {
                        whereClause.append(" ").append(combinerPart.toUpperCase()).append(" ");
                        continue;
                    }

                    var parts = extractFieldAndOperator(combinerPart);

                    String field = parts[0];
                    String op = parts[1];

                    whereClause.append(leftParen).append(fieldToColumn(field)).append(" ");

                    if (op != null) {
                        if(op.equals("False") || op.equals("True")) {
                            whereClause.append("= ").append(op.toUpperCase());
                        } else {
                            String finalOpStr = extractOperator(op);
                            if (finalOpStr == null) {
                                throw new RuntimeException("Unsupported operator: " + op);
                            }

                            whereClause.append(finalOpStr).append(" ?");
                        }
                    } else {
                        whereClause.append(" = ?");
                    }

                    whereClause.append(rightParen);

                    paramCount++;
                }

                whereStr = (whereClause.isEmpty()) ? "" : leftParen + whereClause.toString() + rightParen;
                success = true;

                return this;
            }
        }

        //if(!checkFlag(flags, FLAG_NO_THROW_ON_ERROR))
          //  throw new RuntimeException("Unsupported method: " + methodName);

        return this;
    }

    @Override
    public String toString() {
        return "SqlQueryMethodNameScanner{" +
                "sourceString='" + sourceString + '\'' +
                ", methodType='" + methodType + '\'' +
                ", whereStr='" + whereStr + '\'' +
                ", paramCount=" + paramCount +
                ", success=" + success +
                '}';
    }
}