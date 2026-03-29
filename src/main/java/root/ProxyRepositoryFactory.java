package root;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import root.interfaces.ReviewRepositoryCustom;
import root.logger.Logger;
import root.repositories.*;

import java.lang.reflect.Proxy;
import java.util.Map;

@Configuration
public class ProxyRepositoryFactory<INTERFACE, IMPL> {
    private static Object createRepoImpl(Class<?> repoInterface) {
        try {
            Class<?> implClass = Class.forName(repoInterface.getName() + "CustomImpl");
            return implClass.getDeclaredConstructor().newInstance();
        }catch(Exception e) {
            return null;
        }
    }

    public static <T> ReviewRepositoryCustom createReviewRepository(Object impl, Map<String, Object> options) {
        return (ReviewRepositoryCustom) Proxy.newProxyInstance(
            ReviewRepositoryCustom.class.getClassLoader(),
            new Class[]{ReviewRepositoryCustom.class},
            new RepoProxy<T>(impl, options)
            );
    }

    @Bean
    public static ReviewRepository createReviewRepository(){ return create(ReviewRepository.class); }

    @Bean
    public static ReviewerRepository createReviewerRepository(){ return create(ReviewerRepository.class); }

    @Bean
    public static TenantRepository createTeanatRepository(){ return create(TenantRepository.class); }

    public static <T> T create(Class<T> repoInterfaceClass) {
        try {
            Logger.log("creating instance of repo implementation class " + repoInterfaceClass.getName() + "CustomImpl");
            Object repoImpl = createRepoImpl(repoInterfaceClass);

            String repoInterfaceName = repoInterfaceClass.getSimpleName();
            String repoImplClassName = repoImpl != null ? repoImpl.getClass().getSimpleName() : "";

            // extract entity name from repo interface name, for example if repo interface is TenantRepository then entity name is Tenant
            int i = repoInterfaceName.indexOf("Repository");
            if(i == -1) throw new RuntimeException("repo interface name must contain 'Repository'");
            String entityName = repoInterfaceName.substring(0, i);

            Logger.log("repoInterfaceName: " + repoInterfaceName);
            Logger.log("repoImplClassName: " + repoImplClassName);
            Logger.log("entityName: " + entityName);
            Logger.log("tableName: " + entityName + "s");


            Class<?> modelClass = Class.forName("root.models." + entityName);
            if (modelClass == null) throw new Exception("model class not found for entity " + entityName);

            Logger.log("modelClass: " + modelClass.getName());

            Map<String, Object> options = Map.of(
                "tableName", entityName,
                "modelClass", modelClass
            );

            Logger.log("creating proxyinstance: " + repoInterfaceClass.getName() + " with impl " + repoImpl.getClass().getName());

            return (T) Proxy.newProxyInstance(
                repoInterfaceClass.getClassLoader(),
                new Class[]{repoInterfaceClass},
                new RepoProxy<T>(repoImpl, options)
            );
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
    public static <T> TenantRepositoryCustom createTenantRepo(Object impl, Map<String, Object> options) {
        return (TenantRepositoryCustom) Proxy.newProxyInstance(
            TenantRepositoryCustom.class.getClassLoader(),
            new Class[]{TenantRepositoryCustom.class},
            new RepoProxy<T>(impl, options)
        );
    }

    public static <T> FantasyRepoCustom createFantasyRepo(Object impl, Map<String, Object> options) {
        return (FantasyRepoCustom) Proxy.newProxyInstance(
            FantasyRepoCustom.class.getClassLoader(),
            new Class[]{FantasyRepoCustom.class},
            new RepoProxy<T>(impl, options)
            );
    }
    */
}
