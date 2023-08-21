package com.chua.common.support.lang.spider.pipeline;

import com.chua.common.support.lang.spider.Task;

/**
 * Implements PageModelPipeline to persistent your page model.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public interface PageModelPipeline<T> {

    void process(T t, Task task);

}
