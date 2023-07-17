package com.chua.common.support.sms;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

/**
 * 短信
 * @author CH
 */
@Builder
@Data
public class SmsRequest {

    @Singular("phone")
    private Set<String> phone;
    /**
     * SmsType为模板时: content填写模板ID
     * SmsType为普通短信时: content填写短信内容
     */
    private String content;
    /**
     * 业务ID
     */
    private String serial;
    /**
     * 类型
     */
    @Builder.Default
    private SmsType smsType = SmsType.NORMAL;
    /**
     * 模板短信的模板参数
     */
    private String[] params;
}
