package com.chua.common.support.json.jsonpath;


/**
 * Returns a new representation for the input value.
 *
 * @author Administrator
 */
public interface MapFunction {
    /**
     * 執行
     * @param currentValue 值
     * @param configuration 配置
     * @return 结果
     */
    Object map(Object currentValue, JsonConfiguration configuration);
}
