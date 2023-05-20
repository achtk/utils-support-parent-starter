package com.chua.example.media;

import com.chua.common.support.file.transfer.MediaConverter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CH
 */
@Slf4j
public class MediaConverterExample {

    public static void main(String[] args) {
        MediaConverter mediaConverter = MediaConverter.of("Z://兴隆有礼管理平台接口.md");
        log.info("{}", mediaConverter.inputFormat());
        log.info("{}", mediaConverter.outputFormat());
        log.info("{}", mediaConverter.inputOutput());
        mediaConverter.convert("Z://兴隆有礼管理平台接口.docx");
    }
}
