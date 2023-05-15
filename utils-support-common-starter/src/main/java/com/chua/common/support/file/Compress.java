package com.chua.common.support.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 压缩
 *
 * @author CH
 */
public interface Compress {

    /**
     * 添加文件
     *
     * @param file 文件
     * @return this
     */
    Compress addFile(File file);

    /**
     * 添加文件
     *
     * @param file 文件
     * @return this
     */
    default Compress addFile(String file) {
        return addFile(new File(file));
    }

    /**
     * 添加文件
     *
     * @param prefix 前缀
     * @param file   文件
     * @return this
     */
    Compress addFile(String prefix, File file);

    /**
     * 添加文件
     *
     * @param prefix 前缀
     * @param file   文件
     * @return this
     */
    default Compress addFile(String prefix, String file) {
        return addFile(prefix, new File(file));
    }

    /**
     * 添加流
     *
     * @param name   名称
     * @param stream 流
     * @return this
     */
    Compress addFile(String name, InputStream stream);

    /**
     * 添加流
     *
     * @param name  名称
     * @param bytes 流
     * @return this
     */
    Compress addFile(String name, byte[] bytes);

    /**
     * 输出
     *
     * @param outputStream 输出
     */
    void to(OutputStream outputStream);

    /**
     * 输出
     *
     * @param output 输出
     */
    default void to(Path output) {
        try (OutputStream outputStream = Files.newOutputStream(output)) {
            to(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 输出
     *
     * @param output 输出
     */
    default void to(String output) {
        to(new File(output));
    }

    /**
     * 输出
     *
     * @param output 输出
     */
    default void to(File output) {
        to(output.toPath());
    }
}
