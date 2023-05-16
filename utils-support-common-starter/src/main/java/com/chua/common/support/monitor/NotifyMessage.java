package com.chua.common.support.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 通知消息
 *
 * @author CH
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotifyMessage {
    /**
     * 通知类型
     */
    private NotifyType type;
    /**
     * 消息
     */
    private String message;

    public NotifyMessage(NotifyType type) {
        this.type = type;
    }

    public NotifyMessage(Throwable throwable) {
        this(NotifyType.EXCEPTION);
    }
}
