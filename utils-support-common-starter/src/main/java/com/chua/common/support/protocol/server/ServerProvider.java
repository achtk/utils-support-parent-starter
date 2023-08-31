package com.chua.common.support.protocol.server;

import com.chua.common.support.spi.ServiceProvider;

/**
 * 服务端生产者
 *
 * @author CH
 */
public interface ServerProvider {

    /**
     * 创建服务端
     *
     * @param option 配置
     * @param args   参数
     * @return 服务端
     */
    Server create(ServerOption option, String... args);

    /**
     * 创建服务端
     *
     * @param name 名称
     * @param args 参数
     * @return 服务端
     */
    static Server create(String name, String[] args) {
        try {
            return ServiceProvider.of(ServerProvider.class).getNewExtension(name).create(ServerOption.builder().build(), args);
        } catch (Exception e) {
            throw new NullPointerException("服务器实现不存在");
        }
    }

    /**
     * 创建服务端
     *
     * @param args   参数
     * @param name   名称
     * @param option 配置
     * @return 服务端
     */
    static Server create(String name, ServerOption option, String[] args) {
        try {
            return ServiceProvider.of(ServerProvider.class).getNewExtension(name).create(option, args);
        } catch (Exception e) {
            throw new NullPointerException("服务器实现不存在");
        }
    }

    /**
     * 创建服务端
     *
     * @param name   名称
     * @param option 配置
     * @return 服务端
     */
    static Server create(String name, ServerOption option) {
        try {
            return ServiceProvider.of(ServerProvider.class).getNewExtension(name).create(option);
        } catch (Exception e) {
            throw new NullPointerException("服务器实现不存在");
        }
    }
}
