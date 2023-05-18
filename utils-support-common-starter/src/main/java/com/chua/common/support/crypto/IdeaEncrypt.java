package com.chua.common.support.crypto;

import com.chua.common.support.crypto.decode.IdeaDecode;
import com.chua.common.support.crypto.decode.KeyDecode;
import com.chua.common.support.crypto.encode.IdeaEncode;
import com.chua.common.support.crypto.encode.KeyEncode;
import com.chua.common.support.annotations.Spi;

/**
 * 加解密
 *
 * @author CH
 */
@Spi("idea")
public class IdeaEncrypt extends AbstractEncrypt {

    private static final KeyDecode DECODE = new IdeaDecode();
    private static final KeyEncode ENCODE = new IdeaEncode();

    @Override
    public byte[] decode(byte[] content, byte[] key) {
        return DECODE.decode(content, key);
    }

    @Override
    public byte[] encode(byte[] content, byte[] key) {
        return ENCODE.encode(content, key);
    }
}
