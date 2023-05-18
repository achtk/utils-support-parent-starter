package com.chua.example.mapping;

import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.mapping.annotation.*;

/**
 * 有人
 *
 * @author CH
 */
@MappingAddress("https://openapi.mp.usr.cn")
public interface MpTarget {

    /**
     * 获取token
     *
     * @param appKey    ak
     * @param appSecret sk
     * @return token
     */
    
    @MappingRequest("POST /usrCloud/user/getAuthToken")
    @MappingResponse("$.data.X-Access-Token")
    String getAccessToken(@MappingParam("appKey") String appKey, @MappingParam("appSecret") String appSecret);


    /**
     * 获取用户信息
     *
     * @param appKey    ak
     * @param appSecret sk
     * @return token
     */
    
    @MappingHeader(name = "X-Access-Token", script = "getAccessToken(${appKey}, ${appSecret})")
    @MappingRequest("POST /usrCloud/user/getUser")
    @MappingResponse("$.data")
    JSONObject getUser(@MappingParam("appKey") String appKey, @MappingParam("appSecret") String appSecret, @MappingParam("account") String account);

    /**
     * 获取用户信息
     *
     * @return token
     */
    
    @MappingRequest("POST /usrCloud/user/getUser")
    @MappingResponse("$.data")
    JSONObject getUser(@MappingParam("account") String account, @MappingHeader(name = "X-Access-Token") String token);


}
