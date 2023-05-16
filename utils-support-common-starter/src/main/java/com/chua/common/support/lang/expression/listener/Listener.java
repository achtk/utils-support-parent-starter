package com.chua.common.support.lang.expression.listener;

/**
 * 监听
 * @author CH
 */
public interface Listener {

    /**
     * 是否改变
     *
     * @return 是否改变
     */
    boolean isChange();

    /**
     * 源码
     *
     * @return 源码
     */
    String getSource();

}
