package root.includes.utils;

import root.app.AppConfig;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * SqlSchemaNameValidator is a utility class that provides a method to validate SQL schema names. It uses a regular
 * expression to ensure that the schema name starts with a letter or underscore, followed by any combination of letters,
 * digits, or underscores. If the schema name does not match the pattern, an IllegalArgumentException is thrown.
 */

public class SqlSchemaNameValidator {
    // postgres identifiers: start with letter/_ then letters/digits/_
    private static final int MAX_LEN = 63;

    public static void validateSchemaName(String s) {
        if(s == null || s.isEmpty())
            throw new IllegalArgumentException("Schema name cannot be null or empty");

        if (!AppConfig.VALID_SCHEMA_NAME_PATTERN.matcher(s).matches())
            throw new IllegalArgumentException("Invalid schema name: " + s);
    }

//    public static boolean isValid(String s) {
//        if (!AppConfig.VALID_SCHEMA_NAME_PATTERN.matcher(s).matches())
//            throw new IllegalArgumentException("Invalid schema name: " + s);
//        return true;
//    }
}
