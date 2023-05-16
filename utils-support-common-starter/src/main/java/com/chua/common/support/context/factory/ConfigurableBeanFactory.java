package com.chua.common.support.context.factory;

import com.chua.common.support.context.definition.ClassDefinition;
import com.chua.common.support.context.definition.ObjectDefinition;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.enums.DefinitionType;
import com.chua.common.support.context.environment.Environment;

import java.util.List;

/**
 * 可配置对象工厂
 *
 * @author CH
 */
public interface ConfigurableBeanFactory extends BeanFactory {

    /**
     * 获取对象
     *
     * @param definition 定义
     */
    void registerBean(TypeDefinition definition);

    /**
     * 获取对象
     *
     * @param bean 定义
     */
    @SuppressWarnings("ALL")
    default void registerBean(Object bean) {
        if (bean instanceof Class) {
            registerBean(new ClassDefinition((Class) bean));
            return;
        }

        registerBean(new ObjectDefinition(bean));
    }

    /**
     * 获取对象
     *
     * @param name   名称
     * @param target 目标类型
     * @return 对象
     */
    <T> TypeDefinition<T> getDefinition(String name, Class<T> target);


    /**
     * 删除对象
     *
     * @param name           名称
     * @param definitionType 类型
     */
    void removeBean(String name, DefinitionType definitionType);

    /**
     * 删除对象
     *
     * @param name           名称
     */
   default void removeBean(String name) {
       removeBean(name, DefinitionType.NONE);
   }

    /**
     * 获取任意所有的子类
     *
     * @param targetType 类型
     * @return 子类
     */
    <T> List<T> getAnyBean(Class<T> targetType);

    /**
     * 获取环境
     *
     * @return 环境
     */
    Environment getEnvironment();

    /**
     * 刷新
     */
    void refresh();
}
