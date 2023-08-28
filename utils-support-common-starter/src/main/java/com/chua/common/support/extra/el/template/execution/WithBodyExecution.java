package com.chua.common.support.extra.el.template.execution;

/**
 * 基础类
 *
 * @author CH
 */
public interface WithBodyExecution extends Execution {
    void setBody(Execution... executions);

    boolean isBodyNotSet();
}
