package com.chua.common.support.sms;

import java.util.Map;

/**
 * sms client
 *
 * @author CH
 */
public interface SmsClient {
    /**
     * 发送短信.
     *
     * @param smsTemplate 短信模板
     * @return res
     */
    Map<String, String> send(final SmsTemplate smsTemplate);
}
