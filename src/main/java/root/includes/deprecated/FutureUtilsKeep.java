package root.includes.deprecated;

@Deprecated
public class FutureUtilsKeep {
    boolean isCmpOp(String op) {
        if(op == null) return false;
        return switch (op) {
            case ">", ">=", "<", "<=", "=", "<>" -> true;
            default -> false;
        };
    }

    boolean isMathOp(String op) {
        if(op == null) return false;
        return op.length() == 1 &&
            (op.charAt(0) == '+' || op.charAt(0) == '-' ||
                op.charAt(0) == '*' || op.charAt(0) == '/' ||
                op.charAt(0) == '%');
    }

    boolean isValidExprOp(String op) {
        return isCmpOp(op) || isMathOp(op);
    }
}
