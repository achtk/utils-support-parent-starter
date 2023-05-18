package com.chua.common.support.task.cache;

import com.chua.common.support.configuration.CacheConfiguration;
import com.chua.common.support.date.DateUtils;
import com.chua.common.support.expression.ExpressionParser;
import com.chua.common.support.proxy.DelegateMethodIntercept;
import com.chua.common.support.proxy.ProxyUtils;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.PreconditionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 缓存加载器
 *
 * @author CH
 */
public class CacheProxy {

    private CacheManager cacheManager;
    /**
     * 超时时间
     */
    private final long globalTimeout;

    public CacheProxy(CacheManager cacheManager, long globalTimeout) {
        this.cacheManager = cacheManager;
        this.globalTimeout = globalTimeout;
    }

    /**
     * 初始化
     *
     * @param globalTimeout 全局超时时间
     * @return 初始化
     */

    public static CacheProxy createDefault(long globalTimeout) {
        return new CacheProxy(CacheManager.getInstance(), globalTimeout);
    }

    /**
     * 初始化
     *
     * @param globalTimeout 全局超时时间
     * @return 初始化
     */

    public static CacheProxy create(long globalTimeout) {
        return new CacheProxy(new CacheManager(), globalTimeout);
    }

    /**
     * 初始化
     *
     * @return 初始化
     */

    public static CacheProxy create() {
        return new CacheProxy(new CacheManager(), 60_000);
    }

    /**
     * 初始化
     *
     * @return 初始化
     */

    public static CacheProxy createDefault() {
        return new CacheProxy(CacheManager.getInstance(), 60_000);
    }

    /**
     * 代理
     *
     * @param target 类型
     * @param <T>    类型
     * @return 对象
     */
    public <T> T create(Class<T> target) {
        T forObject = ClassUtils.forObject(target);
        PreconditionUtils.notNull(forObject, "对象无法创建");
        return create(forObject, target);
    }

    /**
     * 代理
     *
     * @param target 类型
     * @param <T>    类型
     * @return 对象
     */
    public <T> T create(T target) {
        PreconditionUtils.notNull(target, "对象无法创建");
        return create(target, (Class<T>) target.getClass());
    }

    /**
     * 代理
     *
     * @param target      对象
     * @param targetClass 类型
     * @param <T>         类型
     * @return 对象
     */
    public <T> T create(T target, Class<T> targetClass) {

        return ProxyUtils.newProxy(targetClass, new DelegateMethodIntercept<>(targetClass, proxyMethod -> {
            Method method = proxyMethod.getMethod();
            if (!hasCache(method)) {
                return ClassUtils.invokeMethod(method, target, proxyMethod.getArgs());
            }

            ExpressionParser expressionParser = new ExpressionParser();
            expressionParser.setParserVariable(method, proxyMethod.getArgs());
            String name = getCacheName(method);
            String cacheKey = expressionParser.compile(getCacheKey(method)).getStringValue();
            Cacheable cacheable = getCacheable(name, method);

            if ((getCacheAnnotation(method, Cache.class)) != null) {
                Object o = cacheable.get(cacheKey);
                if (null != o) {
                    return o;
                }
            }


            if ((getCacheAnnotation(method, CachePut.class)) != null) {
            }

            CacheEvict cacheEvict = null;
            if ((cacheEvict = getCacheAnnotation(method, CacheEvict.class)) != null) {
                boolean allEntries = cacheEvict.allEntries();
                if (allEntries) {
                    cacheable.clear();
                } else {
                    cacheable.remove(cacheKey);
                }
            }


            Object o = ClassUtils.invokeMethod(method, target, proxyMethod.getArgs());
            cacheable.put(cacheKey, o);
            return o;
        }));
    }

    /**
     * 缓存实现
     *
     * @param name   名称缓存
     * @param method 类型
     * @return 缓存实现
     */
    private synchronized Cacheable getCacheable(String name, Method method) {
        Cacheable cacheable1 = cacheManager.getCacheable(name);
        if (null != cacheable1) {
            return cacheable1;
        }

        Integer cacheTime = getCacheTime(method);
        Cacheable cacheable = ServiceProvider.of(Cacheable.class).getNewExtension(getCacheImpl(method));
        CacheConfiguration configuration = new CacheConfiguration();
        configuration.setExpireAfterWrite(null == cacheTime ? globalTimeout : cacheTime);

        cacheable.configuration(configuration);
        cacheManager.addCacheable(name, cacheable);
        return cacheable;

    }

    /**
     * 缓存名称
     *
     * @param method 方法
     * @return 缓存名称
     */
    private Integer getCacheTime(Method method) {
        Cache cache = method.getDeclaredAnnotation(Cache.class);
        if (null != cache) {
            return Math.toIntExact(DateUtils.toDuration(cache.timeout()).toMillis());
        }

        return null;
    }

    /**
     * 缓存名称
     *
     * @param method 方法
     * @return 缓存名称
     */
    private String getCacheImpl(Method method) {
        Cache cache = method.getDeclaredAnnotation(Cache.class);
        if (null != cache) {
            return cache.type();
        }

        CacheEvict cacheEvict = method.getDeclaredAnnotation(CacheEvict.class);
        if (null != cacheEvict) {
            return cacheEvict.type();
        }

        CachePut cachePut = method.getDeclaredAnnotation(CachePut.class);
        if (null != cachePut) {
            return cachePut.type();
        }

        return "guava";
    }

    /**
     * 缓存名称
     *
     * @param method 方法
     * @return 缓存名称
     */
    private String getCacheName(Method method) {
        Cache cache = method.getDeclaredAnnotation(Cache.class);
        if (null != cache) {
            return cache.value();
        }

        CacheEvict cacheEvict = method.getDeclaredAnnotation(CacheEvict.class);
        if (null != cacheEvict) {
            return cacheEvict.value();
        }

        CachePut cachePut = method.getDeclaredAnnotation(CachePut.class);
        if (null != cachePut) {
            return cachePut.value();
        }

        return null;
    }

    /**
     * 缓存名称
     *
     * @param method 方法
     * @return 缓存名称
     */
    private String getCacheKey(Method method) {
        Cache cache = method.getDeclaredAnnotation(Cache.class);
        if (null != cache) {
            return cache.key();
        }

        CacheEvict cacheEvict = method.getDeclaredAnnotation(CacheEvict.class);
        if (null != cacheEvict) {
            return cacheEvict.key();
        }

        CachePut cachePut = method.getDeclaredAnnotation(CachePut.class);
        if (null != cachePut) {
            return cachePut.key();
        }

        return null;
    }

    /**
     * 是否存在缓存
     *
     * @param method     方法
     * @param annotation 注解
     * @return 是否存在缓存
     */
    private <T extends Annotation> T getCacheAnnotation(Method method, Class<T> annotation) {
        return method.getDeclaredAnnotation(annotation);
    }

    /**
     * 是否存在缓存
     *
     * @param method 方法
     * @return 是否存在缓存
     */
    private boolean hasCache(Method method) {
        return
                null != method.getDeclaredAnnotation(Cache.class) ||
                        null != method.getDeclaredAnnotation(CacheEvict.class) ||
                        null != method.getDeclaredAnnotation(CachePut.class);
    }

}
