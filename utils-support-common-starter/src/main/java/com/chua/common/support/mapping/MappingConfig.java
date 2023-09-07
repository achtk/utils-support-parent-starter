package com.chua.common.support.mapping;

import lombok.Builder;
import lombok.Data;

/**
 * 映射配置
 *
 * @author CH
 */
@Data
@Builder
public class MappingConfig {

    public static final MappingConfig DEFAULT = MappingConfig.builder().build();

    /**
     * 主机
     */
    private String host;

    /**
     * 路径
     */
    private String path;
    /**
     * 应用程序密钥
     */
    private String appKey;

    /**
     * 秘密访问密钥
     */
    private String secretAccessKey;


    /**
     * 协议
     */
    @Builder.Default
    private String[] protocol = new String[]{"https"};
}
