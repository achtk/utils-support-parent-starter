package com.chua.common.support.context.factory;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 基础配置
 * @author CH
 */
@Data
@Accessors(fluent = true)
public class GlobalConfiguration {
    /**
     * 是否默认开始代理
     */
    private boolean openProxy = true;


    /**
     * 是否开启扫描位置
     */
    private boolean openScanner = true;
}
