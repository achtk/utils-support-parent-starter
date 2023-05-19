package com.chua.common.support.protocol.server;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 客户端配置
 *
 * @author CH
 */
@Data
@Builder
@Accessors(fluent = true)
public class ServerOption implements Serializable {
    /**
     * 主机
     */
    @Builder.Default
    private String host = "0.0.0.0";
    /**
     * 端口
     */
    @Builder.Default
    private int port = 12345;
    /**
     * 最大连接数
     */
    @Builder.Default
    private int maxTotal = 200;
    /**
     * 最大空闲数
     */
    @Builder.Default
    private int maxIdle = 100;
    /**
     * 可连接队列
     */
    @Builder.Default
    private int backlog = 128;
    /**
     * 最小空闲数
     */
    @Builder.Default
    private int minIdle = 50;
    /**
     * 最大等待时间
     */
    @Builder.Default
    private long maxWaitMillis = 10000;
    /**
     * 最大连接时间
     */
    @Builder.Default
    private int connectionTimeoutMillis = 10000;
    /**
     * 最大会话时间
     */
    @Builder.Default
    private int sessionTimeoutMillis = 10000;
    /**
     * 重试
     */
    @Builder.Default
    private int retry = 3;
    /**
     * 扫描的包
     */
    private String[] packages;
    /**
     * 执行器
     */
    private Executor executor;
    /**
     * 数据存储位置
     */
    private String store;
    /**
     * 数据存储名称
     */
    private String storeName;
    /**
     * 账号
     */
    private String username;
    /**
     * 是否扫描bean
     */
    private boolean autoScanner;
    /**
     * 密码
     */
    private String password;

    @Singular("bean")
    private List<Object> bean;

    @Singular("parameter")
    private Map<String, Object> parameter;


}
