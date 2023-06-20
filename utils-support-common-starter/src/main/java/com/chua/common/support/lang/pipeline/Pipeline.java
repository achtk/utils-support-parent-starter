package com.chua.common.support.lang.pipeline;

/**
 * 管道模式
 *
 * @param <I>
 * @author CH
 */
public interface Pipeline<I> {
    /**
     * 处理
     *
     * @param i 输入
     * @return 输出
     */
    I process(I i);
}
