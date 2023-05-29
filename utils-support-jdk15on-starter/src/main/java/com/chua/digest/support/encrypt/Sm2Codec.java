package com.chua.digest.support.encrypt;

import com.chua.common.support.crypto.AbstractCodec;
import com.chua.common.support.crypto.decode.KeyDecode;
import com.chua.common.support.crypto.encode.KeyEncode;
import com.chua.digest.support.decode.Sm2Decode;
import com.chua.digest.support.encode.Sm2Encode;

/**
 * 加解密
 *
 * @author CH
 */
public class Sm2Codec extends AbstractCodec {

    private static final KeyDecode DECODE = new Sm2Decode();
    private static final KeyEncode ENCODE = new Sm2Encode();

    @Override
    public byte[] decode(byte[] content, byte[] key) {
        return DECODE.decode(content, key);
    }

    @Override
    public byte[] encode(byte[] content, byte[] key) {
        return ENCODE.encode(content, key);
    }
}
