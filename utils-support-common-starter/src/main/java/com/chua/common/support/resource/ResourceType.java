package com.chua.common.support.resource;

/**
 * 类型
 *
 * @author CH
 */
public enum ResourceType {
    /**
     * 系统文件
     */
    FILESYSTEM,
    /**
     * 子类
     */
    SUBTYPE,
    /**
     * 任意系统文件
     */
    FILESYSTEM_ANY,
    /**
     * 加载器文件
     */
    CLASSPATH,
    /**
     * 任意加载器文件
     */
    CLASSPATH_ANY
}
