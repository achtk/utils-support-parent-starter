package com.chua.common.support.crypto;

/**
 * 空
 *
 * @author CH
 */
public class NoneCodec implements Codec {
    @Override
    public Codec accessKey(String accessKey) {
        return this;
    }

    @Override
    public Codec secretKey(String secretKey) {
        return this;
    }

    @Override
    public byte[] decode(byte[] content, byte[] key) {
        return content;
    }

    @Override
    public byte[] encode(byte[] content, byte[] key) {
        return content;
    }
}
