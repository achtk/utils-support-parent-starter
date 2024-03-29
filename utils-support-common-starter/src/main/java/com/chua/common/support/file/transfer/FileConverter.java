package com.chua.common.support.file.transfer;


import com.chua.common.support.function.OrderAware;
import com.chua.common.support.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * 类型转化
 *
 * @author CH
 */
public interface FileConverter extends OrderAware {
    /**
     * 优先级
     *
     * @return 优先级
     */
    @Override
    default int order() {
        return 0;
    }

    /**
     * 目标
     *
     * @return 目标
     */
    String target();

    /**
     * 来源
     *
     * @return 来源
     */
    String source();

    /**
     * 转化
     *
     * @param type         输入类型
     * @param inputStream  输入
     * @param outputStream 输出
     * @param suffix       后缀
     * @throws Exception ex
     */
    void convert(String type, InputStream inputStream, String suffix, OutputStream outputStream) throws Exception;

    /**
     * 转化
     *
     * @param file         输入
     * @param suffix       后缀
     * @param outputStream 输出
     * @throws Exception ex
     */
    void convert(File file, String suffix, OutputStream outputStream) throws Exception;

}
