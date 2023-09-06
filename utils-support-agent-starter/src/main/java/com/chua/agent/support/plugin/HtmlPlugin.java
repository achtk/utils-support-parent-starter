package com.chua.agent.support.plugin;

import com.chua.agent.support.json.JSONObject;

/**
 * httpPlugn
 */
public interface HtmlPlugin extends Plugin {

    /**
     * 获取地址
     *
     * @param address 地址
     */
    void setAddress(String address);

    /**
     * 获取参数
     *
     * @param parameter 参数
     */
    void setParameter(JSONObject parameter);
}
