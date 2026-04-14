package root.common.utils;


import java.util.function.Supplier;


/**
 * A utility class for checking preconditions of method arguments and state. Provides static methods to check for
 * nullity, argument conditions, and instance types, throwing IllegalArgumentException with appropriate messages if the
 * checks fail.
 */

public class Preconditions {

    // throw if o is null, with message

    /**
     * Checks that the specified object reference is not null and throws a customized NullPointerException if it is.
     * This method is designed primarily for doing parameter validation in methods and constructors with multiple
     * parameters.
     *
     * @param o
     * @param message
     * @param <T>
     * @return
     */
    public static <T> T checkNotNull(T o, String message) {
        if (o == null) {
            throw new NullPointerException(message);
        }
        return o;
    }

    /**
     * Checks that the specified object reference is not null and throws a customized NullPointerException if it is.
     * This method is designed primarily for doing parameter validation in methods and constructors with multiple
     * parameters.
     *
     * @param o
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> T checkNotNull(T o, Supplier<String> msg) {
        if (o == null) {
            throw new NullPointerException(msg.get());
        }
        return o;
    }


    /**
     * Checks that the specified object reference is not null and throws a customized NullPointerException if it is.
     * This method is designed primarily for doing parameter validation in methods and constructors with multiple
     * parameters.
     *
     * @param o
     * @param <T>
     * @return
     */
    public static <T> T checkNotNull(T o) {
        if (o == null) {
            throw new NullPointerException("Argument must not be null");
        }
        return o;
    }


    //-- throw if condition is false, with message

    /**
     * Checks the truth of an expression involving one or more parameters to the calling method.
     *
     * @param condition
     */
    public static void checkArgument(boolean condition) {
        if (!condition) throw new IllegalArgumentException("Argument condition not met");
    }

    /**
     * Checks the truth of an expression involving one or more parameters to the calling method.
     *
     * @param condition
     * @param message
     */
    public static void checkArgument(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }


    /**
     * Checks
     *
     * @param condition
     * @param fmt
     * @param args
     */
    public static void checkArgument(boolean condition, String fmt, Object... args) {
        if (!condition) throw new IllegalArgumentException(String.format(fmt, args));
    }


    /**
     * Checks the truth of an expression involving one or more parameters to the calling method.
     *
     * @param condition
     * @param msg
     */
    public static void checkArgument(boolean condition, Supplier<String> msg) {
        if (!condition) throw new IllegalArgumentException(msg.get());
    }



    /** -- throw if condition is false, with message */
    public static void checkState(boolean condition) {
        if (!condition) throw new IllegalStateException("State condition not met");
    }

    /**-- throw if condition is false, with message */

    public static void checkState(boolean condition, String msg) {
        if (!condition) throw new IllegalStateException(msg);
    }


    /**
     * Checks the truth of an expression involving the state of the calling instance, but not involving any parameters
     * to the calling method.
     *
     * @param condition
     * @param fmt
     * @param args
     */
    public static void checkState(boolean condition, String fmt, Object... args) {
        if (!condition) throw new IllegalStateException(String.format(fmt, args));
    }



    //-- throw if o is not instance of clazz, with message

    /**
     * Checks that the specified object is an instance of the specified class and throws a customized
     * IllegalArgumentException if it is not.
     *
     * @param o
     * @param clazz
     * @param msg
     */
    public static void checkIsInstance(Object o, Class<?> clazz, String msg) {
        if (!clazz.isInstance(o)) throw new IllegalArgumentException(msg);
    }

    /**
     * Checks that the specified object is an instance of the specified class and throws a customized
     * IllegalArgumentException if it is not.
     *
     * @param o
     * @param clazz
     * @param msg
     */
    public static void checkIsInstance(Object o, Class<?> clazz, Supplier<String> msg) {
        if (!clazz.isInstance(o)) throw new IllegalArgumentException(msg.get());
    }

    /**
     * Checks that the specified object is an instance of the specified class and throws a customized
     * IllegalArgumentException if it is not.
     *
     * @param o
     * @param clazz
     */
    public static void checkIsInstance(Object o, Class<?> clazz) {
        if (!clazz.isInstance(o)) {
            String msg = "Expected instance of " + clazz.getName() + ", but got " + (o != null ? o.getClass().getName() : "null");
            throw new IllegalArgumentException(msg);
        }
    }


    //-- throw if index is out of bounds for size, with message

    /**
     * Checks that index is a valid element index into a list, string, or array with the specified size.
     *
     * @param index
     * @param size
     * @param msg
     */
    public static int checkElementIndex(int index, int size, String msg) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException(msg);
        return index;
    }

    /**
     * Checks that index is a valid element index into a list, string, or array with the specified size.
     *
     * @param index
     * @param size
     * @param msg
     */
    public static int checkElementIndex(int index, int size, Supplier<String> msg) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException(msg.get());
        return index;
    }

    /**
     * Checks that index is a valid element index into a list, string, or array with the specified size.
     *
     * @param index
     * @param size
     */
    public static int checkElementIndex(int index, int size) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        return index;
    }


    //-- throw if index is out of bounds for size, with message

    /**
     * Checks that index is a valid position index into a list, string, or array with the specified size.
     *
     * @param index
     * @param size
     * @param msg
     */
    public static int checkPositionIndex(int index, int size, Supplier<String> msg) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException(msg.get());
        return index;
    }

    /**
     * Checks that index is a valid position index into a list, string, or array with the specified size.
     *
     * @param index
     * @param size
     * @param msg
     */
    public static int checkPositionIndex(int index, int size, String msg) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException(msg);
        return index;
    }

    /**
     * Checks that index is a valid position index into a list, string, or array with the specified size.
     *
     * @param index
     * @param size
     */
    public static int checkPositionIndex(int index, int size) {
        if (index < 0 || index > size) {
            String msg = "Index: " + index + ", Size: " + size;
            throw new IndexOutOfBoundsException(msg);
        }
        return index;
    }


    //-- throw if index is out of bounds for size, with message

    /**
     * Checks that start and end are valid position indexes into a list, string, or array with the specified size, and
     * that start is less than or equal to end.
     *
     * @param start
     * @param end
     * @param size
     * @param msg
     */
    public static void checkPositionIndexes(int start, int end, int size, String msg) {
        if (start < 0 || end < start || end > size) throw new IndexOutOfBoundsException(msg);
    }

    /**
     * Checks that start and end are valid position indexes into a list, string, or array with the specified size, and
     * that start is less than or equal to end.
     *
     * @param start
     * @param end
     * @param size
     * @param msg
     */
    public static void checkPositionIndexes(int start, int end, int size, Supplier<String> msg) {
        if (start < 0 || end < start || end > size) throw new IndexOutOfBoundsException(msg.get());
    }

}
