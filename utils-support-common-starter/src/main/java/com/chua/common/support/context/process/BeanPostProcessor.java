package com.chua.common.support.context.process;


import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.enums.DefinitionType;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;

import java.util.List;

/**
 * 对象加载器
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public interface BeanPostProcessor {
    /**
     * 注入
     *
     * @param definition 对象
     */
    void processInjection(TypeDefinition definition);

    /**
     * 获取定义
     *
     * @param bean       名称
     * @param targetType 类型
     * @return 定义
     */
    <T> List<TypeDefinition<T>> postProcessInstantiation(String bean, Class<T> targetType);

    /**
     * 获取定义
     *
     * @param bean       名称
     * @param targetType 类型
     * @return 定义
     */
    <T> List<TypeDefinition<T>> postProcessInstantiation(Class<T> targetType);

    /**
     * 校验定义
     *
     * @param definition 定义
     * @return 校验定义
     */
    boolean isValid(TypeDefinition definition);

    /**
     * 卸载
     *
     * @param name           名称
     * @param definitionType 定义类型
     */
    void unProcessInjection(String name, DefinitionType definitionType);

    /**
     * 刷新
     *
     * @param standardConfigurableBeanFactory 上下文
     */
    void refresh(ConfigurableBeanFactory standardConfigurableBeanFactory);

    /**
     * 获取方法类型对应的bean
     *
     * @param type 类型
     * @return rs
     */
    List<TypeDefinition<Object>> postBeanByMethod(Class<?>[] type);
}
