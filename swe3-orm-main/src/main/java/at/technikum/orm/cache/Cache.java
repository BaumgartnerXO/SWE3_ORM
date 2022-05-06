package at.technikum.orm.cache;


import at.technikum.orm.annotations.Id;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Very Simple Cache using javas implementation of HashMap.
 * WARNING: This Cache is not production ready!
 */

@Slf4j
public class Cache {

    private final Map<Class<?>, Map<Object, Object>> cache = new HashMap<>();

    /**
     * Adds an object to the cache
     * @param o
     */
    public void put(Object o) {
        Class<?> clazz = o.getClass();
        Map<Object, Object> entityCache = cache.get(clazz);
        if (entityCache == null) {
            entityCache = new HashMap<>();
            cache.put(clazz, entityCache);
        }
        entityCache.put(extractObjectKey(o), o);
    }

    /**
     * removes object from cache
     * @param o
     */
    public void remove(Object o) {
        Class<?> clazz = o.getClass();
        Map<Object, Object> entityCache = cache.get(clazz);
        if (entityCache == null) {
            entityCache = new HashMap<>();
            cache.put(clazz, entityCache);
        }
        entityCache.put(extractObjectKey(o), null);
    }

    public <T> T get(Class<T> clazz, Object id) {
        if (id == null) {
            return null;
        }
        Map<Object, Object> entityCache = cache.get(clazz);
        if (entityCache == null) {
            return null;
        }
        Object o = entityCache.get(id);
        if (o != null) {
            log.debug("Cache HIT");
        }
        return (T) o;
    }

    /**
     * Only supports Entities that have an @Id field.
     *
     */
    @SneakyThrows
    private static Object extractObjectKey(Object o) {
        Optional<Field> idField = Arrays.stream(o.getClass().getDeclaredFields()).filter(field -> field.getAnnotation(Id.class) != null
        ).findFirst();
        if (idField.isEmpty()) {
            idField = Arrays.stream(o.getClass().getSuperclass().getDeclaredFields()).filter(field -> field.getAnnotation(Id.class) != null
            ).findFirst();
        }
        Field field = idField.get();
        field.setAccessible(true);
        Object key = field.get(o);
        log.debug("generated cache key: {}", key);
        return key;
    }
}