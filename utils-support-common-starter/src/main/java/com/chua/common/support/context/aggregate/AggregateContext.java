package com.chua.common.support.context.aggregate;

/**
 * 聚合
 *
 * @author CH
 */
public interface AggregateContext {
    /**
     * 挂载
     *
     * @param name      名称
     * @param aggregate 聚合
     */
    void mount(String name, Aggregate aggregate);
    /**
     * 取消挂载
     *
     * @param aggregate 聚合
     */
    void unmount(Aggregate aggregate);
    /**
     * 取消挂载
     *
     * @param name      名称
     */
    void unmount(String name);

    /**
     * 类
     * @param name 类名
     * @return  类
     */
    Class<?> forName(String name);
}
