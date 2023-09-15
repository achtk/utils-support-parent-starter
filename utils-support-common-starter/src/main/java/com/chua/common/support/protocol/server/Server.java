package com.chua.common.support.protocol.server;


import com.chua.common.support.function.InitializingAware;

import java.io.IOException;

/**
 * 服务端
 *
 * @author CH
 */
public interface Server extends InitializingAware {
    /**
     * 启动
     *
     * @throws IOException IOException
     */
    void start() throws IOException;


    /**
     * 关闭连接
     *
     * @throws IOException IOException
     */
    void close() throws IOException;

}
