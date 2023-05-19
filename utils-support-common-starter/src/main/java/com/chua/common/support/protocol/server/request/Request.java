package com.chua.common.support.protocol.server.request;

import java.util.Map;

/**
 * 请求
 *
 * @author CH
 */
public interface Request {
    /**
     * 动作
     *
     * @return 动作
     */
    String getAction();

    /**
     * 获取值
     *
     * @param value 值
     * @return 值
     */
    String getParameter(String value);

    /**
     * 获取值
     *
     * @param value 值
     * @return 值
     */
    String getBinder(String value);

    /**
     * 所有参数
     *
     * @return 所有参数
     */
    Map<String, Object> getParameters();

    /**
     * 获取值
     *
     * @param value 值
     * @return 值
     */
    String getHeader(String value);
}
