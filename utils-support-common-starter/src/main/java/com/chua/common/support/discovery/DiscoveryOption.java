package com.chua.common.support.discovery;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;

/**
 * 发现服务配置
 *
 * @author CH
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Accessors(chain = true)
public class DiscoveryOption extends LinkedHashMap<String, Object> {
    /**
     * 地址
     */
    private String address = "127.0.0.1:2181";


    /**
     * 根目录
     */
    private String root = "discovery";

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
    private String database;

    /**
     * 连接超时时间
     */
    private int connectionTimeoutMillis = 10_000;
    /**
     * 会话超时时间
     */
    private int sessionTimeoutMillis = 10_000;
    /**
     * interface
     */
    private boolean deferToInterface;
}
