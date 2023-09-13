package com.chua.common.support.net.proxy;

/**
 * 限流通道
 *
 * @author CH
 * @since 2023/09/13
 */
public interface LimitChannel extends ProxyChannel {


    /**
     * 尝试获取
     *
     * @param frame 资源
     * @return 尝试获取
     */
    boolean tryAcquire(LimitFrame frame);
}
