package com.chua.common.support.net.resolver;

import com.chua.common.support.discovery.Discovery;
import com.chua.common.support.net.frame.Frame;

/**
 * 映射解析器
 *
 * @author CH
 * @since 2023/09/13
 */
public interface MappingResolver {

    /**
     * 解析
     * SocketAddress
     *
     * @param frame 帧
     * @return SocketAddress
     */
    Discovery resolve(Frame frame);

    /**
     * 超时
     *
     * @return int
     */
    int timeout();
}
