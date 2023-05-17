package com.chua.common.support.file.filesystem;

import com.chua.common.support.binary.ByteSource;
import com.chua.common.support.binary.ByteSourceArray;
import com.chua.common.support.binary.ByteSourceInputStream;
import com.chua.common.support.file.ResourceFile;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_SLASH;
import static com.chua.common.support.constant.CommonConstant.UNKNOWN;

/**
 * 文件系统
 *
 * @author CH
 */
public abstract class OsFileSystem {

    private ByteSource byteSource;
    private static final String BASE64 = ";base64,";

    private String contentType = UNKNOWN;

    private OsFileSystem(ByteSource byteSource) {
        this.byteSource = byteSource;
    }

    public OsFileSystem(ResourceFile resourceFile) {
        this.contentType = StringUtils.defaultString(Optional.ofNullable(resourceFile.getContentType())
                .orElse(ContentTypeUtils.getType(resourceFile.getContentType())), UNKNOWN);
        try {
            this.byteSource = new ByteSourceInputStream(resourceFile.openInputStream());
        } catch (IOException e) {
            this.byteSource = new ByteSourceArray(new byte[0]);
        }
    }

    public OsFileSystem(InputStream inputStream) {
        this.byteSource = new ByteSourceInputStream(inputStream);
        try (InputStream is = byteSource.getInputStream()) {
            this.contentType = IoUtils.getMimeType(is);
        } catch (IOException ignored) {
        }
    }

    /**
     * 文件类型
     *
     * @return 文件类型
     */
    public String contentType() {
        return contentType;
    }

    /**
     * 文件类型
     *
     * @return 文件类型
     */
    public String suffix() {
        String contentType = contentType();
        if (!contentType.contains(SYMBOL_LEFT_SLASH)) {
            return contentType;
        }
        return contentType.substring(contentType.lastIndexOf(SYMBOL_LEFT_SLASH) + 1);
    }

    /**
     * 打开文件
     *
     * @param bytes 文件
     */
    public static OsFileSystem open(byte[] bytes) throws IOException {
        ByteSourceArray byteSourceArray = new ByteSourceArray(bytes);
        try (InputStream inputStream1 = byteSourceArray.getInputStream()) {
            String mimeType = IoUtils.getMimeType(inputStream1);
            return of(StringUtils.defaultString(mimeType, UNKNOWN), byteSourceArray.getInputStream());
        }

    }

    /**
     * 打开文件
     *
     * @param inputStream 文件
     */
    public static OsFileSystem open(InputStream inputStream) throws IOException {
        try (InputStream inputStream1 = IoUtils.copy(inputStream)) {
            String mimeType = IoUtils.getMimeType(inputStream1);
            return of(StringUtils.defaultString(mimeType, UNKNOWN), inputStream);
        }

    }

    /**
     * 打开文件
     *
     * @param file 文件
     */
    public static OsFileSystem open(String file) {
        File temp = new File(file);
        if (temp.exists()) {
            return of(ResourceFile.of(temp));
        }

        if (!temp.exists() && file.contains(BASE64)) {
            return of(Base64File.of(file));
        }

        return of(new TempFile());
    }

    /**
     * 打开文件
     *
     * @param inputStream 文件
     */
    private static OsFileSystem of(String contentType, InputStream inputStream) {
        return ServiceProvider.of(OsFileSystem.class).getNewExtension(contentType, "temp", inputStream);
    }

    /**
     * 打开文件
     *
     * @param file 文件
     */
    private static OsFileSystem of(ResourceFile file) {
        String contentType = Optional.ofNullable(file.getContentType()).orElse(Optional.of(ContentTypeUtils.getType(file.getContentType())).orElse("temp"));
        if (file instanceof Base64File) {
            contentType = "base64";
        }

        contentType = StringUtils.defaultString(contentType, "temp");
        return ServiceProvider.of(OsFileSystem.class).getNewExtension(contentType, "temp", file);
    }

    /**
     * 获取流
     *
     * @return 流
     */
    public InputStream openStream() throws IOException {
        return byteSource.getInputStream();
    }

    /**
     * 转为文件
     *
     * @return 文件
     */
    public TransferFile transfer() {
        return new TransferFile.FileTransferFile(this);
    }

    /**
     * 类型转化
     *
     * @param target 目标类型
     * @param <E>    类型
     * @return 结果
     */
    abstract <E> E transfer(Class<E> target);
}
