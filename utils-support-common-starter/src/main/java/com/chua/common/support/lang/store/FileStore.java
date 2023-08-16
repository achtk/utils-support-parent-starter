package com.chua.common.support.lang.store;

/**
 * 文件存储
 *
 * @author CH
 */
public interface FileStore extends AutoCloseable {
    /**
     * 保存信息
     *
     * @param message 消息
     * @param parent  文件夹
     */
    void write(String message, String... parent);
}
