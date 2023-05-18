package com.chua.common.support.database.action;

/**
 * 动作执行器
 *
 * @author CH
 */
public interface ActionExecutor<I, O> {

    /**
     * 执行
     *
     * @param input 输入
     * @return 输出
     */
    O doExecute(I input);
}
