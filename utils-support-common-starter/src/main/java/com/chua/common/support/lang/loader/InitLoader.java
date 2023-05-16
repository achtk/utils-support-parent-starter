package com.chua.common.support.lang.loader;

/**
 * 加载器
 *
 * @author CH
 */
public interface InitLoader<T> {
    /**
     * 初始化被加载的对象<br>
     * 如果对象从未被加载过，调用此方法初始化加载对象，此方法只被调用一次
     *
     * @return 被加载的对象
     */
    T init();
}
