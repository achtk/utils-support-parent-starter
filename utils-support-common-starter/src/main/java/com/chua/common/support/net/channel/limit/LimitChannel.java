package com.chua.common.support.net.channel.limit;

import com.chua.common.support.net.frame.Frame;
import com.chua.common.support.net.proxy.ProxyChannel;

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
    boolean tryAcquire(Frame frame);
}
