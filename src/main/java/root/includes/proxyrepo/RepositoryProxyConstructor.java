package root.includes.proxyrepo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import root.includes.quicktests.quicktests.repofun.FantasyRepository;
import root.models.ReviewVote;
import root.repositories.*;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RepositoryProxyConstructor {
    public static ArrayList<String> MODEL_PACKAGE_SEARCH_PATH = new ArrayList<>(List.of(
        "root.models",
        "root.repofun"
    ));

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

    private static Class<?> resolveModelClass(String entityName) throws Exception {
        for (String packageName : MODEL_PACKAGE_SEARCH_PATH) {
            try {
                return Class.forName(packageName + "." + entityName);
            } catch (Exception _) {}
        }

        throw new RuntimeException("model class not found for entity " + entityName + " in package searchpath");
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

//            Logger.log("modelClass: " + modelClass.getName());

            Map<String, Object> options = Map.of(
                "tableName", entityName,
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
