package root.includes;

import java.security.SecureRandom;


/**
 * Utility class for generating random numeric strings, often used as verification digits.
 * TODO: Any code inserted here must have javadoc comments. This is a requirement for all code in this project.
 */

public class VerificationCodeDigitsGenerator {
    // SecureRandom is a cryptographically strong random number generator, suitable for generating verification digits
    private static final SecureRandom secureRandom = new SecureRandom();


    /**
     * Generates a random numeric string of the specified length.
     *
     * @param numberOfDigits The length of the numeric string to generate.
     * @return A random numeric string of the specified length.
     */

    public static String generate(int numberOfDigits) {
        if(numberOfDigits <= 0) throw new IllegalArgumentException("Number of digits must be positive.");

        char[] out = new char[numberOfDigits];

        for(int i=0; i<numberOfDigits; i++) {
            out[i] = (char) ('0' + secureRandom.nextInt(10));
        }

        return new String(out);
    }
}