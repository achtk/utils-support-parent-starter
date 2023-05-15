package com.chua.common.support.lang;


import com.chua.common.support.converter.Converter;
import com.chua.common.support.utils.NumberUtils;

/**
 * 片段表示，用于表示文本、集合等数据结构的一个区间。
 * @param <T> 数字类型，用于表示位置index
 *
 * @author looly
 * @since 5.5.3
 */
public interface Segment<T extends Number> {

    /**
     * 获取起始位置
     *
     * @return 起始位置
     */
    T getStartIndex();

    /**
     * 获取结束位置
     *
     * @return 结束位置
     */
    T getEndIndex();

    /**
     * 片段长度，默认计算方法为abs({@link #getEndIndex()} - {@link #getEndIndex()})
     *
     * @return 片段长度
     */
    default T length(){
        final T start = getStartIndex();
        final T end = getEndIndex();
        return (T) Converter.convertIfNecessary(NumberUtils.sub(end, start).abs(), start.getClass());
    }
}
