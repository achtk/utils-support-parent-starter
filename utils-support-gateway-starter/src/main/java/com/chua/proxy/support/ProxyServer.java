package com.chua.proxy.support;

/**
 * 代理服务器
 *
 * @author CH
 * @since 2023/09/13
 */
public interface ProxyServer {

    /**
     * 开始
     *
     * @throws InterruptedException 中断异常
     */
    void start() throws InterruptedException;


    /**
     * 正在运行
     *
     * @return boolean
     */
    boolean isRunning();

    /**
     * 停止
     */
    void stop();
}
