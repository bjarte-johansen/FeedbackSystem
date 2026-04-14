package root.includes;

import java.util.*;

/**
 * Utility class for building SQL WHERE clause expressions with parameterized arguments. It allows chaining multiple
 * conditions and supports common patterns like BETWEEN and IN. The class maintains a list of SQL expressions and their
 * corresponding arguments, which can be used to generate the final WHERE clause and its parameters for prepared
 * statements.
 *
 * Todo: write unit tests for this class, especially for edge cases like empty sets, null values, and different data types.
 *  Remove comment here after you are done
 *  TODO: WRite javadoc comments for all public methods in this class, and add more helper methods if needed (e.g. for OR conditions, NOT IN, etc.)
 */

public class WhereExpressionList {
    public static int DEFAULT_SIZE_HINT = 20;

    private List<String> expressions;
    private List<Object> arguments;

    public WhereExpressionList() {
        this(DEFAULT_SIZE_HINT);
    }

    public WhereExpressionList(int sizeHint) {
        expressions = new ArrayList<>(sizeHint);
        arguments = new ArrayList<>(sizeHint);
    }

    public WhereExpressionList create() {
        return new WhereExpressionList(DEFAULT_SIZE_HINT);
    }

    public WhereExpressionList create(int size) {
        return new WhereExpressionList(size);
    }

    public String toSql(boolean includeWhereKeyword) {
        String whereSql = expressions.isEmpty() ? "(1=1)" : String.join(" AND ", expressions);
        return (includeWhereKeyword ? " WHERE " : "") + whereSql;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public WhereExpressionList where(String expr, Object... args) {
        if (expr != null && !expr.isEmpty()) {
            expressions.add(expr);
        }

        if (args != null && args.length > 0) {
            this.arguments.addAll(Arrays.asList(args));
        }

        return this;
    }

    public <T> WhereExpressionList whereBetween(String columnName, T start, T end) {
        if (start != null && end != null) {
            String s = String.format("((%s >= ?) AND (%s <= ?))", columnName, columnName);
            this.where(s, start, end);
        } else if (start != null) {
            String s = String.format("(%s >= ?)", columnName);
            this.where(s, start);
        } else if (end != null) {
            String s = String.format("(%s <= ?)", columnName);
            this.where(s, end);
        }

        return this;
    }

    public WhereExpressionList whereIn(String columnName, Set<Integer> data) {
        if (data != null && !data.isEmpty()) {
            String sql = buildInClauseSql(columnName, data);
            this.where(sql);
        }

        return this;
    }

    public WhereExpressionList whereIn(String columnName, Set<Integer> data, boolean isPossiblyContiguous) {
        if (data != null && !data.isEmpty()) {
            if (data.size() == 1) {
                // if only one element, use equality for better performance
                int singleValue = data.iterator().next();
                return this.where("(" + columnName + " = ?)", singleValue);
            }

            if (isPossiblyContiguous && isContiguous(data)) {
                // if the set is contiguous, use between for better performance
                int min = Collections.min(data);
                int max = Collections.max(data);

                return this.whereBetween(columnName, min, max);
            }

            return this.whereIn(columnName, data);
        }

        return this;
    }



    /*
     */

    private static boolean isRealType(Class<?> cls) {
        return (cls == Float.class || cls == Double.class
            || cls == float.class || cls == double.class);
    }

    private static boolean isIntegralType(Class<?> cls) {
        return (cls == Byte.class || cls == Short.class || cls == Integer.class || cls == Long.class
            || cls == byte.class || cls == short.class || cls == int.class || cls == long.class
            || cls == java.math.BigInteger.class);
    }

    private static <T extends Number> String toCsv(Set<T> set) {
        if (set == null || set.isEmpty()) return "";

        // get rough storage capacity per element based on type of first element (assuming all elements are of the same type)
        Number it = set.iterator().next();
        boolean is_real = isRealType(it.getClass());
        boolean is_integral = !is_real && isIntegralType(it.getClass());
        if (!is_real && !is_integral) throw new IllegalArgumentException("Set elements must be of integral or real numeric type");

        int rough_capacity_per_element = is_real ? 26 : 20;

        // use StringBuilder for efficient string concatenation
        StringBuilder sb = new StringBuilder(set.size() * (1 + rough_capacity_per_element));

        boolean first = true;
        for (Number n : set) {
            if (!first) sb.append(',');
            sb.append(n);
            first = false;
        }

        return sb.toString();
    }


    /*
    build in clause
     */

    private <T extends Number> String buildInClauseSql(String columnName, List<T> valueList) {
        if (valueList == null || valueList.isEmpty()) {
            return "(1=1)";
        }

        return buildInClauseSql(columnName, new HashSet<>(valueList));
    }

    private <T extends Number> String buildInClauseSql(String columnName, Set<T> filterSet) {
        if (filterSet == null || filterSet.isEmpty()) {
            return "(1=1)";
        }

        if (filterSet.size() == 1) {
            // if only one element, use equality for better performance
            T singleValue = filterSet.iterator().next();
            return "(" + columnName + " = " + singleValue + ")";
        }

        return "(" + columnName + " IN (" + toCsv(filterSet) + "))";
    }



    /*
    check if a set of integers is contiguous (i.e. forms a continuous range without gaps).
     */

    private static boolean isContiguous(Set<Integer> set) {
        if (set == null || set.isEmpty()) return false;

        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;

        // find min/max in one pass
        for (int v : set) {
            if (v < min) min = v;
            if (v > max) max = v;
        }

        // contiguous ⇔ size == range length
        return set.size() == (max - min + 1);
    }
}
