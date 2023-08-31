package com.chua.common.support.file;

import com.chua.common.support.binary.BaseByteSource;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 配置项
 *
 * @author CH
 */
@Data
@Builder
@Accessors(chain = true)
public class ResourceFileConfiguration {
    /**
     * 块大小
     */
    private int buffer;
    /**
     * 资源文件实现方式
     */
    private String type;
    /**
     * 文件类型(后缀)
     */
    private String subtype;
    /**
     * contentType
     */
    private String contentType;
    /**
     * 文件
     */
    private BaseByteSource byteSource;
    /**
     * 源文件
     */
    private File source;
    /**
     * 源文件地址
     */
    private String sourceUrl;
    /**
     * 编码
     */
    @Builder.Default
    private Charset charset = UTF_8;
}
