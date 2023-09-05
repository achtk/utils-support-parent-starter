package com.chua.common.support.objects.provider;

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

    private final Map<String, T> sortedList;
    private final TypeDefinitionSourceFactory typeDefinitionSourceFactory;

    public ObjectProvider(Map<String, T> sortedList, TypeDefinitionSourceFactory typeDefinitionSourceFactory) {
        this.sortedList = sortedList;
        this.typeDefinitionSourceFactory = typeDefinitionSourceFactory;
    }


    /**
     * 第一个元素
     *
     * @return {@link T}
     */
    public T get() {
        return sortedList.isEmpty() ? null : CollectionUtils.findFirst(sortedList.values());
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
