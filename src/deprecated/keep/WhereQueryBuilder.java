package root.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Utility class for building SQL WHERE clauses with support for conditional inclusion of conditions and grouping. In
 * part written by chatgpt and heavily modified by us. Supports conditional inclusion of conditions and grouping with
 * AND/OR logic. The core logic is from older project + suggestions from chatgpt.
 * <p>
 * Added conditional group includes, and added better support for AND/OR-Groups.
 */

@Deprecated
public class WhereQueryBuilder {
    private final StringBuilder sql = new StringBuilder();
    private final List<Object> args = new ArrayList<>();


    /**
     * Create a new instance of the WhereQueryBuilder. This is the entry point for building a WHERE clause. You can then
     * chain calls to the where(), whereIf(), orGroup(), and andGroup() methods to construct the desired WHERE clause.
     *
     * @return
     */
    public WhereQueryBuilder create() {
        return new WhereQueryBuilder();
    }


    /**
     * Add a condition to the WHERE clause. If there are already conditions, this will be combined with AND logic.
     *
     * @param cond
     * @param vals
     * @return
     */
    public WhereQueryBuilder where(String cond, Object... vals) {
        if (sql.isEmpty()) {
            sql.append(" WHERE ");
        } else {
            sql.append(" AND ");
        }
        sql.append("(").append(cond).append(")");
        Collections.addAll(args, vals);
        return this;
    }


    /**
     * Add a condition to the WHERE clause if the specified condition is true. This allows for conditional inclusion of
     * conditions based on runtime logic, which can be useful for building dynamic queries.
     *
     * @param ok
     * @param cond
     * @param vals
     * @return
     */
    public WhereQueryBuilder whereIf(boolean ok, String cond, Object... vals) {
        if (ok) {
            where(cond, vals);
        }
        return this;
    }


    /**
     * Add a group of conditions combined with OR logic. The conditions in the group will be combined with AND logic,
     * and the entire group will be combined with OR logic to the existing conditions. This allows for more complex
     * logical combinations of conditions in the WHERE clause.
     *
     * @param fn
     * @return
     */
    public WhereQueryBuilder orGroup(Consumer<WhereQueryBuilder> fn) {
        return orGroupIf(true, fn);
    }


    /**
     * Add a group of conditions combined with OR logic if the specified condition is true. This allows for conditional
     * inclusion of groups of conditions based on runtime logic, which can be useful for building dynamic queries.
     *
     * @param ok
     * @param fn
     * @return
     */
    public WhereQueryBuilder orGroupIf(boolean ok, Consumer<WhereQueryBuilder> fn) {
        if (!ok) return this;

        WhereQueryBuilder sub = new WhereQueryBuilder();
        fn.accept(sub);

        if (!sub.sql.isEmpty()) {
            if (sql.isEmpty()) {
                sql.append(" WHERE ");
            } else {
                sql.append(" AND ");
            }

            sql.append("(")
                .append(
                    sub.sql.substring(" WHERE ".length())
                        .replace(" AND ", " OR ")
                )
                .append(")");
            args.addAll(sub.args);
        }
        return this;
    }


    /**
     * Add a group of conditions combined with AND logic. The conditions in the group will be combined with AND logic,
     * and the entire group will be combined with AND logic to the existing conditions. This allows for more complex
     * logical combinations of conditions in the WHERE clause.
     *
     * @param fn
     * @return
     */
    public WhereQueryBuilder andGroup(Consumer<WhereQueryBuilder> fn) {
        return andGroupIf(true, fn);
    }


    /**
     * Add a group of conditions combined with AND logic if the specified condition is true. This allows for conditional
     * inclusion of groups of conditions based on runtime logic, which can be useful for building dynamic queries.
     *
     * @param ok
     * @param fn
     * @return
     */
    public WhereQueryBuilder andGroupIf(boolean ok, Consumer<WhereQueryBuilder> fn) {
        if (!ok) return this;

        WhereQueryBuilder sub = new WhereQueryBuilder();
        fn.accept(sub);

        if (!sub.sql.isEmpty()) {
            if (sql.isEmpty()) {
                sql.append(" WHERE ");
            } else {
                sql.append(" AND ");
            }

            sql.append("(")
                .append(
                    sub.sql.substring(" WHERE ".length())
                )
                .append(")");
            args.addAll(sub.args);
        }
        return this;
    }


    /**
     *
     */

    public String toString() {
        return build();
    }

    /**
     * Build the final SQL WHERE clause as a string. This method returns the constructed WHERE clause, which can be used
     * in a SQL query. The conditions and groups added to the builder will be combined accordingly based on the logic
     * specified when adding them.
     *
     * @return
     */
    String build() {
        return sql.toString();
    }


    /**
     * Return the list of parameters corresponding to the conditions in the WHERE clause. This list can be used to set
     * the parameters in a prepared statement when executing the SQL query. The order of the parameters corresponds to
     * the order of the conditions and groups added to the builder.
     *
     * @return
     */
    List<Object> params() {
        return args;
    }


    /**
     * Test method to demonstrate the usage of the WhereQueryBuilder. This method constructs a complex WHERE clause
     * using various conditions and groups, and then prints the resulting SQL string and the list of parameters.
     */

    public static void test() {
        WhereQueryBuilder b = new WhereQueryBuilder();
        b.where("a = ?", 1)
            .whereIf(false, "b = ?", 2)
            .whereIf(true, "c = ?", 3)
            .orGroup(g -> {
                g
                    .where("or_outer_g1_a = ?", 4)
                    .where("or_outer_g1_b = ?", 5)
                    .orGroup(g2 -> {
                        g2.where("or_inner_g2_a = ?", 7);
                        g2.where("or_inner_g2_b = ?", 8);
                    });
            })
            .andGroup(g4 -> {
                g4.where("A = ?", 20);
                g4.where("B = ?", 21);
                g4.andGroup(g6 -> {
                    g6.where("C = ?", 20);
                    g6.where("D = ?", 21);
                    g6.orGroup(g7 -> {
                        g7.where("E = ?", 20);
                        g7.where("F = ?", 21);
                    });
                });
            })
            .andGroupIf(true, g5 -> {
                g5.where("and_outer_g5_a = ?", 31);
                g5.where("and_outer_g5_b = ?", 32);
            })
            .orGroupIf(false, g -> {
                g.where("x = ?", 6);
            });

        System.out.println(b.build());
        System.out.println(b.params());

        testOr();
        testOrDouble();
    }

    private static void testOr() {
        var b = new WhereQueryBuilder();
        b.orGroup(g -> {
            g.where("a = ?", 1);
            g.where("b = ?", 2);
        });

        System.out.println(b.build());
        System.out.println(b.params());
    }

    private static void testOrDouble() {
        var b = new WhereQueryBuilder();
        b.orGroup(g -> {
            g.where("a = ?", 1);
            g.where("b = ?", 2);
        }).orGroup(g -> {
            g.where("c = ?", 3);
            g.where("d = ?", 4);
        });

        System.out.println(b.build());
        System.out.println(b.params());
    }
}
