package com.chua.digest.support.decode;


import com.chua.common.support.crypto.decode.KeyDecode;
import com.chua.common.support.utils.StringUtils;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;

import java.math.BigInteger;
import java.util.Base64;

/**
 * 解密
 *
 * @author CH
 */
public class Sm2Decode implements KeyDecode {
    @Override
    public byte[] decode(byte[] content, byte[] key) {
        return decode(StringUtils.utf8Str(content), StringUtils.utf8Str(key));
    }

    @Override
    public byte[] decode(String content, String key) {
        // 使用BC库加解密时密文以04开头，传入的密文前面没有04则补上
        if (!content.startsWith("04")) {
            content = "04" + content;
        }

        byte[] decode = Base64.getDecoder().decode(content);

        //获取一条SM2曲线参数
        X9ECParameters p256v1 = GMNamedCurves.getByName("sm2p256v1");
        //构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(p256v1.getCurve(), p256v1.getG(), p256v1.getN());

        BigInteger privateKeyD = new BigInteger(key, 16);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKeyD, domainParameters);

        SM2Engine sm2Engine = new SM2Engine(SM2Engine.Mode.C1C3C2);
        // 设置sm2为解密模式
        sm2Engine.init(false, privateKeyParameters);

        try {
            return sm2Engine.processBlock(decode, 0, decode.length);
        } catch (Exception e) {
            System.out.println("SM2解密时出现异常:" + e.getMessage());
        }
        return null;
    }
}
