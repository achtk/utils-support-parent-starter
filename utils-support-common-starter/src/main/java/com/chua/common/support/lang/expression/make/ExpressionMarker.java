package com.chua.common.support.lang.expression.make;

import com.chua.common.support.lang.expression.listener.Listener;

/**
 * 对象生成器
 * @author CH
 */
public interface ExpressionMarker {

    /**
     * 创建对象
     *
     * @param listener    监听器
     * @param classLoader 加载器
     * @param args
     * @return 对象
     */
    Object createObject(Listener listener, ClassLoader classLoader, Object[] args);

    /**
     * 类型
     * @return 类型
     */
    Class<?> getType();
}
