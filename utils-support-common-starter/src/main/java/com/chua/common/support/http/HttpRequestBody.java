package com.chua.common.support.http;

import lombok.Data;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * HttpRequestBody
 * @author CH
 */
@Data
public class HttpRequestBody  implements Serializable {

    public static abstract class BaseContentType {

        public static final String JSON = "application/json";

        public static final String XML = "text/xml";

        public static final String FORM = "application/x-www-form-urlencoded";

        public static final String MULTIPART = "multipart/form-data";
    }

    private byte[] body;

    private String contentType;

    private String encoding;

    public HttpRequestBody() {
    }

    public HttpRequestBody(byte[] body, String contentType, String encoding) {
        this.body = body;
        this.contentType = contentType;
        this.encoding = encoding;
    }


    public static HttpRequestBody json(String json, String encoding) {
        try {
            return new HttpRequestBody(json.getBytes(encoding), BaseContentType.JSON, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("illegal encoding " + encoding, e);
        }
    }

    public static HttpRequestBody xml(String xml, String encoding) {
        try {
            return new HttpRequestBody(xml.getBytes(encoding), BaseContentType.XML, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("illegal encoding " + encoding, e);
        }
    }

    public static HttpRequestBody custom(byte[] body, String contentType, String encoding) {
        return new HttpRequestBody(body, contentType, encoding);
    }

//    public static HttpRequestBody form(Map<String,Object> params, String encoding){
//        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(params.size());
//        for (Map.Entry<String, Object> entry : params.entrySet()) {
//            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
//        }
//        try {
//            return new HttpRequestBody(URLEncodedUtils.format(nameValuePairs, encoding).getBytes(encoding), ContentType.FORM, encoding);
//        } catch (UnsupportedEncodingException e) {
//            throw new IllegalArgumentException("illegal encoding " + encoding, e);
//        }
//    }
}