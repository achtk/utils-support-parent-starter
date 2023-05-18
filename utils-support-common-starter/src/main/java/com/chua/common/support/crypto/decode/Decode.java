package com.chua.common.support.crypto.decode;

import com.chua.common.support.crypto.Hex;
import com.chua.common.support.utils.StringUtils;
import lombok.SneakyThrows;

/**
 * 解密
 *
 * @author CH
 */
public interface Decode {

    /**
     * 解密
     *
     * @param content 内容
     * @return 加密结果
     */
    byte[] decode(byte[] content);

    /**
     * 解密
     *
     * @param content 内容
     * @return 加密结果
     */
    default byte[] decode(String content) {
        return decode(StringUtils.utf8Bytes(content));
    }

    /**
     * 解密
     *
     * @param content 内容
     * @return 加密结果
     */
    @SneakyThrows
    default String decodeHex(String content) {
        return StringUtils.utf8Str(decode(Hex.decodeHex(content)));
    }
}
