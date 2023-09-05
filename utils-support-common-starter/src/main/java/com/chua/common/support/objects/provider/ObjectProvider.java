package com.chua.common.support.objects.provider;

import com.chua.common.support.lang.proxy.BridgingMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;
import com.chua.common.support.utils.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 对象提供程序
 *
 * @author CH
 * @since 2023/09/03
 */
public class ObjectProvider<T> {

    private final Class<T> targetType;
    private final Map<String, T> sortedList;
    private final TypeDefinitionSourceFactory typeDefinitionSourceFactory;

    public ObjectProvider(Class<T> targetType, Map<String, T> sortedList, TypeDefinitionSourceFactory typeDefinitionSourceFactory) {
        this.targetType = targetType;
        this.sortedList = sortedList;
        this.typeDefinitionSourceFactory = typeDefinitionSourceFactory;
    }


    /**
     * 第一个元素
     *
     * @return {@link T}
     */
    public T get() {
         T bean = sortedList.isEmpty() ? null : CollectionUtils.findFirst(sortedList.values());
         if(null == bean) {
             return null;
         }

         if(targetType.isAssignableFrom(bean.getClass())) {
             return bean;
         }

         return ProxyUtils.proxy(targetType, targetType.getClassLoader(), new BridgingMethodIntercept<T>(targetType, bean));
    }

    /**
     * 获取全部
     *
     * @return {@link List}<{@link TypeDefinition}>
     */
    public Collection<T> getAll() {
        return sortedList.values();
    }

    /**
     * 为空
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return sortedList.isEmpty();
    }
}
