package com.chua.common.support.task.arrange.async.callback;


import com.chua.common.support.task.arrange.async.worker.WorkResult;

/**
 * 默认回调类，如果不设置的话，会默认给这个回调
 *
 * @author wuweifeng wrote on 2019-11-19.
 */
public class DefaultCallback<T, V> implements Callback<T, V> {
    @Override
    public void begin() {

    }

    @Override
    public void result(boolean success, T param, WorkResult<V> workResult) {

    }

}
