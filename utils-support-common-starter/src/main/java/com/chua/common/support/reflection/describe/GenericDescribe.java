package com.chua.common.support.reflection.describe;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.utils.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * Generic
 *
 * @author CH
 */
public class GenericDescribe implements InitializingAware {
    private final Class<?> beanClass;
    private final List<GenericTypeAttribute> attributes = new LinkedList<>();

    public GenericDescribe(Class<?> beanClass) {
        this.beanClass = beanClass;
        afterPropertiesSet();
    }

    @Override
    public void afterPropertiesSet() {
        Class<?> type = beanClass;
        while (!(ClassUtils.isObject(type))) {
            Type genericSuperclass = type.getGenericSuperclass();
            refreshType(type, genericSuperclass, 0);
            type = type.getSuperclass();
        }
    }

    private void refreshType(Class<?> type, Type genericSuperclass, int deep) {
        if (genericSuperclass instanceof ParameterizedType) {
            refresh(type, (ParameterizedType) genericSuperclass, deep);
            return;
        }
        if (genericSuperclass instanceof Class) {
            attributes.add(new GenericTypeAttribute(type, genericSuperclass.getTypeName(), deep));
        }
    }

    private void refresh(Class<?> type, ParameterizedType parameterizedType, int deep) {
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        for (Type actualTypeArgument : actualTypeArguments) {
            refreshType(type, actualTypeArgument, deep + 1);
        }
    }

    public GenericTypeAttribute get(int i) {
        return attributes.get(i);
    }

    /**
     * 查询类对应的属性
     *
     * @param name 类
     * @return 泛型
     */
    public GenericTypeAttribute getType(String name) {
        for (GenericTypeAttribute attribute : attributes) {
            if (attribute.isEquals(name)) {
                return attribute;
            }
        }

        return null;
    }

    /**
     * 查询类对应的属性
     *
     * @param name 类
     * @return 泛型
     */
    public GenericTypeAttribute getType(Class<?> name) {
        return null == name ? null : getType(name.getTypeName());
    }
}
