package com.chua.digest.support.encrypt;


import com.chua.common.support.crypto.AbstractEncrypt;
import com.chua.common.support.crypto.decode.KeyDecode;
import com.chua.common.support.crypto.encode.KeyEncode;
import com.chua.digest.support.decode.Sm4Decode;
import com.chua.digest.support.encode.Sm4Encode;

/**
 * 加解密
 *
 * @author CH
 */
public class Sm4Encrypt extends AbstractEncrypt {

    private static final KeyDecode DECODE = new Sm4Decode();
    private static final KeyEncode ENCODE = new Sm4Encode();

    @Override
    public byte[] decode(byte[] content, byte[] key) {
        return DECODE.decode(content, key);
    }

    @Override
    public byte[] encode(byte[] content, byte[] key) {
        return ENCODE.encode(content, key);
    }
}
