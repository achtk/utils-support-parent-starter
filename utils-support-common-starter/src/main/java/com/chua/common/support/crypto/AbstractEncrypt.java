package com.chua.common.support.crypto;

/**
 * 加解密
 *
 * @author CH
 */
public abstract class AbstractEncrypt implements Encrypt {

    protected String accessKey;
    protected String secretKey;

    @Override
    public Encrypt accessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    @Override
    public Encrypt secretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }
}
