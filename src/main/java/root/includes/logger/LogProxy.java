package root.includes.logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class LogProxy {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final ConcurrentHashMap<Method, MethodHandle> CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    static <T> T create(Class<T> iface, Class<?> target) {
        return (T) Proxy.newProxyInstance(
            iface.getClassLoader(),
            new Class[]{iface},
            (proxy, method, args) -> {
                MethodHandle mh = CACHE.computeIfAbsent(method, m -> {
                    try {
                        Class<?>[] params = m.getParameterTypes();

                        // fix varargs → ensure Object[] not expanded
                        if (m.isVarArgs()) {
                            params = new Class<?>[]{params[0]}; // Object[]
                        }

                        MethodType type = MethodType.methodType(m.getReturnType(), params);

                        return LOOKUP.findStatic(target, m.getName(), type);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });

                Object result = (args == null)
                    ? mh.invokeExact()
                    : mh.invokeWithArguments(args);

                // return proxy for chaining
                if (method.getReturnType().isInterface())
                    return proxy;

                return result;
            }
        );
    }
}
