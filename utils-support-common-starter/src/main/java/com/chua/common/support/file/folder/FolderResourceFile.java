package com.chua.common.support.file.folder;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.ResourceFile;
import com.chua.common.support.file.ResourceFileConfiguration;
import com.chua.common.support.file.transfer.MediaConverter;
import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.chua.common.support.constant.CommonConstant.UNKNOWN;

/**
 * 目录文件夹
 *
 * @author CH
 */
@Spi("folder")
public class FolderResourceFile implements ResourceFile {

    private final ResourceFileConfiguration resourceConfiguration;

    public FolderResourceFile(ResourceFileConfiguration resourceConfiguration) {
        this.resourceConfiguration = resourceConfiguration;
    }

    @Override
    public String md5() {
        return null;
    }

    @Override
    public String toBase64() {
        return null;
    }

    @Override
    public long size() {
        return FileUtils.sizeOfDirectory(resourceConfiguration.getSource());
    }

    @Override
    public long lastModified() {
        return resourceConfiguration.getSource().lastModified();
    }

    @Override
    public File toFile() {
        return resourceConfiguration.getSource();
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return null;
    }

    @Override
    public String formatName() {
        return "folder";
    }

    @Override
    public String subtype() {
        return null;
    }

    @Override
    public String getContentType() {
        return UNKNOWN;
    }

    @Override
    public void transfer(String suffix, OutputStream outputStream) throws IOException {
        MediaConverter.of(resourceConfiguration.getSource()).convert(suffix, outputStream);
    }

    @Override
    public boolean isCompressFile() {
        return false;
    }

    @Override
    public boolean isImageFile() {
        return false;
    }

    @Override
    public boolean isVideoFile() {
        return false;
    }

    @Override
    public boolean isLineFile() {
        return false;
    }

    @Override
    public boolean isObjectFile() {
        return false;
    }

    @Override
    public <E> E transferFile(Class<E> target) {
        if (target.isAssignableFrom(this.getClass())) {
            return (E) this;
        }
        return ProxyUtils.newProxy(target, new DelegateMethodIntercept<>(target, null));
    }

    @Override
    public boolean isFile() {
        return false;
    }


}
