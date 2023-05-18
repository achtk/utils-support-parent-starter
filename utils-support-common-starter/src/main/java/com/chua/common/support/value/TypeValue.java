package com.chua.common.support.value;


import java.io.Serializable;

/**
 * 值
 *
 * @author CH
 * @since 2022/8/9 21:51
 */
public interface TypeValue extends Value<Object>, Serializable {

    /**
     * 获取数据
     *
     * @param type 类型
     * @param <T>  类型
     * @return 结果
     */
    <T> T get(Class<T> type);
}
