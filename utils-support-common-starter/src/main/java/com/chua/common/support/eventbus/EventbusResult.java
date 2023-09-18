package com.chua.common.support.eventbus;

import com.chua.common.support.lang.code.ResultCode;
import lombok.Builder;
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
@Builder
public class EventbusResult implements Serializable {
    /**
     * 状态
     */
    private ResultCode code;
    /**
     * 错误消息
     */
    private String msg;
}
