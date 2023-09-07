package com.chua.common.support.lang.proxy.plugin.processor;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.objects.definition.element.MethodDescribe;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.task.cache.Cache;
import com.chua.common.support.task.cache.CacheConfiguration;
import com.chua.common.support.task.cache.CacheEvict;
import com.chua.common.support.task.cache.Cacheable;
import com.chua.common.support.unit.TimeUnit;
import com.chua.common.support.value.TimeValue;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

/**
 * @author CH
 */
@Spi(value = "cache", order = 0)
public class CacheMethodAnnotationPostProcessor implements MethodAnnotationPostProcessor<Cache> {

    private final Map<String, Cacheable<String, Object>> referenceHashMap = new ConcurrentReferenceHashMap<>(12);

    @Override
    @SuppressWarnings("ALL")
    public Object execute(MethodDescribe describe, Object entity, Object[] args) {
        Cache cache = describe.getAnnotation(Cache.class);
        if(null != cache) {
            return inCache(cache, describe, entity,  args);
        }

        CacheEvict cacheEvict = describe.getAnnotation(CacheEvict.class);
        return outCache(cacheEvict, describe, entity,  args);
    }

    /**
     * 清除缓存
     *
     * @param cacheEvict 缓存驱逐
     * @param describe   描述
     * @param entity     实体
     * @param args       args
     * @return {@link Object}
     */
    private Object outCache(CacheEvict cacheEvict, MethodDescribe describe, Object entity, Object[] args) {
        String type = cacheEvict.type();
        Cacheable<String, Object> stringObjectCacheable = referenceHashMap.get(type);
        if(null != stringObjectCacheable) {
            stringObjectCacheable.remove(getKey(describe, args));
        }
        try {
            return describe.execute(entity, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *缓存
     *
     * @param cache    缓存
     * @param describe 描述
     * @param entity   实体
     * @param args     args
     * @return {@link Object}
     */
    @SuppressWarnings("ALL")
    private Object inCache(Cache cache, MethodDescribe describe, Object entity, Object[] args) {
        String timeout = cache.timeout();
        String type = cache.type();
        Cacheable<String, Object> cacheable = referenceHashMap.computeIfAbsent(type, s -> {
            Cacheable<String, Object> cacheable1 = ServiceProvider.of(Cacheable.class).getExtension(type);
            cacheable1.configuration(CacheConfiguration.builder().build());
            return cacheable1;
        });

        return cacheable.getOrPut(getKey(describe, args), () -> {
            Duration duration = Duration.ofSeconds(TimeUnit.parse(timeout).toSecond());
            Object value = null;
            try {
                if(describe.hasBean()) {
                    value = describe.bean();
                } else {
                    value = describe.execute(entity, args);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return TimeValue.of(value, duration);            }).getValue();
    }

    private String getKey(MethodDescribe describe, Object[] args) {
        return describe.method().getDeclaringClass().getTypeName() + "#" + describe.method().getName() + Arrays.hashCode(args);
    }

    @Override
    public boolean hasAnnotation(Method method) {
        return method.isAnnotationPresent(Cache.class) || method.isAnnotationPresent(CacheEvict.class);
    }


}
