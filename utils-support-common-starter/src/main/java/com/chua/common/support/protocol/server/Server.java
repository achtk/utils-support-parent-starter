package com.chua.common.support.protocol.server;


import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.spi.ServiceProvider;

import java.io.IOException;

/**
 * 服务端
 *
 * @author CH
 */
public interface Server extends InitializingAware {
    /**
     * 创建服务器
     *
     * @param name   名称(实现方式)
     * @return {@link Server}
     */
    static ServerProvider createServerProvider(String name) {
        return ServiceProvider.of(ServerProvider.class).getExtension(name);
    }

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
