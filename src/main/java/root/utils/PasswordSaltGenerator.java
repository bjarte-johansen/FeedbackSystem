package root.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class PasswordSaltGenerator {
    private static final SecureRandom RND = new SecureRandom();

    public static String generate(int bytes) {
        byte[] salt = new byte[bytes];
        RND.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
