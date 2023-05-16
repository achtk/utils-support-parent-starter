package com.chua.common.support.context.factory;

import com.chua.common.support.context.aggregate.AggregateContext;
import com.chua.common.support.context.bean.BeanObject;
import com.chua.common.support.context.definition.TypeDefinition;

import java.util.Map;

/**
 * bean工厂
 * @author CH
 */
public interface BeanFactory {
    /**
     * 获取对象
     *
     * @param name   名称
     * @param target 目标类型
     * @param <T>    类型
     * @return 对象
     */
    <T> T getBean(String name, Class<T> target);


    /**
     * 获取对象
     *
     * @param beanName 目标类型
     * @return 对象
     */
    BeanObject getBean(String beanName);

    /**
     * 获取对象
     *
     * @param target 目标类型
     * @param <T>    类型
     * @return 对象
     */
    <T> T getBean(Class<T> target);

    /**
     * 获取对象
     *
     * @param target 目标类型
     * @param <T>    类型
     * @return 对象
     */
    <T> Map<String, T> getBeanMap(Class<T> target);

    /**
     * 装配
     *
     * @param bean bean
     */
    void autowire(Object bean);

    /**
     * 获取方法类型对应的bean
     *
     * @param type 类型
     * @return rs
     */
    Map<String, TypeDefinition<Object>> getBeanByMethod(Class<?>... type);

    /**
     * 聚合器
     * @return 聚合器
     */
    AggregateContext createAggregate();
}
