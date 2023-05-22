package com.chua.common.support.reflection.describe.provider;

import com.chua.common.support.collection.CollectionProvider;
import com.chua.common.support.lang.loader.Loadable;
import com.chua.common.support.reflection.describe.FieldDescribe;
import com.chua.common.support.utils.CollectionUtils;

import java.util.Collection;
import java.util.LinkedList;


/**
 * 提供者
 *
 * @author CH
 */
public class FieldDescribeProvider extends LinkedList<FieldDescribe>
        implements CollectionProvider<FieldDescribe, FieldDescribeProvider>,
        DescribeProvider,
        Loadable<FieldDescribeProvider> {

    @Override
    public <T> T execute(Object entity, Class<T> target, Object... args) {
        FieldDescribe first = CollectionUtils.findFirst(this);
        if(null == first) {
            return null;
        }

        return first.get(entity).getValue(target);
    }

    @Override
    public <T> T executeSelf(Class<T> target, Object... args) {
        FieldDescribe first = CollectionUtils.findFirst(this);
        if (null == first) {
            return null;
        }

        return first.get(first.entity()).getValue(target);
    }

    @Override
    public FieldDescribeProvider addChain(FieldDescribe fieldDescribe) {
        add(fieldDescribe);
        return this;
    }

    @Override
    public FieldDescribeProvider addChains(Collection<FieldDescribe> v) {
        addAll(v);
        return this;
    }

    @Override
    public FieldDescribeProvider get() {
        return this;
    }
}
