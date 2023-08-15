package com.chua.agent.support.server;

import com.chua.agent.support.constant.Constant;

/**
 * 服务
 *
 * @author CH
 */
public interface EmbedServer extends Constant {
    /**
     * 启动
     */
    void start();

    /**
     * 停止
     */
    void stop();
}
