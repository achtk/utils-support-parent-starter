package com.chua.common.support.lang.spide.pipeline;

import com.chua.common.support.lang.spide.Spider;
import com.chua.common.support.lang.spide.task.Task;

/**
 * Pipeline
 * @author CH
 */
public interface Pipeline<Spider> {


    /**
     * 额外结果
     *
     * @param resultItems resultItems
     * @param task task
     */
    void process(ResultItems resultItems, Task<Spider> task);
}
