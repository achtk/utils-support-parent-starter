package com.chua.common.support.file;


import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * 资源文件
 *
 * @author CH
 */
public interface ResourceFile {


    /**
     * 创建文件
     *
     * @param file 文件
     * @return 压缩文件
     */
    static ResourceFile of(File file) {
        return ServiceProvider.of(ResourceFile.class)
                .getNewExtension(FileUtils.getExtension(file), file);
    }


    /**
     * md5
     *
     * @return md5
     */
    String md5();

    /**
     * base64
     *
     * @return base64
     */
    String toBase64();

    /**
     * 大小
     *
     * @return 大小
     */
    long size();

    /**
     * 大小
     *
     * @return 大小
     */
    long lastModified();

    /**
     * 获取文件
     *
     * @return 文件
     */
    File toFile();

    /**
     * 流
     *
     * @return 流
     * @throws IOException ex
     */
    InputStream openInputStream() throws IOException;

    /**
     * 文件主体类型
     *
     * @return 文件主体类型
     */
    String formatName();

    /**
     * 格式名称
     *
     * @return 格式名称
     */
    String subtype();

    /**
     * 类型
     *
     * @return 类型
     */
    String getContentType();

    /**
     * 转化
     *
     * @param suffix       后缀
     * @param outputStream 输出
     * @throws IOException ex
     */
    void transfer(String suffix, OutputStream outputStream) throws IOException;

    /**
     * 转化
     *
     * @param suffix  后缀
     * @param outFile 输出
     * @throws IOException ex
     */
    default void transfer(String suffix, String outFile) throws IOException {
        transfer(suffix, new File(outFile));
    }

    /**
     * 转化
     *
     * @param suffix  后缀
     * @param outFile 输出
     * @throws IOException ex
     */
    default void transfer(String suffix, File outFile) throws IOException {
        transfer(suffix, Files.newOutputStream(outFile.toPath()));
    }

    /**
     * 是否是压缩文件
     *
     * @return 是否是压缩文件
     */
    boolean isCompressFile();

    /**
     * 是否是图片文件
     *
     * @return 是否是图片文件
     */
    boolean isImageFile();

    /**
     * 是否是文件
     *
     * @return 是否是文件
     */
    default boolean isFile() {
        return true;
    }

    /**
     * 是否是图片文件
     *
     * @return 是否是图片文件
     */
    boolean isVideoFile();

    /**
     * 是否是逐行文件
     *
     * @return 是否是逐行文件
     */
    boolean isLineFile();

    /**
     * 是否是对象文件
     *
     * @return 是否是对象文件
     */
    boolean isObjectFile();

    /**
     * 转化文件
     *
     * @param target 目标类型
     * @param <E>    类型
     * @return E
     */
    <E> E transferFile(Class<E> target);
}
