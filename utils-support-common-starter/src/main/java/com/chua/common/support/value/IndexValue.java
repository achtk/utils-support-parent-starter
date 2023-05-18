package com.chua.common.support.value;


import java.io.Serializable;
import java.util.List;

/**
 * 值
 *
 * @author CH
 * @since 2022/8/9 21:51
 */
public interface IndexValue extends Value<KeyValue>, Serializable, List<KeyValue> {

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 结果
     */
    KeyValue get(int index);
    /**
     * 获取数据
     *
     * @return 结果
     */
    List<KeyValue> getAll();
}
