package com.chua.common.support.utils;

import com.chua.common.support.media.MediaType;
import com.chua.common.support.media.MediaTypeFactory;

import java.util.Optional;

import static com.chua.common.support.constant.CommonConstant.UNKNOWN;

/**
 * ContentType
 *
 * @author CH
 */
public class ContentTypeUtils {
    /**
     * 获取类型
     *
     * @param contentType 类型
     * @return 类型
     */
    public static String getType(String contentType) {
        if (StringUtils.isNullOrEmpty(contentType)) {
            return UNKNOWN;
        }

        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(contentType);
        return mediaType.map(MediaType::type).orElse(UNKNOWN);
    }
}
