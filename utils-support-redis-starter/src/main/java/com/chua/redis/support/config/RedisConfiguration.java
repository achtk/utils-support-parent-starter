package com.chua.redis.support.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * redis 配置
 *
 * @author CH
 */
@Data
@Accessors(chain = true)
public class RedisConfiguration {

    private static final RedisConfiguration CONFIGURATION = new RedisConfiguration();
    /**
     * 地址
     */
    private String address;
    /**
     * 地址
     */
    private String host = "127.0.0.1";
    /**
     * 端口
     */
    private int port = 6379;
    /**
     * 账号
     */
    private String user;
    /**
     * 密码
     */
    private String password;
    /**
     * 数据库
     */
    private Integer database = 8;
    /**
     * 连接超时时间
     */
    private Integer connectionTimeoutMs = 10_000;

    public static RedisConfiguration defaultConfiguration() {
        return CONFIGURATION;
    }
}
