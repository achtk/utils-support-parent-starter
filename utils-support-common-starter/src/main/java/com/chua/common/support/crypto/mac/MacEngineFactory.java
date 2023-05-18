package com.chua.common.support.crypto.mac;


import com.chua.common.support.spi.ServiceProvider;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

/**
 * {@link MacEngine} 实现工厂类
 *
 * @author Looly
 * @since 4.5.13
 */
public class MacEngineFactory {

    /**
     * 根据给定算法和密钥生成对应的{@link MacEngine}
     *
     * @param algorithm 算法，见{@link HmacAlgorithm}
     * @param key       密钥
     * @return {@link MacEngine}
     */
    public static MacEngine createEngine(String algorithm, Key key) {
        return createEngine(algorithm, key, null);
    }

    /**
     * 根据给定算法和密钥生成对应的{@link MacEngine}
     *
     * @param algorithm 算法，见{@link HmacAlgorithm}
     * @param key       密钥
     * @param spec      spec
     * @return {@link MacEngine}
     * @since 5.7.12
     */
    public static MacEngine createEngine(String algorithm, Key key, AlgorithmParameterSpec spec) {
        if (algorithm.equalsIgnoreCase(HmacAlgorithm.HMAC_SM3.getValue())) {
            // HmacSM3算法是BC库实现的，忽略加盐
            return ServiceProvider.of(MacEngine.class).getExtension(HmacAlgorithm.HMAC_SM3.getValue());
        }
        return new DefaultHMacEngine(algorithm, key, spec);
    }
}
