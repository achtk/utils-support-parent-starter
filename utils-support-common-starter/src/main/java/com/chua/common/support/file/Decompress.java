package com.chua.common.support.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 解压
 *
 * @author CH
 */
public interface Decompress {
    /**
     * 解压
     *
     * @param stream 压缩包
     * @param output 文件夹
     * @throws IOException ex
     */
    void unFile(InputStream stream, File output) throws IOException;

    /**
     * 解压
     *
     * @param stream     压缩包
     * @param consumer   名称
     * @param needStream 是否需要流
     * @throws IOException ex
     */
    void unFile(InputStream stream, Function<FileMedia, Boolean> consumer, boolean needStream) throws IOException;

    /**
     * 解压
     *
     * @param stream   压缩包
     * @param consumer 名称
     * @throws IOException ex
     */
    default void unFile(InputStream stream, Function<FileMedia, Boolean> consumer) throws IOException {
        unFile(stream, consumer, false);
    }

    /**
     * 解压
     *
     * @param stream 压缩包
     * @param output 文件夹
     * @throws IOException ex
     */
    default void unFile(InputStream stream, String output) throws IOException {
        unFile(stream, new File(output));
    }

    /**
     * 解压
     *
     * @param file   压缩包
     * @param output 文件夹
     * @throws IOException ex
     */
    default void unFile(File file, File output) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            unFile(fileInputStream, output);
        }
    }

    /**
     * 解压
     *
     * @param file   压缩包
     * @param output 文件夹
     * @throws IOException ex
     */
    default void unFile(String file, String output) throws IOException {
        unFile(new File(file), new File(output));
    }


}
