package com.chua.common.support.lang.robin;

import lombok.Builder;
import lombok.Data;

/**
 * 基础配置
 *
 * @author CH
 */
@Data
@Builder
public class RobinConfig {
    /**
     * 地址
     */
    private String host;
    /**
     * 端口
     */
    private int port;
    /**
     * 用户
     */
    private String user;
    /**
     * 密码
     */
    private String password;

    /**
     * 根
     */
    @Builder.Default
    private String root = "/service";
}
