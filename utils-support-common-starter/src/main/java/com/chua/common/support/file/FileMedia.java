package com.chua.common.support.file;

import com.chua.common.support.constant.FileType;
import lombok.Builder;
import lombok.Data;

import java.io.InputStream;
import java.util.Optional;

/**
 * 类型
 *
 * @author CH
 */
@Data
@Builder
public class FileMedia {

    private FileType fileType;

    private String name;

    private MediaType mediaType;

    private long size;

    private InputStream stream;

    public MediaType getMediaType() {
        if (mediaType == null) {
            Optional<MediaType> mediaType1 = MediaTypeFactory.getMediaType(name);
            return mediaType1.orElse(MediaType.ANY_TYPE);
        }
        return mediaType;
    }
}
