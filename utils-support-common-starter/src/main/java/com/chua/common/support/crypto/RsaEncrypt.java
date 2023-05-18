package com.chua.common.support.crypto;

import com.chua.common.support.crypto.decode.RsaDecode;
import com.chua.common.support.crypto.encode.RsaEncode;
import com.chua.common.support.annotations.Spi;

/**
 * rsa
 *
 * @author CH
 */
@Spi("rsa")
public class RsaEncrypt extends AbstractEncrypt {

    private static final RsaDecode DECODE = new RsaDecode();
    private static final RsaEncode ENCODE = new RsaEncode();

    @Override
    public byte[] decode(byte[] content, byte[] key) {
        return DECODE.decode(content, key);
    }

    @Override
    public byte[] encode(byte[] content, byte[] key) {
        return ENCODE.encode(content, key);
    }
}
