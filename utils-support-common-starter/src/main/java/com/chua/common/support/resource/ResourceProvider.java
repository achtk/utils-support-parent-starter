package com.chua.common.support.resource;

import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.resource.finder.*;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.Map;
import java.util.Set;

import static com.chua.common.support.resource.ResourceType.*;

/**
 * 资源查找器
 *
 * @author CH
 */
public class ResourceProvider {

    private static final Map<ClassLoader, Map<String, ResourceProvider>> LINK = new ConcurrentReferenceHashMap<>(512);
    private static final Map<String, Class<? extends ResourceFinder>> CACHE = new ConcurrentReferenceHashMap<>(12);

    private static final EmptyResourceFinder EMPTY_RESOURCE_FINDER = new EmptyResourceFinder(ResourceConfiguration.DEFAULT);

    private final Map<String, Store> cache = new ConcurrentReferenceHashMap<>(512);
    static {
        CACHE.put(FILESYSTEM.name(), FileSystemResourceFinder.class);
        CACHE.put(CLASSPATH.name(), ClassPathResourceFinder.class);
        CACHE.put(CLASSPATH_ANY.name(), ClassPathAnyResourceFinder.class);
        CACHE.put(FILESYSTEM_ANY.name(), FileSystemAnyResourceFinder.class);
        CACHE.put(SUBTYPE.name(), SubtypeResourceFinder.class);
        CACHE.put("method", MethodAnnotationResourceFinder.class);
        CACHE.put("type", TypeAnnotationResourceFinder.class);
    }

    private final String name;
    private final ResourceFinder resourceFinder;

    /**
     * 初始化
     *
     * @param name           名称
     * @param resourceFinder 初始化
     */
    private ResourceProvider(String name, ResourceFinder resourceFinder) {
        this.name = name;
        this.resourceFinder = resourceFinder;
    }

    /**
     * 存储器
     * @return 存储器
     */
    public Store getStore() {
        Store ifPresent = cache.get(name);
        if(null != ifPresent) {
            return ifPresent;
        }

        Set<Resource> resources = resourceFinder.find(name);
        DelegateStore store = new DelegateStore(resources);
        cache.put(name, store);
        return store;
    }

    /**
     * 查找器
     *
     * @return 查找器
     */
    public Set<Resource> getResources() {
        return getStore().getResource();
    }

    /**
     * 查找器
     *
     * @return 查找器
     */
    public Resource getResource() {
        return CollectionUtils.findFirst(getResources());
    }

    /**
     * 添加实现
     *
     * @param name 名称
     * @param type 实现类
     */
    public static void addFinder(String name, Class<? extends ResourceFinder> type) {
        CACHE.put(name, type);
    }
    /**
     * 初始化
     *
     * @param name 名称
     * @return this
     */
    public static ResourceProvider of(String name) {
        return of(name, ResourceConfiguration.DEFAULT);
    }
    /**
     * 初始化
     *
     * @param name 名称
     * @param configuration 配置
     * @return this
     */
    public static ResourceProvider of(String name, ResourceConfiguration configuration) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        name = name.contains(":") ? name : "classpath:" + name;
        return LINK
                .computeIfAbsent(configuration.getClassLoader(), it -> new ConcurrentReferenceHashMap<>(512))
                .computeIfAbsent(name, s -> {
                    int index = s.indexOf(":");
                    String type = s.substring(0, index).replaceAll("(\\*){1,}", "_ANY");
                    String name1 = s.substring(index + 1);

                    Class<? extends ResourceFinder> aClass = CACHE.get(type.toUpperCase());
                    if (null == aClass) {
                        return new ResourceProvider(name1, EMPTY_RESOURCE_FINDER);
                    }
                    return new ResourceProvider(name1, ClassUtils.forObject(aClass, configuration));
                });
    }

}
