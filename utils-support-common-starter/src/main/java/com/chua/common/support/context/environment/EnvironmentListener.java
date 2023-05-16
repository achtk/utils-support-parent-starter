package com.chua.common.support.context.environment;

import com.chua.common.support.constant.Action;

/**
 * 监听
 *
 * @author CH
 */
public interface EnvironmentListener {

    /**
     * 监听
     *
     * @param value  数据
     * @param action 动作
     */
    void doListener(String value, Action action);

    /**
     * 表达式
     *
     * @return 表达式
     */
    String getExpression();

}
