package com.chua.common.support.protocol.server;


/**
 * 服务端
 *
 * @author CH
 */
public interface BeanServer extends Server {

    /**
     * 注册对象
     *
     * @param bean 对象
     * @return this
     */
    BeanServer register(Object bean);

    /**
     * 注册对象
     *
     * @param name name
     * @param bean 对象
     * @return this
     */
    BeanServer register(String name, Object bean);

}
