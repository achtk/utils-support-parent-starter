package com.chua.common.support.extra.api;

import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.lang.code.ResultCode;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息推送结果
 * @author CH
 */
@Data
@Builder
@Accessors(fluent = true)
public class MessageResponse {
    /**
     * 结果（00000）
     */
    private ResultCode code;
    /**
     * 消息
     */
    private String message;
    /**
     * 数据
     */
    private JSONObject data;

    /**
     * 获取数据
     * @param key key
     * @return 结果
     */
    public String getString(String key) {
        return null != data ? data.getString(key) : null;
    }
    /**
     * 获取数据
     * @param key key
     * @return 结果
     */
    public Long getLong(String key) {
        return null != data ? data.getLong(key) : null;
    }
}
