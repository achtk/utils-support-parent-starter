package com.chua.example.media;

import com.chua.common.support.media.MediaType;
import com.chua.common.support.media.MediaTypeFactory;

import java.util.Optional;

/**
 * @author CH
 */
public class MediaExample {
    public static void main(String[] args) {
        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType("1.jar");
        System.out.println();
    }
}
