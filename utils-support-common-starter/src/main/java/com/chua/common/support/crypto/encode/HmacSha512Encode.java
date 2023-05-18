package com.chua.common.support.crypto.encode;

import com.chua.common.support.annotations.Spi;

/**
 * hmacSha512
 *
 * @author CH
 */
@Spi("hmacSha512")
public class HmacSha512Encode extends HmacMd5Encode {

    @Override
    public String getAlgorithm() {
        return "HmacSHA512";
    }

}
