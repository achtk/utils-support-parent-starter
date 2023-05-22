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
        doRefresh(beanClass, 0);
    }

    private void doRefresh(Class<?> type, int level) {
        refreshType(type, type.getGenericSuperclass(), level);
        Class<?> superclass = type.getSuperclass();
        if (!ClassUtils.isObject(superclass)) {
            refreshType(superclass, superclass.getGenericSuperclass(), level);
            if (!ClassUtils.isObject(superclass)) {
                doRefresh(superclass, level + 1);
            }
        }

        Class<?>[] interfaces = type.getInterfaces();
        for (Class<?> aClass : interfaces) {
            refreshType(aClass, aClass.getGenericSuperclass(), level);
            if (!ClassUtils.isObject(aClass)) {
                doRefresh(aClass, level + 1);
            }
        }
    }

    private void refreshType(Class<?> type, Type genericSuperclass, int deep) {
        if (genericSuperclass instanceof ParameterizedType) {
            refresh(type, (ParameterizedType) genericSuperclass, deep);
            return;
        }
    }

    private void refresh(Class<?> type, ParameterizedType parameterizedType, int deep) {
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        for (Type actualTypeArgument : actualTypeArguments) {
            refreshClass(type, actualTypeArgument, deep + 1);
        }
    }

    private void refreshClass(Class<?> type, Type genericSuperclass, int deep) {
        if (genericSuperclass instanceof Class) {
            attributes.add(new GenericTypeAttribute(type, genericSuperclass.getTypeName(), deep));
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
