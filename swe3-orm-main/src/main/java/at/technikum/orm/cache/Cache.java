package at.technikum.orm.cache;


import at.technikum.orm.annotations.Id;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class Cache {

    private final static Map<Class<?>, Map<Object, Object>> cache = new HashMap<>();
    private static Field field;

    public static void put(Object o) {
        Class<?> clazz = o.getClass();
        Map<Object, Object> entityCache = cache.get(clazz);
        if (entityCache == null) {
            entityCache = new HashMap<>();
            cache.put(clazz, entityCache);
        }
        entityCache.put(extractObjectKey(o), o);

    }

    public static Object get(Class<?> clazz, Object id) {
        Map<Object, Object> entityCache = cache.get(clazz);
        Object o = entityCache.get(id);
        if(o !=null){
            log.debug("Cache HIT");
        }
        return o;
    }

    @SneakyThrows
    private static Object extractObjectKey(Object o) {
        Optional<Field> idField = Arrays.stream(o.getClass().getDeclaredFields()).filter(field -> field.getAnnotation(Id.class) != null
        ).findFirst();
        if (idField.isEmpty()) {
            idField = Arrays.stream(o.getClass().getSuperclass().getDeclaredFields()).filter(field -> field.getAnnotation(Id.class) != null
            ).findFirst();
        }
        field = idField.get();
        field.setAccessible(true);
        Object key = field.get(o);
        log.debug("generated cache key: {}", key);
        return key;
    }
}
