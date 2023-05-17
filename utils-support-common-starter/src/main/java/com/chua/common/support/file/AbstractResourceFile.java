package com.chua.common.support.file;

import com.chua.common.support.binary.ByteSource;
import com.chua.common.support.file.converter.MediaConverter;
import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.resource.ResourceProvider;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.Md5Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.*;
import java.util.Base64;

/**
 * 资源文件
 *
 * @author CH
 */
public abstract class AbstractResourceFile implements ResourceFile {

    protected final ResourceFileConfiguration resourceFileConfiguration;

    public AbstractResourceFile(ResourceFileConfiguration resourceFileConfiguration) {
        this.resourceFileConfiguration = resourceFileConfiguration;
    }

    @Override
    public File toFile() {
        return null == resourceFileConfiguration.getSource() ? createFile() : resourceFileConfiguration.getSource();
    }

    /**
     * 是否是临时文件
     *
     * @return 是否是临时文件
     */
    protected boolean isTempFile() {
        return null == resourceFileConfiguration.getSource();
    }

    @Override
    public String toBase64() {
        byte[] buffer;
        try {
            buffer = IoUtils.toByteArray(openInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(buffer);
    }

    /**
     * 创建文件
     *
     * @return 文件
     */
    private File createFile() {
        Path tempPath = null;
        try {
            tempPath = Files.createTempFile("temp_", "." + resourceFileConfiguration.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream inputStream = openInputStream()) {
            CopyOption[] copyOption = new CopyOption[0];
            if (Files.exists(tempPath)) {
                copyOption = new CopyOption[1];
                copyOption[0] = StandardCopyOption.REPLACE_EXISTING;
            }
            Files.copy(inputStream, tempPath, copyOption);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tempPath.toFile();
    }

    @Override
    public InputStream openInputStream() throws IOException {
        ByteSource byteSource = resourceFileConfiguration.getByteSource();
        if (null != byteSource) {
            return byteSource.getInputStream();
        }

        File source = resourceFileConfiguration.getSource();
        if (null != source) {
            return Files.newInputStream(source.toPath());
        }

        String sourceUrl = resourceFileConfiguration.getSourceUrl();
        if (new File(sourceUrl).exists()) {
            return Files.newInputStream(Paths.get(sourceUrl));
        }

        Resource resource = ResourceProvider.of(resourceFileConfiguration.getSourceUrl()).getResource();
        if (null != resource) {
            return resource.openStream();
        }

        URL url = new URL(sourceUrl);
        return url.openStream();
    }

    @Override
    public void transfer(String suffix, OutputStream outputStream) throws IOException {
        MediaConverter.of(resourceFileConfiguration.getSource()).convert(suffix, outputStream);
    }

    @Override
    public String getContentType() {
        return resourceFileConfiguration.getContentType();
    }

    @Override
    public String formatName() {
        return resourceFileConfiguration.getType();
    }

    @Override
    public String subtype() {
        return resourceFileConfiguration.getSubtype();
    }

    @Override
    public long size() {
        try {
            return resourceFileConfiguration.getByteSource().getLength();
        } catch (IOException e) {
            return -1L;
        }
    }

    @Override
    public long lastModified() {
        File source = resourceFileConfiguration.getSource();
        return null == source ? -1L : source.lastModified();
    }

    @Override
    public String md5() {
        try (InputStream inputStream = openInputStream()) {
            return Md5Utils.getInstance().getMd5String(IoUtils.toByteArray(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isCompressFile() {
        return this instanceof CompressFile;
    }

    @Override
    public boolean isImageFile() {
        return this instanceof ImageFile;
    }

    @Override
    public boolean isVideoFile() {
        return this instanceof VideoFile;
    }

    @Override
    public boolean isLineFile() {
        return this instanceof LineFile;
    }

    @Override
    public boolean isObjectFile() {
        return this instanceof ObjectFile;
    }

    @Override
    public <E> E transferFile(Class<E> target) {
        if (target.isAssignableFrom(this.getClass())) {
            return (E) this;
        }
        return ProxyUtils.newProxy(target, new DelegateMethodIntercept<>(target, null));
    }
}
