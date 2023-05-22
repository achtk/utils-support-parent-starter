package com.chua.redis.support.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * redis 配置
 *
 * @author CH
 */
@Data
@Accessors(chain = true)
public class RedisConfiguration {

    private static final RedisConfiguration CONFIGURATION = new RedisConfiguration();
    private int database = 0;

    private String config;
    private String file;
    private String url;
    private String host = "localhost";
    private String username;
    private String password;
    private int port = 6379;
    private boolean ssl;
    private Duration timeout = Duration.of(10, ChronoUnit.SECONDS);
    private Duration connectTimeout = Duration.of(10, ChronoUnit.SECONDS);
    private String clientName;

    public static RedisConfiguration defaultConfiguration() {
        return CONFIGURATION;
    }
}
