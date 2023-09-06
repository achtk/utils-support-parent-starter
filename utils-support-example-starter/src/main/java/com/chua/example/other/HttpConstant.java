package com.chua.example.other;

/**
 * constant
 *
 * @author CH
 */
public interface HttpConstant {
    /**
     * HTTP
     */
    public static final String HTTP = "http://";
    /**
     * HTTPS
     */
    public static final String HTTPS = "https://";
    /**
     * GET
     */
    public static final String HTTP_METHOD_GET = "GET";
    /**
     * POST
     */
    public static final String HTTP_METHOD_POST = "POST";
    /**
     * DELETE
     */
    public static final String HTTP_METHOD_DELETE = "DELETE";
    /**
     * STOMP
     */
    public static final String HTTP_METHOD_STOMP = "STOMP";
    /**
     * WEB_SOCKET
     */
    public static final String HTTP_METHOD_WEB_SOCKET = "WEB_SOCKET";
    /**
     * HEADER
     */
    public static final String HTTP_METHOD_HEADER = "HEADER";
    /**
     * OPTIONS
     */
    public static final String HTTP_METHOD_OPTIONS = "OPTIONS";
    /**
     * PUT
     */
    public static final String HTTP_METHOD_PUT = "PUT";

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String FORM_DATA = "multipart/form-data";
    public static final String FORM_DATA_UTF_8 = "multipart/form-data;charset=UTF-8";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_JSON_UTF_8 = "application/json;charset=UTF-8";
    public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_X_WWW_FORM_URLENCODED_UTF_8 = "application/x-www-form-urlencoded;charset=UTF-8";

    /**
     * Accept-Charset
     */
    public static final String HTTP_HEADER_ACCEPT_CHARSET = "Accept-Charset";
    /**
     * Content-Type
     */
    public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    /**
     * Content-Length
     */
    public static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";

    /**
     * accept
     */
    public static final String ACCEPT = "accept";
    /**
     * user-agent
     */
    public static final String USER_AGENT = "user-agent";
    /**
     * Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)
     */
    public static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";
    /**
     * connection
     */
    public static final String CONNECTION = "connection";
    /**
     * Keep-Alive
     */
    public static final String KEEP_ALIVE = "Keep-Alive";
    /**
     * *
     */
    public static final String ANY = "*/*";
    public static final String SYMBOL_MINUS = "-";
    public static final String SYMBOL_LEFT_TRIANGLE_BRACKET = "(";
    String TEXT_XML = "text/xml";

    /**
     * 表单类型Content-Type
     */
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded;charset=UTF-8";
    /**
     * 流类型Content-Type
     */
    public static final String CONTENT_TYPE_STREAM = "application/octet-stream;charset=UTF-8";
    /**
     * JSON类型Content-Type
     */
    public static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
    /**
     * XML类型Content-Type
     */
    public static final String CONTENT_TYPE_XML = "application/xml;charset=UTF-8";
    /**
     * 文本类型Content-Type
     */
    public static final String CONTENT_TYPE_TEXT = "application/text;charset=UTF-8";

    /**
     * 请求Header Accept
     */
    public static final String HTTP_HEADER_ACCEPT = "Accept";
    /**
     * 请求Body内容MD5 Header
     */
    public static final String HTTP_HEADER_CONTENT_MD5 = "Content-MD5";
    /**
     * 请求Header UserAgent
     */
    public static final String HTTP_HEADER_USER_AGENT = "User-Agent";
    /**
     * 请求Header Date
     */
    public static final String HTTP_HEADER_DATE = "Date";

    /**
     * 签名Header
     */
    public static final String X_CA_SIGNATURE = "x-ca-signature";
    /**
     * 所有参与签名的Header
     */
    public static final String X_CA_SIGNATURE_HEADERS = "x-ca-signature-headers";
    /**
     * 请求时间戳
     */
    public static final String X_CA_TIMESTAMP = "x-ca-timestamp";
    /**
     * 请求放重放Nonce,15分钟内保持唯一,建议使用UUID
     */
    public static final String X_CA_NONCE = "x-ca-nonce";
    /**
     * APP KEY
     */
    public static final String X_CA_KEY = "x-ca-key";
}
