package com.chua.common.support.mapping.invoke.hik.util;


import com.chua.common.support.http.HttpMethod;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Request
 *
 * @author CH
 * @since 2023/09/06
 */
@Data
public class HikRequest {

    public HikRequest(HttpMethod method, String host, String path, String appKey, String appSecret, int timeout) {
        this.method = method;
        this.host = host;
        this.path = path;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.timeout = timeout;
    }

    /**
     * （必选）请求方法
     */
    private HttpMethod method;

    /**
     * （必选）Host
     */
    private String host;

    /**
     * （必选）Path
     */
    private String path;

    /**
     * （必选)APP KEY
     */
    private String appKey;

    /**
     * （必选）APP密钥
     */
    private String appSecret;

    /**
     * （必选）超时时间,单位毫秒,设置零默认使用com.aliyun.apigateway.demo.constant.Constants.DEFAULT_TIMEOUT
     */
    private int timeout;

    /**
     * （可选） HTTP头
     */
    private Map<String, String> headers;

    /**
     * （可选） Querys
     */
    private Map<String, String> querys;

    /**
     * （可选）表单参数
     */
    private Map<String, String> bodys;

    /**
     * （可选）字符串Body体
     */
    private String stringBody;

    /**
     * （可选）字节数组类型Body体
     */
    private byte[] bytesBody;

    /**
     * （可选）自定义参与签名Header前缀
     */
    private List<String> signHeaderPrefixList;
}
