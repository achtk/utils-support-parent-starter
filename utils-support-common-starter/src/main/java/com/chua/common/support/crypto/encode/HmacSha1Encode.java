package com.chua.common.support.crypto.encode;

import com.chua.common.support.annotations.Spi;

/**
 * hmacSha1
 *
 * @author CH
 */
@Spi("hmacSha1")
public class HmacSha1Encode extends HmacMd5Encode {

    @Override
    public String getAlgorithm() {
        return "HmacSHA1";
    }
}
