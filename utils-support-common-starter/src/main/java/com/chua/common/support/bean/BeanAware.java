package com.chua.common.support.bean;

import com.chua.common.support.function.InitializingAware;

/**
 * @author CH
 */
public interface BeanAware extends InitializingAware {
    /**
     * 获取参数
     *
     * @param name 名称
     * @return 值
     */
    String getProperty(String name);

}
