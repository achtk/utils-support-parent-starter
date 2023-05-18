package com.chua.common.support.database.entity;

import lombok.Data;

/**
 * 主键
 *
 * @author CH
 */
@Data
public class Primary {

    /**
     * 主键策略
     */
    private String strategy = "increment";
}
