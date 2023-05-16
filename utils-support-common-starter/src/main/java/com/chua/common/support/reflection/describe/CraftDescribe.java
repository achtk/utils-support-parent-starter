package com.chua.common.support.reflection.describe;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 合成描述
 *
 * @author CH
 */
@Data
@Builder
@Accessors(fluent = true)
public class CraftDescribe {
    /**
     * 实体
     */
    private Object obj;
    /**
     * 类型
     */
    @Singular("parameter")
    private List<Object> parameters;

    /**
     * 设置值
     *
     * @param index 索引
     * @param value 值
     * @return 结果
     */
    public synchronized CraftDescribe set(int index, Object value) {
        parameters.remove(index);
        parameters.set(index, value);
        return this;
    }
}
