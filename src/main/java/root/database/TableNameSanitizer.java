package root.database;

import java.util.regex.Pattern;


/**
 * Utility class for sanitizing and validating table names to prevent SQL injection. This class provides methods to
 * check if a table name is valid and to sanitize it by ensuring it matches a safe pattern. A valid table name must
 * start with a letter or underscore, followed by letters, digits, or underscores only.
 * <p>
 * The point of the class is NOT to modify the table name, but to validate it and throw an exception if it is not valid.
 * This is a common approach to prevent SQL injection when using dynamic table names in SQL queries.
 */

public class TableNameSanitizer {
    private static final Pattern SAFE = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");


    /**
     * Checks if the provided table name is safe to use in SQL queries. A safe table name must start with a letter or
     * underscore, followed by letters, digits, or underscores only.
     *
     * This is a utility method that can be used with to validate table names before using them in SQL queries to
     * prevent SQL injection
     *
     * @param name The table name to check.
     * @return True if the table name is valid and safe to use in SQL queries, false otherwise.
     */

    public static boolean isValidTableName(String name) {
        return (name != null) && SAFE.matcher(name).matches();
    }


    /**
     * Sanitizes the provided table name by validating it against a safe pattern. If the table name is valid, it is
     * returned as is; otherwise, an IllegalArgumentException is thrown.
     *
     * @param name The table name to sanitize.
     * @throws IllegalArgumentException if the table name is invalid.
     * @return The sanitized table name if it is valid.
     */

    public static String sanitize(String name) {
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("Invalid table name: " + name);
        }

        return name;
    }


    /**
     * Checks if the provided table name is safe to use in SQL queries. A safe table name must start with a letter or
     * underscore, followed by letters, digits, or underscores only.
     * This method will throw an IllegalArgumentException if the table name is invalid.
     *
     * This is a utility method that can be used with import static to validate table names before using them in
     * SQL queries to prevent SQL injection
     *
     * @param name The table name to check.
     * @throws IllegalArgumentException if the table name is invalid.
     * @return The validated table name if it is safe.
     */

    public static String checkSafeTableName(String name) {
        return sanitize(name);
    }


    /**
     * Checks if the provided table name is safe to use in SQL queries. A safe table name must start with a letter or
     * underscore, followed by letters, digits, or underscores only.
     * This method will throw an IllegalArgumentException if the table name is invalid.
     *
     * This is a utility method that can be used with import static to validate table names before using them in
     * SQL queries to prevent SQL injection
     *
     * @param name The table name to check.
     * @throws IllegalArgumentException if the table name is invalid.
     * @return The validated table name if it is safe.
     */

    public static String validateSafeTableName(String name) {
        return sanitize(name);
    }
}
