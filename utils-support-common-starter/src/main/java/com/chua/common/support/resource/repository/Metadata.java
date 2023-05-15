package com.chua.common.support.resource.repository;

import com.chua.common.support.exception.NotSupportedException;
import com.chua.common.support.file.Decompress;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.Md5Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static com.chua.common.support.constant.CommonConstant.FILE;

/**
 * 元数据
 *
 * @author CH
 */
public interface Metadata {
    /**
     * url
     *
     * @return url
     */
    URL toUrl();

    /**
     * File
     *
     * @return File
     */
    default File toFile() {
        URL url = toUrl();
        if (null == url) {
            return null;
        }

        if (FILE.equalsIgnoreCase(url.getProtocol())) {
            return new File(url.getFile());
        }


        String md5String = Md5Utils.getInstance().getMd5String(url.toExternalForm());
        String repositoryMetadata = System.getProperty("repository_metadata", "./.repository.metadata");
        FileUtils.mkdir(repositoryMetadata);
        File temp = new File(repositoryMetadata, md5String);
        if (temp.exists()) {
            return temp;
        }

        try (InputStream inputStream = url.openStream();
             FileOutputStream outputStream = new FileOutputStream(temp);
        ) {
            IoUtils.write(inputStream, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return temp;
    }

    /**
     * 打开流
     *
     * @return 打开流
     */
    InputStream openInputStream();

    /**
     * 文件大小
     *
     * @return 文件大小
     */
    long getSize();

    /**
     * uri
     *
     * @return uri
     */
    default URI toUri() {
        try {
            return toUrl().toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件名
     * @return 文件名
     */
    String getName();

    /**
     * 是否是压缩文件
     * @return 压缩文件
     */
    boolean isCompressFile();

    /**
     * 解析到目录
     * @param property 解析到目录
     */
    default void transferTo(String[] property) {
        for (String s : property) {
            File file = new File(s, this.getName());
            try (InputStream inputStream = this.openInputStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(file);
            ) {

                IoUtils.copy(inputStream, fileOutputStream);
                return;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 解压压缩包到目录
     * @param property 解压压缩包到目录
     */
    default void unTransferTo(String[] property) {
        Decompress decompress = ServiceProvider.of(Decompress.class).getNewExtension(FileUtils.getSimpleExtension(getName()));
        if(null == decompress) {
            throw new NotSupportedException("不支持压缩文件: " + getName());
        }

        for (String s : property) {
            File file = new File(s);
            try (InputStream inputStream = this.openInputStream();
            ) {
                decompress.unFile(inputStream, file);
                return;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 是否是图片
     * @return 是否是图片
     */
    boolean isImage();

    /**
     * content-type
     * @return  content-type
     */
    String getContentType();

    /**
     * type
     * @return  type
     */
    String getType();

    /**
     * type
     * @return  type
     */
    String getSubType();
}
