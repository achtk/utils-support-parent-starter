package com.chua.common.support.sms;

import lombok.Builder;
import lombok.Data;

/**
 * 短信
 * @author CH
 */
@Builder
@Data
public class SmsConfiguration {
    static final SmsConfiguration DEFAULT = SmsConfiguration.builder().build();

    /**
     * 服务器地址
     */
    @Builder.Default
    private String address = "http://112.35.4.197:15000";

    /**
     * 企业
     */
    private String ecName;
    /**
     * 账号
     */
    private String appKey;
    /**
     * 密码
     */
    private String appSecure;
    /**
     * sign
     */
    private String sign;
    /**
     * 下发间隔(s)
     */
    @Builder.Default
    private Integer intervals = 60;

    public static SmsConfiguration newDefault() {
        return DEFAULT;
    }
}
