package com.chua.common.support.lang.proxy.plugin;

import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.lang.date.DateUtils;
import com.chua.common.support.reflection.describe.processor.impl.AbstractMethodAnnotationPostProcessor;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.task.cache.Cache;
import com.chua.common.support.task.cache.Cacheable;
import com.chua.common.support.value.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;

/**
 * 缓存注解扫描
 *
 * @author CH
 */
@Slf4j
public class CacheableMethodAnnotationPostProcessor extends AbstractMethodAnnotationPostProcessor<Cache> {

    private static final Map<String, Cacheable> CACHE = new ConcurrentReferenceHashMap<>(512);

    @Override
    public Object execute(Object entity, Object[] args) {
        Cache cache = getAnnotationValue();
        if (null == cache) {
            return invoke(entity, args);
        }

        String group = cache.value();
        Cacheable cacheable = CACHE.computeIfAbsent(group, s -> {
            Cacheable cacheable1 = ServiceProvider.of(Cacheable.class).getNewExtension(cache.type());
            cacheable1.configuration(ImmutableBuilder.<String, Object>builderOfMap().put("expireAfterWrite", DateUtils.toDuration(cache.timeout()).toMillis() * 1000).build());
            return cacheable1;
        });
        if (null == cacheable) {
            return invoke(entity, args);
        }

        int code = entity.hashCode();
        int hashCode = Arrays.hashCode(args);

        return ((Value) cacheable.apply(cache.value() + code + "@" + hashCode, () -> {
            try {
                try {
                    return Value.of(invoke(entity, args));
                } catch (Exception ignored) {
                }
                return Value.of(null);
            } finally {
                if (log.isDebugEnabled()) {
                    log.debug("开始处理数据并加载到缓存, 缓存方法: {}, 缓存名称: {}, 缓存时间: {}, 缓存类型: {}",
                            getMethod().getName(),
                            cache.value(),
                            cache.timeout(),
                            cache.type());
                }
            }
        })).getValue();
    }

    @Override
    public Class<Cache> getAnnotationType() {
        return Cache.class;
    }
}
