package root.models;


/*
Supplied by ChatGPT, see https://chat.openai.com/share/1e5b9c8e-7a0c-4d2b-9f1c-8a3e5b6c9d2f

Notes:
    very fast (2 ops/char)
    deterministic
    fits PostgreSQL/MySQL BIGINT
    collision risk low for typical IDs/emails but not cryptographic
 */

@Deprecated
public class FNV1A64HashGenerator {
    /**
     * Generate a 64-bit FNV-1a hash of the input string.
     *
     * @param s The input string to hash.
     * @return The 64-bit FNV-1a hash of the input string.
     */
    public static long generate(String s) {
        long h = 0xcbf29ce484222325L;

        byte[] b = s.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        for (byte v : b) {
            h ^= (v & 0xff);
            h *= 0x100000001b3L;
        }

        return h;
    }
}
