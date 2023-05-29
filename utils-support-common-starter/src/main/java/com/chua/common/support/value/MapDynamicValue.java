package com.chua.common.support.value;

import java.util.Map;
import java.util.function.Function;

/**
 * 回调值
 * @param <T> 类型
 * @author CH
 */
public class MapDynamicValue<T> extends DynamicValue<Map<String, Object>, T>{

    public MapDynamicValue(Function<Map<String, Object>, T> function) {
        super(function);
    }
}
