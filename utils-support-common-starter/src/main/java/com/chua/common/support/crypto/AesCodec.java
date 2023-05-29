package com.chua.common.support.crypto;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.crypto.decode.AesDecode;
import com.chua.common.support.crypto.encode.AesEncode;

/**
 * aes
 *
 * @author CH
 */
@Spi("aes")
public class AesCodec extends AbstractCodec {

    private static final AesDecode DECODE = new AesDecode();
    private static final AesEncode ENCODE = new AesEncode();

    @Override
    public byte[] decode(byte[] content, byte[] key) {
        return DECODE.decode(content, key);
    }

    @Override
    public byte[] encode(byte[] content, byte[] key) {
        return ENCODE.encode(content, key);
    }
}
