package com.chua.common.support.resource.repository;

import com.chua.common.support.media.MediaType;
import com.chua.common.support.media.MediaTypeFactory;
import com.chua.common.support.utils.FileUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

/**
 * 元数据
 *
 * @author CH
 */
@Data
@Accessors(chain = true)
public final class FileSystemMetadata implements Metadata {
    private final File file;
    private final Optional<MediaType> mediaType;

    private boolean isEqualsOrigin = true;

    public FileSystemMetadata(File file) {
        this.file = file;
        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(getName());
        this.mediaType = mediaType;
    }


    public FileSystemMetadata(String file) {
        this(new File(file));
    }

    @Override
    public URL toUrl() {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream openInputStream() {
        try {
            return Files.newInputStream(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getSize() {
        if (file.isFile()) {
            return file.length();
        }

        return FileUtils.sizeOfDirectory(file);
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public boolean isCompressFile() {
        return FileUtils.isCompressFile(this.getName());
    }

    @Override
    public boolean isImage() {
        return mediaType.filter(type -> "image".equals(type.type())).isPresent();
    }

    @Override
    public String getContentType() {
        return mediaType.map(MediaType::toString).orElse(null);
    }

    @Override
    public String getType() {
        return mediaType.map(MediaType::type).orElse(null);
    }

    @Override
    public String getSubType() {
        return mediaType.map(MediaType::subtype).orElse(null);
    }
}
