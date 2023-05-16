package com.chua.common.support.file.tailer;


/**
 * commons-io
 *
 * @author CH
 */
public interface TailerListener {

    /**
     * 初始化 tailer
     *
     * @param tailer tailer
     */
    void init(Tailer tailer);

    /**
     * 文件不存在
     */
    void fileNotFound();

    /**
     * 处理
     *
     * @param line 行
     */
    void handle(String line);

    /**
     * 处理异常
     *
     * @param ex the exception.
     */
    void handle(Exception ex);
}
