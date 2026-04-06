package root.app;

import java.lang.reflect.Array;
import java.util.Arrays;

@Deprecated
public class GenericArrayUtils {
    public static Object[] makeEmptyArray(Class<?> componentType) {
        return (Object[]) Array.newInstance(componentType, 0);
    }

    public static Class<?> getArrayComponentType(Object[] arr) {
        return (arr != null) ? arr.getClass().getComponentType() : Object.class;
    }

    public static Object[] cloneArrayStructure(Object[] arr, int newLength) {
        Class<?> componentType = getArrayComponentType(arr);
        return (Object[]) Array.newInstance(componentType, newLength);
    }

    public static Object[] makeEmptyArray(Object[] arr) {
        Class<?> componentType = getArrayComponentType(arr);
        return (Object[]) Array.newInstance(componentType, 0);
    }

    public static String safeArrayToString(Object[] arr) {
        arr = arr != null ? arr : makeEmptyArray(getArrayComponentType(arr));
        return Arrays.toString(arr);
    }
}
