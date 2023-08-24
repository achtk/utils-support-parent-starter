package com.chua.common.support.message;

import com.chua.common.support.extra.api.MessageRequest;
import com.chua.common.support.extra.api.MessageResponse;

/**
 * 消息推送
 *
 * @author CH
 */
public interface MessageSender extends AutoCloseable{
    /**
     * 下发消息
     *
     * @param request 请求
     * @return 结果
     */
    MessageResponse send(MessageRequest request);
}
