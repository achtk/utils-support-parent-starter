package com.chua.common.support.sms;

import java.util.List;

/**
 * 短信处理器
 * @author CH
 */
public interface SmsHandler {
    /**
     * 下发请求
     * @param request 请求
     * @return 结果
     */
    List<SmsResult> send(SmsRequest request);
}
