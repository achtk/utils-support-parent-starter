package com.chua.common.support.lang.proxy.plugin.processor;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.objects.definition.element.MethodDescribe;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.task.cache.Cache;
import com.chua.common.support.task.cache.CacheConfiguration;
import com.chua.common.support.task.cache.Cacheable;
import com.chua.common.support.unit.TimeUnit;
import com.chua.common.support.value.TimeValue;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

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
        String timeout = cache.timeout();
        String type = cache.type();
        Cacheable<String, Object> cacheable = referenceHashMap.computeIfAbsent(type, new Function<String, Cacheable<String, Object>>() {
            @Override
            public Cacheable<String, Object> apply(String s) {
                Cacheable<String, Object> cacheable = ServiceProvider.of(Cacheable.class).getExtension(type);
                cacheable.configuration(CacheConfiguration.builder().build());
                return cacheable;
            }
        });

        return cacheable.getOrPut(getKey(entity, args), new Supplier<Object>() {
            @Override
            public Object get() {
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
                return TimeValue.of(value, duration);            }
        }).getValue();
    }

    private String getKey(Object entity, Object[] args) {
        return "" + entity.hashCode() + Arrays.hashCode(args);
    }

    @Override
    public boolean hasAnnotation(Method method) {
        return method.isAnnotationPresent(Cache.class);
    }


}
