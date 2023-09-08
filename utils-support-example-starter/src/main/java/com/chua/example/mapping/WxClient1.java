package com.chua.example.mapping;

import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.mapping.annotations.MappingAddress;
import com.chua.common.support.mapping.annotations.MappingParam;
import com.chua.common.support.mapping.annotations.MappingRequest;

/**
 * wx
 * @author CH
 */
@MappingAddress("https://api.weixin.qq.com")
public interface WxClient1 {


    /**
     * 获取关注公众号用户的openid
     * @param token 公众号token
     * @return 关注公众号的人
     */
    @MappingRequest("POST /cgi-bin/user/get?access_token={token}")
    JSONObject getGzhUsers(@MappingParam(value = "WxClient.getToken({config.appKey}, {config.appSecret})", type = MappingParam.ParamType.METHOD) String token);
}
