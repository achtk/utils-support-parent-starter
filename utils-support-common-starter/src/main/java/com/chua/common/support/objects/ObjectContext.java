package com.chua.common.support.objects;

import com.chua.common.support.collection.SortedList;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.definition.ZipTypeDefinition;
import com.chua.common.support.objects.provider.ObjectProvider;

import java.io.File;

/**
 * 对象管理器
 *
 * @author CH
 */
public interface ObjectContext {

    /**
     * 获取bean
     *
     * @param name       名称
     * @param targetType 目标类型
     * @return {@link T}
     */
    <T> T getBean(String name, Class<T> targetType);

    /**
     * 获取bean
     *
     * @param name 名称
     * @return {@link Object}
     */
    Object getBean(String name);

    /**
     * 获取bean定义
     *
     * @param name 名称
     * @return {@link SortedList}<{@link TypeDefinition}>
     */
    SortedList<TypeDefinition> getBeanDefinition(String name);

    /**
     * 获取bean
     *
     * @param targetType 目标类型
     * @return {@link T}
     */
    <T> ObjectProvider<T> getBean(Class<T> targetType);


    /**
     * 注销
     *
     * @param typeDefinition 定义
     */
    void unregister(TypeDefinition typeDefinition);

    /**
     * 注销
     *
     * @param type 类型
     * @param name 名称
     */
    void unregister(String name, Class<? extends TypeDefinition> type);

    /**
     * 注册
     *
     * @param definition 释义
     */
    void register(TypeDefinition definition);

    /**
     * 注册
     *
     * @param file 释义
     */
    default void register(File file) {
        register(new ZipTypeDefinition(file));
    }

}
