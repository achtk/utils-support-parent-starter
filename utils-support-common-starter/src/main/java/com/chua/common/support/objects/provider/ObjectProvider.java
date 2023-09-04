package com.chua.common.support.objects.provider;

import com.chua.common.support.collection.SortedList;
import com.chua.common.support.objects.definition.TypeDefinition;

/**
 * 对象提供程序
 *
 * @author CH
 * @since 2023/09/03
 */
public class ObjectProvider<T> {

    private final SortedList<TypeDefinition> sortedList;

    public ObjectProvider(SortedList<TypeDefinition> sortedList) {
        this.sortedList = sortedList;
    }
}
