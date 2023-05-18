package com.chua.common.support.crypto.encode;

import com.chua.common.support.annotations.Spi;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * hmacMd5
 *
 * @author CH
 */
@Spi("hmacMd5")
public class HmacMd5Encode implements KeyEncode {

    @Override
    public byte[] encode(byte[] content, byte[] key) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, getAlgorithm());
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            // 通过Base64转码返回
            return mac.doFinal(content);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 获取算法
     *
     * @return 算法
     */
    public String getAlgorithm() {
        return "HmacMD5";
    }

}
