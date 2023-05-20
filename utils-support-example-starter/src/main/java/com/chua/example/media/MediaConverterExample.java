package com.chua.example.media;

import com.chua.common.support.file.transfer.MediaConverter;

/**
 * @author CH
 */
public class MediaConverterExample {

    public static void main(String[] args) {

        MediaConverter mediaConverter = MediaConverter.of("Z://兴隆有礼管理平台接口.md");
        mediaConverter.convert("Z://兴隆有礼管理平台接口.docx");
    }
}
