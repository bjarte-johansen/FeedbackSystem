package root.includes;

import java.security.SecureRandom;
import java.util.Base64;


/**
 * Utility class for generating password salts.
 */

public class PasswordSaltGenerator {
    private static final SecureRandom RND = new SecureRandom();

    /**
     * Generates a random salt of the specified byte length and encodes it as a URL-safe Base64 string without padding.
     *
     * @param bytes
     * @return
     */
    public static String generate(int bytes) {
        byte[] salt = new byte[bytes];
        RND.nextBytes(salt);
        return Base64
            .getUrlEncoder()
            .withoutPadding()
            .encodeToString(salt);
    }
}
