package com.chua.common.support.database.structure;

import lombok.Builder;
import lombok.Data;

/**
 * 结构
 *
 * @author CH
 */
@Data
@Builder
public class StructureValue {
    /**
     * 名称
     */
    private String source;
    /**
     * 名称
     */
    private String target;
    /**
     * 作用域
     */
    private String schema;
    /**
     * 时间
     */
    private long timestamp;
}
