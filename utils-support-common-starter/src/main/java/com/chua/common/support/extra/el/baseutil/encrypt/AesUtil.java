package com.chua.common.support.extra.el.baseutil.encrypt;

import com.chua.common.support.extra.el.baseutil.exception.UnSupportException;
import com.chua.common.support.extra.el.baseutil.reflect.ReflectUtil;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * aes加解密工具类，注意，该类为非线程安全
 *
 * @author linbin
 */
public class AesUtil implements EnDecrpt
{
    private Cipher decryptCipher;
    private Cipher encrptCipher;

    public AesUtil(byte[] key)
    {
        if (key.length == 16)
        {
            try
            {
                // 加密
                SecretKey aeskey = new SecretKeySpec(key, "AES");
                encrptCipher = Cipher.getInstance("AES");
                encrptCipher.init(Cipher.ENCRYPT_MODE, aeskey);
                decryptCipher = Cipher.getInstance("AES");
                decryptCipher.init(Cipher.DECRYPT_MODE, aeskey);
            }
            catch (Exception e)
            {
                ReflectUtil.throwException(e);
            }
        }
        else
        {
            throw new UnSupportException("默认只支持16byte的密钥");
        }
    }

    @Override
    public void setPublicKey(byte[] publicKeyBytes)
    {
        throw new UnSupportException("AES为对称加密，无公钥");
    }

    @Override
    public void setPrivateKey(byte[] privateKeyBytes)
    {
        throw new UnSupportException("AES为对称加密，无私钥");
    }

    @Override
    public byte[] encrypt(byte[] src)
    {
        try
        {
            return encrptCipher.doFinal(src);
        }
        catch (Exception e)
        {
            ReflectUtil.throwException(e);
            return null;
        }
    }

    @Override
    public byte[] decrypt(byte[] src)
    {
        try
        {
            return decryptCipher.doFinal(src);
        }
        catch (Exception e)
        {
            ReflectUtil.throwException(e);
            return null;
        }
    }

    @Override
    public void setKey(byte[] key)
    {
    }

    @Override
    public byte[] sign(byte[] src)
    {
        throw new UnSupportException("aes无签名功能");
    }

    @Override
    public boolean check(byte[] src, byte[] sign)
    {
        throw new UnSupportException("aes无签名功能");
    }
}
