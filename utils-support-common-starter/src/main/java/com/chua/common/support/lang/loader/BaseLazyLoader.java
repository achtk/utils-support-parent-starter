package com.chua.common.support.lang.loader;

/**
 * 懒加载加载器<br>
 *
 * @author CH
 */
public abstract class BaseLazyLoader<T> implements InitLoader<T>, Loader<T> {

    /**
     * 被加载对象
     */
    private volatile T object;

    /**
     * 获取一个对象，第一次调用此方法时初始化对象然后返回，之后调用此方法直接返回原对象
     */
    @Override
    public T get() {
        T result = object;
        if (result == null) {
            synchronized (this) {
                result = object;
                if (result == null) {
                    object = result = init();
                }
            }
        }
        return result;
    }
}
