package com.chua.common.support.sms;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 短信
 * @author CH
 */
@Data
@AllArgsConstructor
public class SmsResult {
    /**
     * 手机号
     */
    private String phone;
    /**
     * 状态码
     */
    private int code;
}
