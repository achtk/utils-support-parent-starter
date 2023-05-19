package com.chua.example.crypto;

import com.chua.common.support.crypto.mac.HmacAlgorithm;
import com.chua.common.support.crypto.utils.DigestUtils;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author CH
 */
public class DigestUtilsExample {

    public static void main(String[] args) {
        System.out.println(DigestUtils.hmac(HmacAlgorithm.HMAC_SHA1, "1".getBytes(UTF_8))
                .digestBase64("1", true));
        System.out.println(DigestUtils.sha512Hex("1"));
    }
}
