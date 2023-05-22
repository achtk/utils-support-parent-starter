package com.chua.common.support.reflection.describe;

import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.reflection.describe.provider.FieldDescribeProvider;
import com.chua.common.support.reflection.describe.provider.MethodDescribeProvider;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * 类型属性
 *
 * @author CH
 */
public class TypeAttribute implements MemberDescribe<TypeAttribute> {
    /**
     * 类型描述
     */
    private final TypeDescribe typeDescribe;

    private static final Map<Class<?>, TypeAttribute> CACHE = new ConcurrentReferenceHashMap<>(256);


    private TypeAttribute(Class<?> beanClass, Object bean) {
        if (null == bean) {
            this.typeDescribe = TypeDescribe.create(beanClass);
        } else {
            this.typeDescribe = TypeDescribe.create(bean);
        }
    }

    /**
     * 初始化
     *
     * @param bean 类
     * @return 属性
     */
    public static TypeAttribute create(Object bean) {
        return CACHE.computeIfAbsent(null == bean ? void.class : bean.getClass(), it -> new TypeAttribute(null == bean ? void.class : bean.getClass(), bean));
    }

    /**
     * 初始化
     *
     * @param beanClass 类
     * @return 属性
     */
    public static TypeAttribute create(Class<?> beanClass) {
        return CACHE.computeIfAbsent(beanClass, it -> new TypeAttribute(beanClass, null));
    }

    @Override
    public FieldDescribe getFieldDescribe(String name) {
        return typeDescribe.getFieldDescribe(name);
    }

    @Override
    public FieldDescribeProvider getFieldDescribeProvider(String name) {
        return new FieldDescribeProvider().addChain(getFieldDescribe(name));
    }

    @Override
    public MethodDescribe getMethodDescribe(String name, String[] parameterTypes) {
        return typeDescribe.getMethodDescribe(name, parameterTypes);
    }

    @Override
    public MethodDescribeProvider getMethodDescribe(String name) {
        return typeDescribe.getMethodDescribe(name);
    }

    @Override
    public MethodDescribeProvider getMethodDescribeByAnnotation(String name) {
        return typeDescribe.getMethodDescribeByAnnotation(name);
    }

    @Override
    public List<FieldDescribe> getFieldDescribeByAnnotation(String name) {
        return typeDescribe.getFieldDescribeByAnnotation(name);
    }

    @Override
    public boolean hasAnyAnnotation(String[] name) {
        return typeDescribe.hasAnyAnnotation(name);
    }

    @Override
    public boolean hasAnnotation(String name) {
        return typeDescribe.hasAnnotation(name);
    }

    @Override
    public boolean hasMethodByParameterType(Class<?>[] type) {
        return typeDescribe.hasMethodByParameterType(type);
    }

    @Override
    public void addAnnotation(Annotation annotation) {
        typeDescribe.addAnnotation(annotation);
    }

    @Override
    public TypeAttribute doChainSelf(Object... args) {
        return this;
    }

    @Override
    public TypeAttribute doChain(Object... args) {
        return this;
    }

    @Override
    public TypeAttribute doChain(Object bean, Object... args) {
        return this;
    }

    @Override
    public TypeDescribe isChainSelf() {
        return typeDescribe.isChainSelf();
    }

    @Override
    public TypeDescribe isChain(Object... args) {
        return typeDescribe.isChain(args);
    }

    @Override
    public TypeDescribe isChain(Object bean, Object... args) {
        return typeDescribe.isChain(bean, args);
    }

    /**
     * 获取字段值
     *
     * @param name 名称
     */
    public TypeAttribute getFieldAttribute(String name) {
        return typeDescribe.getFieldAttribute(name);
    }

    /**
     * 获取值
     *
     * @param name 名称
     * @return 值
     */
    public Object getFieldValue(String name) {
        return getFieldValue(name, Object.class);
    }

    /**
     * 获取值
     *
     * @param name   名称
     * @param target 类型
     * @return 值
     */
    public <T> T getFieldValue(String name, Class<T> target) {
        return typeDescribe.getFieldValue(name, target);
    }
}
