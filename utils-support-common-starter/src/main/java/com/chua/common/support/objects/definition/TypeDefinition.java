package com.chua.common.support.objects.definition;

/**
 * 定义
 * @author CH
 */
public interface TypeDefinition {
    /**
     * 获取类型
     * @return {@link Class}<{@link ?}>
     */
    Class<?> getType();

    /**
     * 獲取對象
     *
     * @return 对象
     */
    Object getObject();


    /**
     * 是否单例
     * @return boolean
     */
    boolean isSingle();

    /**
     * 是否代理
     * @return boolean
     */
    boolean isProxy();

    /**
     * 优先级
     *
     * @return boolean
     */
    int order();

    /**
     * 目标类是否是当前类的子类
     * @param target 目标类
     * @return 目标类是否是当前类的子类
     */
    boolean isAssignableFrom(Class<?> target);

    /**
     * 类加载器
     *
     * @return 类加载器
     */
    ClassLoader getClassLoader();


    /**
     * 名称
     *
     * @return {@link String}
     */
    String getName();
}
