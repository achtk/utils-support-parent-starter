package com.chua.common.support.file.export;

import java.io.OutputStream;
import java.util.List;

/**
 * 导出文件
 *
 * @author CH
 */
public interface ExportFile extends AutoCloseable {


    /**
     * 导出文件
     *
     * @param outputStream 流
     * @param data         数据
     */
    <T> void export(OutputStream outputStream, List<T> data);


    /**
     * 导出文件
     *
     * @param outputStream 流
     * @param data         数据
     */
    <T> void export(OutputStream outputStream, T data);

    /**
     * 追加
     *
     * @param records 记录
     * @param <T>     类型
     */
    <T> void append(List<T> records);
}
