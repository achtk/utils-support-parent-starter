package com.chua.common.support.crypto.mac;

/**
 * @author CH
 */
public enum HmacAlgorithm {
    /**
     * md5
     */
    HMAC_MD5("HmacMD5"),
    /**
     * sha1
     */
    HMAC_SHA1("HmacSHA1"),
    /**
     * sha256
     */
    HMAC_SHA256("HmacSHA256"),
    /**
     * sha384
     */
    HMAC_SHA384("HmacSHA384"),
    /**
     * sha512
     */
    HMAC_SHA512("HmacSHA512"),
    /**
     * HmacSM3算法实现，需要BouncyCastle库支持
     */
    HMAC_SM3("HmacSM3");

    private final String value;

    HmacAlgorithm(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
