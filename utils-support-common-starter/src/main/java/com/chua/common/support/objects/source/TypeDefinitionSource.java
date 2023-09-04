package com.chua.common.support.objects.source;

import com.chua.common.support.collection.SortedList;
import com.chua.common.support.objects.definition.TypeDefinition;

import java.lang.annotation.Annotation;

/**
 * 类型定义源
 *
 * @author CH
 * @since 2023/09/02
 */
public interface TypeDefinitionSource {


    /**
     * 匹配
     *
     * @param typeDefinition 类型定义
     * @return boolean
     */
    boolean isMatch(TypeDefinition typeDefinition);

    /**
     * 获取bean
     *
     * @param name       名称
     * @param targetType 目标类型
     * @return {@link SortedList}<{@link TypeDefinition}>
     */
    SortedList<TypeDefinition> getBean(String name, Class<?> targetType);

    /**
     * 获取bean
     *
     * @param name 名称
     * @return {@link SortedList}<{@link TypeDefinition}>
     */
    SortedList<TypeDefinition> getBean(String name);

    /**
     * 获取bean
     *
     * @param targetType 目标类型
     * @return {@link SortedList}<{@link TypeDefinition}>
     */
    SortedList<TypeDefinition> getBean(Class<?> targetType);


    /**
     * 注销
     *
     * @param typeDefinition 定义
     */
    void unregister(TypeDefinition typeDefinition);

    /**
     * 注销
     *
     * @param name 名称
     */
    void unregister(String name);

    /**
     * 注册
     *
     * @param definition 释义
     */
    void register(TypeDefinition definition);

    /**
     * 获取bean通过方法
     *
     * @param annotationType 注解类型
     * @return {@link SortedList}<{@link TypeDefinition}>
     */
    SortedList<TypeDefinition> getBeanByMethod(Class<? extends Annotation> annotationType);
}
