package root.includes;

/**
 * Utility class for working with bit flags.
 */

public class BitFlags {
    /**
     * Checks if a specific flag is set in the given flags integer.
     * @param flags
     * @param flag
     * @return
     */
    public static boolean has(int flags, int flag) {
        return (flags & flag) != 0;
    }

    /**
     * Sets a specific flag in the given flags integer.
     * @param flags
     * @param flag
     * @return
     */

    public static int set(int flags, int flag) {
        return flags | flag;
    }

    /**
     * Clears a specific flag in the given flags integer.
     * @param flags
     * @param flag
     * @return
     */
    public static int clear(int flags, int flag) {
        return flags & ~flag;
    }

    /**
     * Toggles a specific flag in the given flags integer.
     * If the flag is set, it will be cleared; if it is clear, it will be set.
     * @param flags
     * @param flag
     * @return
     */

    public static int toggle(int flags, int flag) {
        return flags ^ flag;
    }
}
