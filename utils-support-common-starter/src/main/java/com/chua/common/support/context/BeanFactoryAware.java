package com.chua.common.support.context;

import com.chua.common.support.context.factory.BeanFactory;

/**
 * aware
 *
 * @author CH
 */
public interface BeanFactoryAware {

    /**
     * 初始化
     *
     * @param beanFactory 工厂
     */
    void setBeanFactory(BeanFactory beanFactory);
}
