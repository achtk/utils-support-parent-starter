package com.chua.common.support.extra.el.baseutil.encrypt;

import com.chua.common.support.extra.el.baseutil.StringUtil;
import com.chua.common.support.extra.el.baseutil.reflect.ReflectUtil;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * 基础类
 *
 * @author CH
 */
public class Md5Util {
    private static Charset charset = Charset.forName("UTF-8");

    public static byte[] md5(byte[] array) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] result = md.digest(array);
            return result;
        } catch (NoSuchAlgorithmException e) {
            ReflectUtil.throwException(e);
            return null;
        }
    }

    public static byte[] md5(ByteBuffer array) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(array);
            byte[] result = md.digest();
            return result;
        } catch (NoSuchAlgorithmException e) {
            ReflectUtil.throwException(e);
            return null;
        }
    }

    public static byte[] md5(byte[] array, int off, int length) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.digest(array, off, length);
            byte[] result = md.digest();
            return result;
        } catch (Exception e) {
            ReflectUtil.throwException(e);
            return null;
        }
    }

    public static byte[] md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] data = str.getBytes(charset);
            byte[] result = md.digest(data);
            return result;
        } catch (Exception e) {
            ReflectUtil.throwException(e);
            return null;
        }
    }

    public static String md5(File file) {
        return md5(file, 0, file.length());
    }

    /**
     * 检查文件的MD5值
     *
     * @param file
     * @param offset
     * @param length
     * @return
     */
    public static String md5(File file, long offset, long length) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] src;
            if (length > 1024 * 1024) {
                src = new byte[1024 * 1024];
            } else {
                src = new byte[(int) length];
            }
            randomAccessFile.seek(offset);
            if (randomAccessFile.length() < offset + length) {
                throw new IllegalArgumentException();
            }
            long index = 0;
            int read = 0;
            for (; index < length; index += read) {
                if (index + src.length < length) {
                    read = randomAccessFile.read(src);
                } else {
                    read = randomAccessFile.read(src, 0, (int) (length - index));
                }
                if (read != -1) {
                    md.update(src, 0, read);
                } else {
                    break;
                }
            }
            return StringUtil.toHexString(md.digest());
        } catch (Exception e) {
            ReflectUtil.throwException(e);
            return null;
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String md5Str(String str) {
        return StringUtil.toHexString(md5(str));
    }

    /**
     * 返回加密后的密码 格式为iterationCount:slat:hash。其中num为迭代次数
     *
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static String generateStorngPasswordHash(String password) {
        try {
            int iterations = 10;
            char[] chars = password.toCharArray();
            byte[] salt = getSalt();
            PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return iterations + ":" + StringUtil.toHexString(salt) + ":" + StringUtil.toHexString(hash);
        } catch (Exception e) {
            ReflectUtil.throwException(e);
            return null;
        }
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    /**
     * 验证密码的正确性，密码原文的格式为iterationCount:slat:hash。其中num为迭代次数
     *
     * @param originalPassword 待验证的密码
     * @param storedPassword   存储的加密的密码
     * @return
     */
    public static boolean validatePassword(String originalPassword, String storedPassword) {
        try {
            String[] parts = storedPassword.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = StringUtil.hexStringToBytes(parts[1]);
            byte[] hash = StringUtil.hexStringToBytes(parts[2]);
            PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] testHash = skf.generateSecret(spec).getEncoded();
            int diff = hash.length ^ testHash.length;
            for (int i = 0; i < hash.length && i < testHash.length; i++) {
                diff |= hash[i] ^ testHash[i];
            }
            return diff == 0;
        } catch (Exception e) {
            ReflectUtil.throwException(e);
            return false;
        }
    }
}
