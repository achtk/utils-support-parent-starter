package com.chua.common.support.mapping.invoke.hik.util;


import com.chua.common.support.http.HttpResponse;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 回答
 *
 * @author CH
 * @since 2023/09/06
 */
@Data
public class HikResponse {
    private int statusCode;
    private String contentType;
    private String requestId;
    private String errorMessage;
    private Map<String, String> headers;
    private String body;
    private HttpResponse response;

    public String getHeader(String key) {
        if (null != headers) {
            return headers.get(key);
        } else {
            return null;
        }
    }

    public void setHeader(String key, String value) {
        if (null == this.headers) {
            this.headers = new HashMap<>();
        }
        this.headers.put(key, value);
    }


}
