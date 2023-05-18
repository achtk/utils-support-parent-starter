package com.chua.common.support.mapping.value;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.utils.ArrayUtils;

/**
 * value
 *
 * @author CH
 */
public class MappingValue {
    private final int length;
    private Object[] args;

    public MappingValue(Object[] args) {
        this.args = args;
        this.length = args.length;
    }

    /**
     * 获取值
     *
     * @param index  索引
     * @param target 类型
     * @return 值
     */
    public <T> T get(int index, Class<T> target) {
        return Converter.convertIfNecessary(get(index), target);
    }

    /**
     * 获取值
     *
     * @param index 索引
     * @return 值
     */
    public Object get(int index) {
        return ArrayUtils.getIndex(args, index % length);
    }
}
