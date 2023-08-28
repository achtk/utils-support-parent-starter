package com.chua.common.support.lang.loader;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 原子加载器
 *
 * @author CH
 */
public abstract class BaseAtomicLoader<T> implements InitLoader<T>, Loader<T> {


    /**
     * 被加载对象的引用
     */
    private final AtomicReference<T> reference = new AtomicReference<>();

    /**
     * 获取一个对象，第一次调用此方法时初始化对象然后返回，之后调用此方法直接返回原对象
     */
    @Override
    public T get() {
        T result = reference.get();
        if (result == null) {
            result = init();
            if (!reference.compareAndSet(null, result)) {
                // 其它线程已经创建好此对象
                result = reference.get();
            }
        }

        return result;
    }
}
