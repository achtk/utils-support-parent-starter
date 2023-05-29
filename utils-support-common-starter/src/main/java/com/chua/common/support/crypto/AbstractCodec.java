package com.chua.common.support.crypto;

/**
 * 加解密
 *
 * @author CH
 */
public abstract class AbstractCodec implements Codec {

    protected String accessKey;
    protected String secretKey;

    @Override
    public Codec accessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    @Override
    public Codec secretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }
}
