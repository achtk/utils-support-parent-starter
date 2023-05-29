package com.chua.common.support.protocol.client;

import com.chua.common.support.file.export.ExportProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 客户端配置
 *
 * @author CH
 */
@Data
@Accessors(fluent = true)
public class ClientOption {

    private static final ClientOption DEFAULT = new ClientOption();

    /**
     * 初始化
     *
     * @return 初始化
     */
    public static ClientOption newDefault() {
        return DEFAULT;
    }

    /**
     * 最大连接数
     */
    private int maxTotal = 200;
    /**
     * 最大空闲数
     */
    private int maxIdle = 100;
    /**
     * 最小空闲数
     */
    private int minIdle = 50;
    /**
     * 最大等待时间
     */
    private long maxWaitMillis = 10000;
    /**
     * 最大连接时间
     */
    private int connectionTimeoutMillis = 10000;
    /**
     * 最大会话时间
     */
    private int sessionTimeoutMillis = 10000;
    /**
     * 重试
     */
    private int retry = 3;
    /**
     * 执行器
     */
    private Executor executor;

    /**
     * 账号
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 扩展信息
     */
    private Map ream = new LinkedHashMap<>();
    /**
     * 保留策略
     */
    private String retentionPolicy;
    /**
     * 数据库
     */
    @ExportProperty("url")
    private String database;
    /**
     * 驱动
     */
    private String driver;


    public static ClientOption newBuilder() {
        return new ClientOption();
    }


    public ClientOption add(String name, Object value) {
        ream.put(name, value);
        return this;
    }
}
