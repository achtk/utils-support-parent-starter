package com.chua.common.support.extra.el.template.execution;

/**
 * 基础类
 *
 * @author CH
 */
public interface WithBodyExecution extends Execution {
    /**
     * 设置
     *
     * @param executions 执行
     */
    void setBody(Execution... executions);

    /**
     * 獲取body
     *
     * @return boolean
     */
    boolean isBodyNotSet();
}
