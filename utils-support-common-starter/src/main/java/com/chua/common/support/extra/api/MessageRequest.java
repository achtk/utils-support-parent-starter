package com.chua.common.support.extra.api;

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
public class MessageRequest {
    /**
     * 模板（非必填）
     */
    private String template;
    /**
     * 接收人（必填）,多个逗号分隔
     */
    private String toUser;
    /**
     * 数据
     */
    private Object data;

    /**
     * 请求参数异常
     * @param message 信息
     * @return 结果
     */
    public static MessageResponse illegal(String message) {
        return MessageResponse.builder().message(message).build();
    }
}
