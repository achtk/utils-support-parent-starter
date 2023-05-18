package com.chua.common.support.crypto;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.crypto.decode.Decode;
import com.chua.common.support.crypto.decode.KeyDecode;
import com.chua.common.support.crypto.encode.Encode;
import com.chua.common.support.crypto.encode.KeyEncode;
import com.chua.common.support.crypto.utils.DigestUtils;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.Builder;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * MessageDigest
 *
 * @author CH
 */
@Builder
public class MessageDigest {
    private static final Class<DigestUtils> CLASS = DigestUtils.class;


    /**
     * 密钥
     */
    private String key;
    /**
     * 类型
     */
    private String type;

    /**
     * 加密
     *
     * @param str str
     * @return 加密
     */
    public byte[] encode(String str) {
        if (StringUtils.isNullOrEmpty(key)) {
            ServiceProvider<Encode> provider = ServiceProvider.of(Encode.class);
            Encode keyEncode = provider.getExtension(type);
            if (null != keyEncode) {
                return keyEncode.encode(str);
            }

            return Converter.convertIfNecessary(utilsDigest(str), byte[].class);
        }

        ServiceProvider<KeyEncode> provider = ServiceProvider.of(KeyEncode.class);
        KeyEncode keyEncode = provider.getExtension(type);
        if (null != keyEncode) {
            return keyEncode.encode(str, Optional.ofNullable(key).orElse(""));
        }

        return null;
    }

    /**
     * 加密
     *
     * @param str str
     * @return 加密
     */
    public String encodeHex(String str) {
        if (StringUtils.isNullOrEmpty(key)) {
            ServiceProvider<Encode> provider = ServiceProvider.of(Encode.class);
            Encode keyEncode = provider.getExtension(type);
            if (null != keyEncode) {
                return keyEncode.encodeHex(str);
            }

            return Converter.convertIfNecessary(utilsDigest(str), String.class);
        }

        ServiceProvider<KeyEncode> provider = ServiceProvider.of(KeyEncode.class);
        KeyEncode keyEncode = provider.getExtension(type);
        if (null != keyEncode) {
            return keyEncode.encodeHex(str, Optional.ofNullable(key).orElse(""));
        }

        return null;
    }
    /**
     * 解密
     *
     * @param str str
     * @return 解密
     */
    public byte[] decode(String str) {
        if (StringUtils.isNullOrEmpty(key)) {
            ServiceProvider<Decode> provider = ServiceProvider.of(Decode.class);
            Decode decode = provider.getExtension(type);
            if (null != decode) {
                return decode.decode(str);
            }

            return null;
        }

        ServiceProvider<KeyDecode> provider = ServiceProvider.of(KeyDecode.class);
        KeyDecode keyDecode = provider.getExtension(type);
        if (null != keyDecode) {
            return keyDecode.decode(str, Optional.ofNullable(key).orElse(""));
        }

        return null;
    }

    /**
     * 解密
     *
     * @param str str
     * @return 解密
     */
    public String decodeHex(String str) {
        if (StringUtils.isNullOrEmpty(key)) {
            ServiceProvider<Decode> provider = ServiceProvider.of(Decode.class);
            Decode decode = provider.getExtension(type);
            if (null != decode) {
                return decode.decodeHex(str);
            }

            return utilsDigest(str);
        }

        ServiceProvider<KeyDecode> provider = ServiceProvider.of(KeyDecode.class);
        KeyDecode keyDecode = provider.getExtension(type);
        if (null != keyDecode) {
            return keyDecode.decodeHex(str, Optional.ofNullable(key).orElse(""));
        }

        return null;
    }

    /**
     * 工具类
     *
     * @param str 数据
     * @return 加密数据
     */
    private String utilsDigest(String str) {
        Method method = null;
        try {
            method = CLASS.getDeclaredMethod(type, String.class);
        } catch (NoSuchMethodException ignored) {
        }

        if (null != method) {
            ClassUtils.setAccessible(method);
            return (String) ClassUtils.invokeMethod(method, null, str);
        }
        return null;
    }
}
