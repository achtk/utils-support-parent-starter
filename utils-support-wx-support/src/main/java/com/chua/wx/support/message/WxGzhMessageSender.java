package com.chua.wx.support.message;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.extra.api.MessageRequest;
import com.chua.common.support.extra.api.MessageResponse;
import com.chua.common.support.json.Json;
import com.chua.common.support.message.MessageSender;
import com.chua.common.support.utils.StringUtils;
import com.chua.wx.support.WxGzh;
import lombok.extern.slf4j.Slf4j;

/**
 * 微信公众号
 * @author CH
 */
@Slf4j
public class WxGzhMessageSender implements MessageSender {

    private final WxGzh wxGzh;

    public WxGzhMessageSender(String appId, String appSecret) {
        this.wxGzh = new WxGzh(appId, appSecret);
    }


    @Override
    public MessageResponse send(MessageRequest request) {
        log.info("===================开始模板推送======================");

        if (StringUtils.isEmpty(request.toUser())) {
            return MessageRequest.illegal("请填写接收人信息");
        }
        if(log.isDebugEnabled()) {
            log.debug("填充数据为：{}", Json.prettyFormat(request.data()));
        }

        return wxGzh.sendMessage(request.template(), BeanMap.create(request.data()), request.toUser().split(","));
    }

    @Override
    public void close() throws Exception {

    }
}
