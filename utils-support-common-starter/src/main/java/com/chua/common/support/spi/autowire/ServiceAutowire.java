package com.chua.common.support.spi.autowire;

/**
 * 服务属性注入器
 * @author CH
 */
public interface ServiceAutowire {

    /**
     * 装配
     *
     * @param object 对象
     * @return 对象
     */
    Object autowire(Object object);

    /**
     * 创建对象
     * @param implClass 实现
     * @return 对象
     */
    Object createBean(Class<?> implClass);
}
