package com.chua.common.support.monitor;

/**
 * 通知类型
 *
 * @author CH
 */
public enum NotifyType {
    /**
     * 创建
     */
    CREATE,
    /**
     * 删除
     */
    DELETE,
    /**
     * 修改
     */
    MODIFY,
    /**
     * 异常
     */
    EXCEPTION,

    /**
     * query
     */
    QUERY,
    /**
     * Table
     */
    TABLE,
    /**
     * FormatDescription
     */
    FORMAT_DESCRIPTION,
    /**
     * Rotate
     */
    ROTATE,
    /**
     * other
     */
    OTHER;
}
