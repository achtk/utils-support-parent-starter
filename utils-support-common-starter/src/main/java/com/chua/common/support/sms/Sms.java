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
public class Sms {

    @Singular("phone")
    private Set<String> phone;
    /**
     * 服务器地址
     */
    private String address;

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
     * 内容
     */
    private String content;
    /**
     * sign
     */
    private String sign;
    /**
     * 业务ID
     */
    private String serial;
}
