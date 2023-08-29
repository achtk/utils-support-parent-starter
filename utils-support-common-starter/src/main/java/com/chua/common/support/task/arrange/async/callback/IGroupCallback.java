package com.chua.common.support.task.arrange.async.callback;

import com.chua.common.support.task.arrange.Worker;

import java.util.List;

/**
 * 如果是异步执行整组的话，可以用这个组回调。不推荐使用
 *
 * @author wuweifeng wrote on 2019-11-19.
 */
public interface IGroupCallback {
    /**
     * 成功后，可以从wrapper里去getWorkResult
     * @param workers works
     */
    void success(List<Worker> workers);

    /**
     * 失败了，也可以从wrapper里去getWorkResult
     * @param workers works
     * @param e  e
     */
    void failure(List<Worker> workers, Exception e);
}
