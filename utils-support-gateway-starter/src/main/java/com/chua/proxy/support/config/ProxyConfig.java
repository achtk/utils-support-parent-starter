package com.chua.proxy.support.config;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 代理配置
 *
 * @author CH
 * @since 2023/09/13
 */
@Data
@Builder
@Accessors(fluent = true)
public class ProxyConfig {

    /**
     * 主机
     */
    private String host;

    /**
     * 港口城市
     */
    private int port;


    /**
     * 线程数
     */
    @Builder.Default
    private int bossWorkNum = 1;

    /**
     * 连接数
     */
    @Builder.Default
    private int backlog = 1024;
    /**
     * TCP是否将数据立即发送给对方
     */
    @Builder.Default
    private boolean tcpNoDelay = true;


    /**
     * 读卡器空闲时间
     */
    @Builder.Default
    private long readerIdleTime = 30L;
    /**
     * 写入程序空闲时间
     */
    @Builder.Default
    private long writerIdleTime = 0L;
    /**
     * 所有空闲时间
     */
    @Builder.Default
    private long allIdleTime = 0L;

}
