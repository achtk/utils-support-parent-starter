package com.chua.common.support.context.definition;


import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.collection.Table;
import com.chua.common.support.context.aware.DestructionAwareBeanPostProcessor;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.context.resolver.factory.InitializingResolverFactory;
import com.chua.common.support.utils.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.chua.common.support.context.constant.ContextConstant.COMPARATOR;

/**
 * 定义处理
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class DefinitionUtils {
    /**
     * 注册
     *
     * @param definition 定义
     * @param table      缓存对象
     */
    public static void register(TypeDefinition definition,
                                Map<String, SortedList<TypeDefinition>> table) {
        String[] name = definition.getBeanName();
        for (String s : name) {
            register(s, definition, table);
        }

        Class<?>[] types = definition.getTypes();
        for (Class<?> type : types) {
            register(type.getTypeName(), definition, table);
        }
    }


    /**
     * 注册定义
     *
     * @param name       名称
     * @param definition 定义
     * @param table      缓存对象
     */
    private static void register(String name, TypeDefinition definition, Map<String, SortedList<TypeDefinition>> table) {
        table.computeIfAbsent(name, it -> new SortedArrayList<>(COMPARATOR)).add(definition);
    }

    /**
     * 注册定义
     *
     * @param name       名称
     * @param type       类型
     * @param definition 定义
     * @param table      缓存对象
     */
    private static synchronized void register(String name, Class<?> type, TypeDefinition definition, Table<String, String, TypeDefinition> table) {
        table.put(name, type.getTypeName(), definition);
    }

    /**
     * 刷新bean
     *
     * @param value       值
     * @param beanFactory 上下文
     */

    public static void refresh(SortedList<TypeDefinition> value, ConfigurableBeanFactory beanFactory) {
        if (null == beanFactory || CollectionUtils.isEmpty(value)) {
            return;
        }

        InitializingResolverFactory initializingResolverFactory = new InitializingResolverFactory();
//        List<DestructionAwareBeanPostProcessor> anyBean = beanFactory.getAnyBean(DestructionAwareBeanPostProcessor.class);
        for (TypeDefinition definition : value) {
            Object object = definition.getObject(beanFactory);
            if (null == object) {
                return;
            }

            initializingResolverFactory.refresh(object);
//            refreshBeanPostProcessor(anyBean, definition, beanFactory);
        }
    }

    /**
     * 刷新加载器
     *
     * @param anyBean     加载器
     * @param definition  定义
     * @param beanFactory 上下文
     */
    private static void refreshBeanPostProcessor(List<DestructionAwareBeanPostProcessor> anyBean, TypeDefinition definition, ConfigurableBeanFactory beanFactory) {
        for (DestructionAwareBeanPostProcessor destructionAwareBeanPostProcessor : anyBean) {
            destructionAwareBeanPostProcessor.postProcessBeforeDestruction(definition, beanFactory);
        }
    }
}
