package com.chua.common.support.net.frame;

import lombok.Data;

import java.util.Map;

/**
 * 限位框
 *
 * @author CH
 * @since 2023/09/13
 */
@Data
public class HttpFrame implements Frame {

    /**
     * uri
     */
    private String uri;

    /**
     * method
     */
    private String method;
    /**
     * 地址
     */
    private String address;


    /**
     * 消息头
     */
    private Map<String, String> header;

}
