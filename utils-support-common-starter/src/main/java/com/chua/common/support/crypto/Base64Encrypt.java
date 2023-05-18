package com.chua.common.support.crypto;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.crypto.decode.Base64Decode;
import com.chua.common.support.crypto.encode.Base64Encode;

/**
 * base64
 *
 * @author CH
 */
@Spi("base64")
public class Base64Encrypt extends AbstractEncrypt {

    private static final Base64Decode DECODE = new Base64Decode();
    private static final Base64Encode ENCODE = new Base64Encode();

    @Override
    public byte[] decode(byte[] content, byte[] key) {
        return DECODE.decode(content);
    }

    @Override
    public byte[] encode(byte[] content, byte[] key) {
        return ENCODE.encode(content);
    }
}
