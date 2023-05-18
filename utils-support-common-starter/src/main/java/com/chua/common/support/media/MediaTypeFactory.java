package com.chua.common.support.media;

import com.chua.common.support.collection.MultiLinkedValueMap;
import com.chua.common.support.collection.MultiValueMap;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.constant.CommonConstant.EMPTY;

/**
 * media
 *
 * @author CH
 */
public class MediaTypeFactory {
    private static final String[] MIME_TYPES_FILE_NAME = new String[]{"/mime/types/mime.types"};
    private static final Map<String, String> FILE_EXTENSION = new ConcurrentHashMap<>();
    private static final MultiValueMap<String, MediaType> FILE_EXTENSION_TO_MEDIA_TYPES = parseMimeTypes();

    private MediaTypeFactory() {
    }

    /**
     * 初始化
     *
     * @return 初始化
     */
    private static MultiValueMap<String, MediaType> parseMimeTypes() {
        MultiLinkedValueMap<String, MediaType> result = new MultiLinkedValueMap<>();
        for (String s : MIME_TYPES_FILE_NAME) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(IoUtils.newClassPathStream(s), StandardCharsets.US_ASCII))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty() || line.charAt(0) == '#') {
                        continue;
                    }
                    String[] tokens = StringUtils.tokenizeToStringArray(line, " \t\n\r\f");
                    MediaType mediaType = MediaType.parse(tokens[0]);
                    for (int i = 1; i < tokens.length; i++) {
                        String fileExtension = tokens[i].toLowerCase(Locale.ENGLISH);
                        mediaType.addParameter("suffix", fileExtension);
                        result.add(fileExtension, mediaType);
                        FILE_EXTENSION.put(mediaType.toString(), fileExtension);
                    }
                }
                return result;
            } catch (IOException ignored) {
            }
        }


        return result;
    }

    /**
     * 获取媒体
     *
     * @param url 文件名
     * @return the corresponding media type, or {@code null} if none found
     */
    public static String getExtension(URL url) {
        try {
            return FILE_EXTENSION.get(url.openConnection().getContentType());
        } catch (IOException e) {
            return EMPTY;
        }
    }

    /**
     * 获取媒体
     *
     * @param filename 文件名
     * @return the corresponding media type, or {@code null} if none found
     */
    public static Optional<MediaType> getMediaType(String filename) {
        return getMediaTypes(filename).stream().findFirst();
    }

    /**
     * 获取媒体
     *
     * @param filename 文件名
     * @return the corresponding media types, or an empty list if none found
     */
    public static List<MediaType> getMediaTypes(String filename) {
        List<MediaType> mediaTypes = null;
        String ext = filename;

        while (!StringUtils.isEmpty(ext = FileUtils.getSimpleExtension(ext))) {
            mediaTypes = FILE_EXTENSION_TO_MEDIA_TYPES.get(ext.toLowerCase(Locale.ENGLISH));
            if (null == mediaTypes) {
                continue;
            }

            return mediaTypes;
        }

        return Collections.emptyList();
    }

}
