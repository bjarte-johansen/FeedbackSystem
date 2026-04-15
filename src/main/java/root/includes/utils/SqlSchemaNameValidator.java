package root.includes.utils;

import root.app.AppConfig;

/**
 * SqlSchemaNameValidator is a utility class that provides a method to validate SQL schema names. It uses a regular
 * expression to ensure that the schema name starts with a letter or underscore, followed by any combination of letters,
 * digits, or underscores. If the schema name does not match the pattern, an IllegalArgumentException is thrown.
 */

public class SqlSchemaNameValidator {
    public static void validateSchemaName(String name) {
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Schema name cannot be null or empty");

        if (!AppConfig.VALID_SCHEMA_NAME_PATTERN.matcher(name).matches())
            throw new IllegalArgumentException("Invalid schema name: " + name);
    }
}
