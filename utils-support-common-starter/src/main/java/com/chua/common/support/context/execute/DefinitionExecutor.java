package com.chua.common.support.context.execute;

import com.chua.common.support.context.bean.BeanObjectValue;

/**
 * 执行器
 * @author CH
 */
public interface DefinitionExecutor {

    /**
     * 执行
     * @return 执行
     */
    BeanObjectValue execute();
}
