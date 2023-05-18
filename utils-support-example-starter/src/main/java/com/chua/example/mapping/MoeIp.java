package com.chua.example.mapping;

import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.mapping.annotation.MappingAddress;
import com.chua.common.support.mapping.annotation.MappingParam;
import com.chua.common.support.mapping.annotation.MappingRequest;

/**
 * Moe IP
 *
 * @author CH
 */
@MappingAddress("https://ip-moe.zerodream.net")
public interface MoeIp {
    /**
     * 翻译IP
     *
     * @param ip IP
     * @return 结果
     */
    @MappingRequest("GET /")
    JSONObject analysis(@MappingParam("ip") String ip);
}
