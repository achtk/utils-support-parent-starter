package com.chua.example.mapping;

import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.mapping.annotation.MappingAddress;
import com.chua.common.support.mapping.annotation.MappingParam;
import com.chua.common.support.mapping.annotation.MappingRequest;
import com.chua.common.support.task.cache.Cache;

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
    @Cache
    JSONObject analysis(@MappingParam("ip") String ip);
}
