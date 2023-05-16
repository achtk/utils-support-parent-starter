package com.chua.common.support.context.outside;

import com.chua.common.support.context.factory.ConfigureApplicationContext;

/**
 * 外部上下文
 *
 * @author CH
 */
public interface OutSideContext {

    /**
     * 基础配置
     *
     * @param contextConfiguration 基础配置
     * @return this
     */
    OutSideContext configuration(ConfigureApplicationContext contextConfiguration);

    /**
     * 刷新
     */
    void refresh();
}
