package com.chua.agent.support.plugin;

import com.alibaba.json.JSONObject;

/**
 * 带页面接口插件
 *
 * @author CH
 */
public interface HtmlAgentPlugin extends AgentPlugin {
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
