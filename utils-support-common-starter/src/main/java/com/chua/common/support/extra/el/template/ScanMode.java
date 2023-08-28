package com.chua.common.support.extra.el.template;

/**
 * 基础类
 *
 * @author CH
 */
public enum ScanMode {
    /**
     * 字符串
     */
    LITERALS, 
    /**
     * 执行语句.说明当前处于<%%>包围之中
     */
    EXECUTION, 
}
