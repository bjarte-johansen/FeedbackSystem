package root.common.utils;


import java.util.function.Supplier;

/**
 * A utility class for checking preconditions of method arguments and state.
 * Provides static methods to check for nullity, argument conditions, and instance types, throwing IllegalArgumentException with appropriate messages if the checks fail.
 */

public class Preconditions {

    // throw if o is null, with message

    public static <T> T checkNotNull(T o, String message) {
        if (o == null) {
            throw new NullPointerException(message);
        }
        return o;
    }

    public static <T> T checkNotNull(T o, Supplier<String> msg) {
        if (o == null) {
            throw new NullPointerException(msg.get());
        }
        return o;
    }

    public static <T> T checkNotNull(T o) {
        if (o == null) {
            throw new NullPointerException("Argument must not be null");
        }
        return o;
    }



    //-- throw if condition is false, with message

    public static void checkArgument(boolean condition) {
        if(!condition) throw new IllegalArgumentException("Argument condition not met");
    }
    public static void checkArgument(boolean condition, String message) {
        if(!condition) throw new IllegalArgumentException(message);
    }
    public static void checkArgument(boolean condition, Supplier<String> msg) {
        if (!condition) throw new IllegalArgumentException(msg.get());
    }



    //-- throw if o is not instance of clazz, with message

    public static void checkInstanceOf(Object o, Class<?> clazz, String msg) {
        if (!clazz.isInstance(o)) throw new IllegalArgumentException(msg);
    }

    public static void checkInstanceOf(Object o, Class<?> clazz, Supplier<String> msg) {
        if (!clazz.isInstance(o)) throw new IllegalArgumentException(msg.get());
    }

    public static void checkInstanceOf(Object o, Class<?> clazz) {
        if(!clazz.isInstance(o)) {
            String msg = "Expected instance of " + clazz.getName() + ", but got " + (o != null ? o.getClass().getName() : "null");
            throw new IllegalArgumentException(msg);
        }
    }



    //-- throw if o is not instance of clazz, with message, better names
/*
    public static void checkArgumentInstanceOf(Object o, Class<?> clazz, String msg) {
        if (!clazz.isInstance(o)) throw new IllegalStateException(msg);
    }

    public static void checkArgumentInstanceOf(Object o, Class<?> clazz, Supplier<String> msg) {
        if (!clazz.isInstance(o)) throw new IllegalStateException(msg.get());
    }

    public static void checkArgumentInstanceOf(Object o, Class<?> clazz) {
        if(!clazz.isInstance(o)) {
            String msg = "Expected instance of " + clazz.getName() + ", but got " + (o != null ? o.getClass().getName() : "null");
            throw new IllegalArgumentException(msg);
        }
    }
*/


    //-- throw if index is out of bounds for size, with message

    public static void checkElementIndex(int index, int size, String msg) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException(msg);
    }

    public static void checkElementIndex(int index, int size, Supplier<String> msg) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException(msg.get());
    }

    public static void checkElementIndex(int index, int size) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }



    //-- throw if index is out of bounds for size, with message

    public static void checkPositionIndex(int index, int size, Supplier<String> msg) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException(msg.get());
    }

    public static void checkPositionIndex(int index, int size, String msg) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException(msg);
    }

    public static void checkPositionIndex(int index, int size) {
        if (index < 0 || index > size){
            String msg = "Index: " + index + ", Size: " + size;
            throw new IndexOutOfBoundsException(msg);
        }
    }



    //-- throw if index is out of bounds for size, with message

    public static void checkPositionIndexes(int start, int end, int size, String msg) {
        if (start < 0 || end < start || end > size) throw new IndexOutOfBoundsException(msg);
    }

    public static void checkPositionIndexes(int start, int end, int size, Supplier<String> msg) {
        if (start < 0 || end < start || end > size) throw new IndexOutOfBoundsException(msg.get());
    }

}
