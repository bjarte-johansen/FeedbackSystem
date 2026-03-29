package root;

import java.util.List;
import java.util.Optional;

public interface ProxyRepository<ENTITY, ID> {
    List<ENTITY> findAll();
    Optional<ENTITY> findById(ID id);
    ENTITY save(ENTITY entity);
    void deleteById(ID id);
    long count();

    void delete(ENTITY entity);

    void deleteAll(Iterable<ENTITY> entities);
    void deleteAllInBatch(Iterable<ENTITY> entities);

    void deleteAllById(ID id);
    void deleteAllByIdInBatch(Iterable<ID> ids);
}
