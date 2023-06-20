package com.chua.common.support.modularity;

import lombok.Data;

/**
 * 模块
 *
 * @author CH
 */
@Data
public class Modularity {
    /**
     * 模块名称(唯一)
     */
    private String moduleName;
    /**
     * 处理内容
     */
    private String moduleScript;
    /**
     * 模块类型
     */
    private String moduleType;

}
