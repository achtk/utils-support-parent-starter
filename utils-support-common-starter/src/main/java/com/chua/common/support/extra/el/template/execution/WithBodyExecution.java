package com.chua.common.support.extra.el.template.execution;

public interface WithBodyExecution extends Execution
{
    void setBody(Execution... executions);

    boolean isBodyNotSet();
}
