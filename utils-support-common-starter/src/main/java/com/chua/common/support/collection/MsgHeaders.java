package com.chua.common.support.collection;

import com.chua.common.support.http.HttpHeader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 消息头
 * @author CH
 */
@Data
@Accessors(chain = true)
public class MsgHeaders {

    private List<MsgHeader> headers  = new LinkedList<>();

    public MsgHeaders add(MsgHeader header) {
        headers.add(header);
        return this;
    }

    public MsgHeaders add(String name, String value) {
        headers.add(new MsgHeader(name, value));
        return this;
    }

    public HttpHeader toHttpHeader() {
        HttpHeader header = new HttpHeader();
        for (MsgHeader msgHeader : headers) {
            header.add(msgHeader.getName(), msgHeader.getValue());
        }
        return header;
    }


    @Data
    @AllArgsConstructor
    public static class MsgHeader {

        private String name;
        private String value;
    }
}
