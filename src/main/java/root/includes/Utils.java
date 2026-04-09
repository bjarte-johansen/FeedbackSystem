package root.includes;

import root.app.AppConfig;

import java.util.ArrayList;
import java.util.List;

import static root.common.utils.Preconditions.checkArgument;


/**
 * A utility class containing various helper methods. This class is not meant to be instantiated.
 * TODO: Consider splitting this class into multiple classes based on functionality (e.g., StringUtils, CollectionUtils)
 *  if it grows too large.
 */

public class Utils {

    /**
     * Checks if a string has non-whitespace text. Returns true if the string is not null and contains at least one
     * non-whitespace character. Returns false for null, empty, or whitespace-only strings.
     * TODO: Consider renaming this method to isNotBlank for clarity, as "hasText" can be ambiguous.
     *
     * @param s
     * @return
     */

    public static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }


    /**
     * @see #parseCsvIntList(String, String)
     */

    public static List<Integer> parseCsvIntList(String s) {
        return parseCsvIntList(s, ",");
    }


    /**
     * Parses a comma-separated string of integers into a list of integers. Example: "1, 2, 3" -> List.of(1, 2, 3) -
     * Handles null or empty input by returning an empty list. - Trims whitespace around numbers.
     *
     * @param s the comma-separated string to parse
     * @return a list of integers parsed from the input string
     * @throws NumberFormatException if any of the parts cannot be parsed as an integer
     * <p>
     * TODO: write unit test for it with various inputs, including edge cases (null, empty string, strings with
     *  extra spaces, invalid numbers)
     */

    public static List<Integer> parseCsvIntList(String s, String delimiter) {
        if (s == null || s.isEmpty()) return List.of();

        String[] parts = s.split(delimiter);

        List<Integer> result = new ArrayList<>(parts.length);
        for (String p : parts) {
            result.add(Integer.parseInt(p.trim()));
        }
        return result;
    }


    /**
     * Checks if the provided email is valid according to the regex pattern defined in AppConfig.VALID_EMAIL_REGEX. The
     * email must not be null or empty, and must match the specified regex pattern. If any of these conditions are not
     * met, an IllegalArgumentException is thrown with an appropriate message.
     *
     * @param email
     * @return true if valid, otherwise an exception is thrown
     */

    public static boolean requireValidEmail(String email) {
        checkArgument(email != null, "Email cannot be null");
        checkArgument(!email.isEmpty(), "Email cannot be empty");
        checkArgument(email.matches(AppConfig.VALID_EMAIL_REGEX), "Invalid email format");
        return true;
    }


    /**
     * Checks if the provided password meets the complexity requirements defined in AppConfig.VALID_PASSWORD_REGEX. The
     * password must not be null or empty, and must match the specified regex pattern. If any of these conditions are
     * not met, an IllegalArgumentException is thrown with an appropriate message.
     *
     * @param password
     * @return true if valid, otherwise an exception is thrown
     */

    public static boolean requireValidPassword(String password) {
        checkArgument(password != null, "Password cannot be null");
        checkArgument(!password.isEmpty(), "Password cannot be empty");
        checkArgument(password.length() >= AppConfig.MIN_PASSWORD_LENGTH, "Password must be at least " + AppConfig.MIN_PASSWORD_LENGTH + " characters long");
        checkArgument(password.matches(AppConfig.VALID_PASSWORD_REGEX), "Password does not meet complexity requirements");
        return true;
    }
}
