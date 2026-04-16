package root.A_TODO;

import root.models.Tenant;

import java.util.Optional;
import java.util.function.Function;

interface EvictableCache<K, V> {
    Tenant get(K k);
    void put(K k, V v);
    Optional<Tenant> computeIfAbsent(K k, Function<K, V> mappingFunction);

    void evictAll();
    void evict(K k);
}