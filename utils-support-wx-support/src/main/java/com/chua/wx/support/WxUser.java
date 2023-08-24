package com.chua.wx.support;

import lombok.Data;

/**
 * 微信用户信息
 * @author CH
 */
@Data
public class WxUser {
    /**
     * openId
     */
    private String openId;

    /**
     * unionId
     */
    private String unionId;

}
