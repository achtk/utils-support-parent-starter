package com.chua.common.support.crypto.encode;

import com.chua.common.support.annotations.Spi;

/**
 * hmacSha256
 *
 * @author CH
 */
@Spi("hmacSha256")
public class HmacSha256Encode extends HmacMd5Encode {

    @Override
    public String getAlgorithm() {
        return "HmacSHA256";
    }

}
