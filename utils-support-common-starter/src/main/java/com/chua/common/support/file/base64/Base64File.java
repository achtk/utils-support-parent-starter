package com.chua.common.support.file.base64;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.AbstractResourceFile;
import com.chua.common.support.file.ResourceFile;
import com.chua.common.support.file.ResourceFileBuilder;
import com.chua.common.support.file.ResourceFileConfiguration;

/**
 * base64
 *
 * @author CH
 */
@Spi("base64")
public class Base64File extends AbstractResourceFile {
    public Base64File(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration);
    }

    /**
     * 初始化
     *
     * @param base64 base64
     * @return Base64File
     */
    public static ResourceFile of(String base64) {
        return ResourceFileBuilder.builder().open(base64);
    }
}
