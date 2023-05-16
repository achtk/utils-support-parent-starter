package com.chua.common.support.context.factory;

import com.chua.common.support.context.aggregate.AggregateContext;

/**
 * 可配置的上下文
 *
 * @author CH
 */
public interface ConfigureApplicationContext extends ApplicationContext, AggregateContext {

    /**
     * 获取配置
     *
     * @return 配置
     */
    ApplicationContextConfiguration getApplicationContextConfiguration();

    /**
     * 刷新Bean
     */
    void refresh();

}
