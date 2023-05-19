package com.chua.digest.support.encode;

import com.chua.common.support.crypto.encode.Encode;
import com.chua.common.support.crypto.encode.KeyEncode;
import com.chua.common.support.utils.StringUtils;
import org.bouncycastle.crypto.digests.SM3Digest;

import java.util.Base64;

/**
 * sm3加密
 *
 * @author CH
 */
public class Sm3Encode implements Encode {
    @Override
    public byte[] encode(byte[] content) {
        SM3Digest sm3Digest = new SM3Digest();
        sm3Digest.update(content, 0, content.length);
        byte[] hash = new byte[sm3Digest.getDigestSize()];
        sm3Digest.doFinal(hash, 0);
        return hash;
    }
}
