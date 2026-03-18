package root;

import root.models.interfaces.IReviewRepository;
import root.models.repositories.JdbcReviewRepository;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Map;

public class ReviewRepository {
    public static <T> IReviewRepository create(Connection c, Map<String, Object> options) {
        JdbcReviewRepository impl = new JdbcReviewRepository();

        return (IReviewRepository) Proxy.newProxyInstance(
            IReviewRepository.class.getClassLoader(),
            new Class[]{IReviewRepository.class},
            new RepoProxy<T>(impl, options)
            );
    }
}
