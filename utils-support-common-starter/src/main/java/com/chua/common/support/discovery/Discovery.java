package com.chua.common.support.discovery;

import com.chua.common.support.json.Json;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 发现实例
 *
 * @author CH
 * @version 1.0.0
 */
@Data
@Builder
@Accessors(chain = true)
public class Discovery implements Serializable {

    /**
     * id
     */
    private String id;
    /**
     * 协议
     */
    private String protocol;
    /**
     * header
     */
    private Map<String, String> headers = new LinkedHashMap<>();
    /**
     * 超时
     */
    private int timeout;
    /**
     * 权
     */
    private double weight;
    /**
     * 地址
     */
    private String ip;

    /**
     * 端口
     */
    private int port;
    /**
     * 格式
     */
    private String uriSpec;
    /**
     * 数据
     */
    private Map<String, String> metadata;

    public String toFullString() {
        return Json.toJson(this);
    }
}
