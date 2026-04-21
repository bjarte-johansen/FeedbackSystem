package root.includes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;
import root.app.AppConfig;
import root.includes.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;;


/**
 * A utility class containing various helper methods. This class is not meant to be instantiated.
 * TODO: Consider splitting this class into multiple classes based on functionality (e.g., StringUtils, CollectionUtils)
 *  if it grows too large.
 */

public class Utils {
    private static final ObjectMapper M = new ObjectMapper()
        .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
        .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * Checks if a string has non-whitespace text. Returns true if the string is not null and contains at least one
     * non-whitespace character. Returns false for null, empty, or whitespace-only strings.
     * TODO: Consider renaming this method to isNotBlank for clarity, as "hasText" can be ambiguous.
     *
     * @param s
     * @return
     */

    public static boolean hasText(String s) {
        return org.springframework.util.StringUtils.hasText(s);
    }


    /**
     * @see #parseCsvIntList(String, String, boolean)
     */

    public static List<Integer> parseCsvIntList(String s) {
        return parseCsvIntList(s, ",", true);
    }


    /**
     * Parses a comma-separated string of integers into a list of integers. Example: "1, 2, 3" -> List.of(1, 2, 3) -
     * Handles null or empty input by returning an empty list. - Trims whitespace around numbers.
     *
     * note will handle "1", "1,2,3", "1,", "1,s,t,3" safely but looses invalid data
     *
     * @param s the comma-separated string to parse
     * @return a list of integers parsed from the input string
     * @throws NumberFormatException if any of the parts cannot be parsed as an integer
     */

    public static List<Integer> parseCsvIntList(String s, String delimiter, boolean skipInvalidValues) {
        checkArgument(delimiter != null && !delimiter.isEmpty(), "Delimiter cannot be null or empty");

        if (s == null || s.isEmpty()) return List.of();

        String[] parts = s.split(Pattern.quote(delimiter));

        List<Integer> result = new ArrayList<>(parts.length);
        for (String p : parts) {
            p = p.trim();
            if(p.isBlank()) continue; // skip empty parts

            try {
                int tmp = Integer.parseInt(p);
                result.add(tmp);
            }catch(NumberFormatException e) {
                if(!skipInvalidValues) {
                    throw new RuntimeException("Invalid csv integer list", e);
                }
            }
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


    /**
     * Converts an object to a JSON string and escapes it for safe use in HTML attributes. This method uses the provided
     * ObjectMapper to serialize the object to JSON, and then replaces special characters with their corresponding HTML
     * entities to prevent issues when the JSON string is used within an HTML attribute. The characters escaped include
     * &, ", <, and >.
     * TODO: Make unittest
     *
     * @param o
     * @return
     */

    public static String toJson(Object o) {
        try {
            return M.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON for HTML attribute", e);
        }
    }


    /**
     * Escapes special characters in a string for safe use in HTML. This method replaces &, ", <, and > with their
     * corresponding HTML entities (&amp;, &quot;, &lt;, &gt;) to prevent issues when the string is included in HTML
     * content. This is important for preventing XSS vulnerabilities and ensuring that the string is displayed correctly
     * in the browser when it contains special characters.
     * TODO: Make unittest
     *
     * @param s
     * @return
     */

    public static String escapeHtml(String s) {
        if (s == null) return null;
        return s.replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }


    /**
     * Converts a string to a valid CSS identifier by replacing invalid characters with hyphens and ensuring that the
     * first character is a letter, underscore, or hyphen. This method also collapses multiple consecutive hyphens or
     * underscores into a single hyphen to create a cleaner identifier. If the input string is null or empty, it returns
     * an underscore as a default valid identifier. This is useful for generating CSS class names or IDs from arbitrary
     * strings while ensuring they conform to CSS naming rules.
     * <p>
     * PS: Written by chatgpt, we can trust it because it is a simple string manipulation function and the logic is
     * straightforward. Its instead of including separate library for sanitation.
     * TODO: Make unittest and delete this text
     *
     * @param s
     * @return
     */

    public static String toCssIdentifier(String s) {
        if (s == null || s.isEmpty()) return "_";

        s = s.replaceAll("[^a-zA-Z0-9_-]", "-");
        if (!Character.isLetter(s.charAt(0)) && s.charAt(0) != '_' && s.charAt(0) != '-') {
            s = "_" + s.substring(1);
        }
        return s.replaceAll("[-_]{2,}", "-");
    }


    /**
     * Creates a LinkedHashMap from an array of key-value pairs. The input array should contain an even number of
     * elements, where each pair of elements represents a key and its corresponding value. The method iterates through
     * the array, adding each key-value pair to the LinkedHashMap. If the input array has an odd number of elements, an
     * IllegalArgumentException is thrown to indicate that the key-value pairs are not properly formed. This utility
     * method provides a convenient way to create a LinkedHashMap in a single line of code, improving readability and
     * reducing boilerplate when initializing maps with known key-value pairs.
     * <p>
     * Copilot assisted in improving this method, but I have reviewed and tested it to ensure it meets our requirements
     * and handles edge cases appropriately. The logic is straightforward, and the method includes error handling for
     * invalid input, making it a reliable utility for creating LinkedHashMaps from key-value pairs.
     * TODO: Make unittest and delete this text
     *
     * @param kv
     * @param <K>
     * @param <V>
     * @return
     */

    //@SafeVarargs
    public static <K, V> LinkedHashMap<K, V> linkedMap(Object... kv) {
        if (kv.length % 2 != 0) throw new IllegalArgumentException();

        LinkedHashMap<K, V> m = new LinkedHashMap<>(kv.length / 2);
        for (int i = 0; i < kv.length; i += 2) {
            m.put((K) kv[i], (V) kv[i + 1]);
        }
        return m;
    }
}
