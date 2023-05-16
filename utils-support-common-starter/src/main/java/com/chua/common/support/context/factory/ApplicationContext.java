package com.chua.common.support.context.factory;

/**
 * 上下文
 *
 * @author CH
 */
public interface ApplicationContext extends ConfigurableBeanFactory {


    /**
     * 获取对象管理器
     *
     * @return 对象管理器
     */
    BeanFactory getBeanFactory();


}
