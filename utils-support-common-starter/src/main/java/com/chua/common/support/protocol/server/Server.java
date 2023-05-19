package com.chua.common.support.protocol.server;


import com.chua.common.support.function.InitializingAware;

/**
 * 服务端
 *
 * @author CH
 */
public interface Server extends InitializingAware, AutoCloseable {
    /**
     * 启动
     */
    void start();

    /**
     * 注册对象
     *
     * @param bean 对象
     * @return this
     */
    Server register(Object bean);

    /**
     * 注册对象
     *
     * @param name name
     * @param bean 对象
     * @return this
     */
    Server register(String name, Object bean);

    /**
     * 关闭连接
     */
    @Override
    void close();

}
