package com.chua.common.support.function;

/**
 * 链路
 *
 * @author CH
 */
public interface Link<T, O> {

    /**
     * next
     *
     * @param t t
     */
    void next(T t);

    /**
     * 成功
     *
     * @param t t
     * @return O
     */
    O resolve(T t);

    /**
     * 失败
     *
     * @param t t
     * @return O
     */
    O reject(T t);
}
