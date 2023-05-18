package com.chua.common.support.crypto.encode;


import com.chua.common.support.annotations.Spi;

/**
 * hmacSha384
 *
 * @author CH
 */
@Spi("hmacSha384")
public class HmacSha384Encode extends HmacMd5Encode {

    @Override
    public String getAlgorithm() {
        return "HmacSHA384";
    }

}
