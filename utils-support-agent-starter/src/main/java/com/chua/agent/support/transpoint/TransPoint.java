package com.chua.agent.support.transpoint;

import com.chua.agent.support.constant.Constant;

/**
 * 数据传输
 *
 * @author CH
 */
public interface TransPoint extends Constant {
    /**
     * 连接
     */
    void connect();

    /**
     * 下发消息
     *
     * @param type    类型
     * @param message 下发消息
     */
    void publish(String type, String message);
}
