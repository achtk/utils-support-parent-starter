package com.chua.common.support.file.pandoc;

/**
 * 执行器
 *
 * @author CH
 */
public interface Executor {
    /**
     * 执行
     *
     * @param inputFile  输入文件
     * @param outputFile 输出文件
     */
    void execute(String inputFile, String outputFile);
}
