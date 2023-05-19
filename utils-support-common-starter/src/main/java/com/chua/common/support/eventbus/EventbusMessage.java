package com.chua.common.support.eventbus;

import lombok.Data;

import java.io.Serializable;

/**
 * 订阅消息
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/5/20
 */
@Data
public class EventbusMessage implements Serializable {

    /**
     * 结果
     */
    private Object message;
    /**
     * 类型
     */
    private String type;

    public EventbusMessage() {
    }

    public EventbusMessage(Object message) {
        this(message, message.getClass());
    }

    public EventbusMessage(Object message, Class<?> type) {
        this.message = message;
        this.type = type.getTypeName();
    }
}
