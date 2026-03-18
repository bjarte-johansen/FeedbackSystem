package root.database;

import root.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Utility for parsing SQL with named parameters.
 *
 * Written by ChatGPT-4, javadoc is intentionally left out for brevity and
 * as it is not written by students and thus not part of the assignment.
 *
 * Modified by students to fit the needs of the assignment and to be more robust.
 *
 * Example:
 *
 *   String sql = "SELECT * FROM users WHERE id = :id AND status = :status";
 *   Map<String, Object> namedArgs = Map.of("id", 123, "status", "active");
 *
 *   NamedSql.Parsed parsed = NamedSql.parse(sql, namedArgs);
 *
 *   System.out.println(parsed.sql); // "SELECT * FROM users WHERE id = ? AND status = ?"
 *   System.out.println(Arrays.toString(parsed.args)); // [123, "active"]
 */

public class NamedSql {

    static class Parsed {
        final String sql;
        final Object[] args;

        Parsed(String sql, List<Object> args) {
            this(sql, args.toArray());
        }

        Parsed(String sql, Object[] args) {
            this.sql = sql;
            this.args = args;
        }

        public String toString(){
            return "sql: " + sql + "\n\tpositional-args: " + Arrays.toString(args);
        }
    }

    static Parsed parse(String sql, Map<String,Object> named, Object... positional) {
        /*
        try(var logger = Logger.scope("NamedSql.parse")) {
            Logger.log("Parsing SQL: " + sql);
            Logger.log("Named Arguments: " + named.toString());
            Logger.log("Positional Arguments: " + java.util.Arrays.toString(positional));
        }
        */

        StringBuilder out = new StringBuilder(sql.length());
        List<Object> args = new ArrayList<>();

        int pos = 0;
        int n = sql.length();

        boolean inString = false;
        boolean inIdent = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;

        for (int i = 0; i < n; i++) {
            char c = sql.charAt(i);

            // ---- line comment
            if (inLineComment) {
                out.append(c);
                if (c == '\n') inLineComment = false;
                continue;
            }

            // ---- block comment
            if (inBlockComment) {
                out.append(c);
                if (c == '*' && i + 1 < n && sql.charAt(i + 1) == '/') {
                    out.append('/');
                    i++;
                    inBlockComment = false;
                }
                continue;
            }

            // ---- string literal
            if (inString) {
                out.append(c);
                if (c == '\'') inString = false;
                continue;
            }

            // ---- quoted identifier
            if (inIdent) {
                out.append(c);
                if (c == '"') inIdent = false;
                continue;
            }

            // ---- start comment
            if (c == '-' && i + 1 < n && sql.charAt(i + 1) == '-') {
                out.append("--");
                i++;
                inLineComment = true;
                continue;
            }

            if (c == '/' && i + 1 < n && sql.charAt(i + 1) == '*') {
                out.append("/*");
                i++;
                inBlockComment = true;
                continue;
            }

            // ---- start literal / identifier
            if (c == '\'') {
                out.append(c);
                inString = true;
                continue;
            }

            if (c == '"') {
                out.append(c);
                inIdent = true;
                continue;
            }

            // ---- positional parameter
            if (c == '?') {
                args.add(positional[pos++]);
                out.append('?');
                continue;
            }

            // ---- named parameter
            if (c == ':' && i + 1 < n) {

                // postgres :: cast
                if (sql.charAt(i + 1) == ':') {
                    out.append("::");
                    i++;
                    continue;
                }

                char nc = sql.charAt(i + 1);
                if (Character.isLetter(nc) || nc == '_') {

                    int j = i + 1;
                    while (j < n) {
                        char cc = sql.charAt(j);
                        if (!Character.isLetterOrDigit(cc) && cc != '_') break;
                        j++;
                    }

                    String name = sql.substring(i + 1, j);

                    args.add(named.get(name));
                    out.append('?');

                    i = j - 1;
                    continue;
                }
            }

            out.append(c);
        }

        return new Parsed(out.toString(), args);
    }
}