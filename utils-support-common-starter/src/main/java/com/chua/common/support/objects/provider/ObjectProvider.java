package com.chua.common.support.objects.provider;

import com.chua.common.support.collection.SortedList;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;

/**
 * 对象提供程序
 *
 * @author CH
 * @since 2023/09/03
 */
public class ObjectProvider<T> {

    private final SortedList<TypeDefinition> sortedList;
    private final TypeDefinitionSourceFactory typeDefinitionSourceFactory;

    public ObjectProvider(SortedList<TypeDefinition> sortedList, TypeDefinitionSourceFactory typeDefinitionSourceFactory) {
        this.sortedList = sortedList;
        this.typeDefinitionSourceFactory = typeDefinitionSourceFactory;
    }


    /**
     * 第一个元素
     *
     * @return {@link T}
     */
    public T get() {
        return sortedList.isEmpty() ? null : sortedList.first().newInstance(typeDefinitionSourceFactory);
    }
}
