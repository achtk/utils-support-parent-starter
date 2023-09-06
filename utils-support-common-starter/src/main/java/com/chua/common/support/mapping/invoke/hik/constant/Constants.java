package com.chua.common.support.mapping.invoke.hik.constant;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 通用常量
 */
public class Constants {
    /**
     * 签名算法HmacSha256
     */
    public static final String HMAC_SHA256 = "HmacSHA256";
    /**
     * 编码UTF-8
     */
    public static final String ENCODING = "UTF-8";
    /**
     * UserAgent
     */
    public static final String USER_AGENT = "demo/aliyun/java";
    /**
     * 换行符
     */
    public static final String LF = SYMBOL_N;
    /**
     * 串联符
     */
    public static final String SPE1 = SYMBOL_COMMA;
    /**
     * 示意符
     */
    public static final String SPE2 = SYMBOL_COLON;
    /**
     * 连接符
     */
    public static final String SPE3 = SYMBOL_AND;
    /**
     * 赋值符
     */
    public static final String SPE4 = SYMBOL_EQUALS;
    /**
     * 问号符
     */
    public static final String SPE5 = "?";
    /**
     * 默认请求超时时间,单位毫秒
     */
    public static int DEFAULT_TIMEOUT = 1000;
    /**
     * 参与签名的系统Header前缀,只有指定前缀的Header才会参与到签名中
     */
    public static final String CA_HEADER_TO_SIGN_PREFIX_SYSTEM = "x-ca-";

    public static final Double JDK_VERSION = 1.7;
}
