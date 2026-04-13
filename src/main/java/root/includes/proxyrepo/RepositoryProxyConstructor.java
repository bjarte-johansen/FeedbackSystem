package root.includes.proxyrepo;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.persistence.Table;


public class RepositoryProxyConstructor {
    public static ArrayList<String> MODEL_PACKAGE_SEARCH_PATH = new ArrayList<>(List.of(
        "root.models",
        "root.includes.quicktests.repofun"
    ));
    private static final Map<String, Class<?>> CACHE = new ConcurrentHashMap<>();


    static String getTableName(Class<?> cls) {
        Table t = cls.getAnnotation(Table.class);
        return (t != null && !t.name().isBlank())
            ? t.name()
            : cls.getSimpleName(); // fallback
    }

    public static String getDeveloperWarningMessages() {
        return List.of(
            "WARNING: RepositoryProxyConstructor.MODEL_PACKAGE_SEARCH_PATH is used to search for model classes when " +
                "creating repository proxy instances. Make sure to include all packages that contain model classes in " +
                "this list, otherwise repository proxies may fail to create properly."
            ).toString();

    }

    private static Object createRepoImpl(Class<?> repoInterface) {
        try {
            Class<?> implClass = Class.forName(repoInterface.getName() + "CustomImpl");
            return implClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    private static Class<?> resolveModelClass(String entityName) {
        return CACHE.computeIfAbsent(entityName, name -> {
            for (String pkg : MODEL_PACKAGE_SEARCH_PATH) {
                try {
                    return Class.forName(pkg + "." + name);
                } catch (Exception _) {

                }
            }
            throw new RuntimeException("Model not found: " + name);
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> repoInterfaceClass) {
        try {
//            Logger.log("creating instance of repo implementation class " + repoInterfaceClass.getName() + "CustomImpl");
            Object repoImpl = createRepoImpl(repoInterfaceClass);
            if (repoImpl == null) repoImpl = new Object();

            String repoInterfaceName = repoInterfaceClass.getSimpleName();


            // extract entity name from repo interface name (ex interface name TestRepository -> entity name is Test)
            int i = repoInterfaceName.indexOf("Repository");
            if (i == -1) throw new RuntimeException("repo interface name must contain 'Repository'");

            String entityName = repoInterfaceName.substring(0, i);

//            Logger.log("repoInterfaceName: " + repoInterfaceName);
//            String printOnlyRepoImplClassName = (repoImpl != null) ? repoImpl.getClass().getSimpleName() : "";
//            Logger.log("repoImplClassName: " + printOnlyRepoImplClassName);
//            Logger.log("entityName: " + entityName);
//            Logger.log("tableName: " + entityName);

            Class<?> modelClass = resolveModelClass(entityName);
            String tableName = getTableName(modelClass);

//            Logger.log("modelClass: " + modelClass.getName());

            Map<String, Object> options = Map.of(
                "tableName", tableName,
                "modelClass", modelClass
            );

//            Logger.log("creating proxyinstance: " + repoInterfaceClass.getName() + " with impl " + repoImpl.getClass().getName());

            return (T) Proxy.newProxyInstance(
                repoInterfaceClass.getClassLoader(),
                new Class[]{repoInterfaceClass},
                new RepositoryProxyImpl<T>(repoImpl, options)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
