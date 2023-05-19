package com.chua.digest.support.encode;

import com.chua.common.support.crypto.encode.Encode;
import com.chua.common.support.crypto.encode.KeyEncode;
import com.chua.common.support.crypto.utils.DigestUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.util.Base64;

/**
 * sm4加密
 *
 * @author CH
 */
public class Sm4Encode implements KeyEncode {

    public static final String KEY_ALGORITHM = "SM4";
    /**
     * 加密算法/分组加密模式/分组填充方式
     * PKCS5Padding-以8个字节为一组进行分组加密
     * 定义分组加密模式使用：PKCS5Padding
     */
    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";

    @Override
    @SneakyThrows
    public byte[] encode(byte[] content, byte[] key) {
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME_ECB_PADDING, BouncyCastleProvider.PROVIDER_NAME);
        // 初始化为加密模式的密码器
        cipher.init(Cipher.ENCRYPT_MODE, DigestUtils.getSecretKey(KEY_ALGORITHM, key));
        return cipher.doFinal(content);
    }
}
