package com.chua.common.support.file.export;

import java.io.OutputStream;
import java.util.List;

/**
 * 导出文件
 *
 * @author CH
 */
public interface ExportFile {


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
}
