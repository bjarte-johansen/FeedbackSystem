package root.models;

import java.security.SecureRandom;


/**
 * Utility class for generating random numeric strings, often used as verification digits.
 */

class VerificationDigitsGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();


    /**
     * Generates a random numeric string of the specified length.
     *
     * @param numberOfDigits The length of the numeric string to generate.
     * @return A random numeric string of the specified length.
     */

    public static String generate(int numberOfDigits)
    {
        char[] out = new char[numberOfDigits];

        for(int i=0; i<numberOfDigits; i++) {
            out[i] = (char) ('0' + secureRandom.nextInt(10));
        }

        return new String(out);
    }
}