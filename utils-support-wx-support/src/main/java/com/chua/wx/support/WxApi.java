package com.chua.wx.support;

import com.chua.common.support.extra.api.Api;
import com.chua.common.support.extra.api.MessageResponse;

import java.util.Map;

/**
 * 微信接口
 *
 * @author CH
 */
public interface WxApi extends Api {

    /**
     * 下发消息
     *
     * @param templateId 模板ID
     * @param data       参数
     * @param toUser     接收人
     * @return 结果
     */
    MessageResponse sendMessage(String templateId, Map<String, ?> data, String... toUser);
}
