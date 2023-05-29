package com.chua.common.support.crypto;

import com.chua.common.support.crypto.decode.DesDecode;
import com.chua.common.support.crypto.decode.KeyDecode;
import com.chua.common.support.crypto.encode.DesEncode;
import com.chua.common.support.crypto.encode.KeyEncode;
import com.chua.common.support.annotations.Spi;

/**
 * des
 *
 * @author CH
 */
@Spi("des")
public class DesCodec extends AbstractCodec {

    private static final KeyDecode DECODE = new DesDecode();
    private static final KeyEncode ENCODE = new DesEncode();

    @Override
    public byte[] decode(byte[] content, byte[] key) {
        return DECODE.decode(content, key);
    }

    @Override
    public byte[] encode(byte[] content, byte[] key) {
        return ENCODE.encode(content, key);
    }
}
