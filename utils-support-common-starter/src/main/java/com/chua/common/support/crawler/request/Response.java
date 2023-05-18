package com.chua.common.support.crawler.request;

import com.chua.common.support.crawler.node.Parser;
import lombok.Data;

/**
 * 响应
 *
 * @author Administrator
 */
@Data
public class Response {
    private final String baseUri;
    private final Parser parser;
    private final Object value;

    public Response(String baseUri, Parser parser, Object value) {
        this.baseUri = baseUri;
        this.parser = parser;
        this.value = value;
    }
}
