package com.chua.proxy.support.message;

import com.alibaba.fastjson2.JSONObject;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import static com.chua.common.support.http.HttpConstant.*;

/**
 * 消息
 *
 * @author CH
 * @since 2023/09/13
 */
public class LimitMessage {
    private final FullHttpRequest request;
    private final String message;
    private final String s;

    public LimitMessage(FullHttpRequest request, String message, String s) {
        this.request = request;
        this.message = message;
        this.s = s;
    }


    public byte[] toByteArray() {
        HttpHeaders headers = request.headers();
        String accept = headers.get(ACCEPT, ANY);
        if (ANY.equalsIgnoreCase(accept) || accept.contains(APPLICATION_JSON)) {
            return new JSONObject()
                    .fluentPut("code", s)
                    .fluentPut("msg", message)
                    .toJSONBBytes();
        }

        return "".getBytes();
    }
}
