package com.chua.example.media;

import com.chua.common.support.file.transfer.BaseMediaConverter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CH
 */
@Slf4j
public class MediaConverterExample {

    public static void main(String[] args) {
        BaseMediaConverter mediaConverter = BaseMediaConverter.of("Z://兴隆有礼管理平台接口.md");
        log.info("{}", mediaConverter.inputFormat());
        log.info("{}", mediaConverter.outputFormat());
        log.info("{}", mediaConverter.inputOutput());
        mediaConverter.convert("Z://兴隆有礼管理平台接口.docx");
    }
}
