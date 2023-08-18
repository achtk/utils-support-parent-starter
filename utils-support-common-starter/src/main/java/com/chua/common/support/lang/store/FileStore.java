package com.chua.common.support.lang.store;

import java.util.List;
import java.util.Map;

/**
 * 文件存储
 *
 * @author CH
 */
public interface FileStore extends AutoCloseable {
    /**
     * 保存信息
     * @param applicationName 应用
     * @param message 消息
     * @param parent  文件夹
     */
    void write(String applicationName, String message, String parent);

    /**
     * 查询数据
     * @param keyword 关键词
     * @return 结果
     */
    List<Map<String, Object>> search(String keyword);
}
