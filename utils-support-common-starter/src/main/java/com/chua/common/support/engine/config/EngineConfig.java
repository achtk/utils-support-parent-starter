package com.chua.common.support.engine.config;

import lombok.Data;

/**
 * 基础配置
 *
 * @author CH
 */
@Data
public class EngineConfig {

    /**
     * 初始化时是否清除之前数据
     */
    private boolean cleanWhenInitial;
    /**
     * 分片
     */
    private int fragmentation = 1;
}
