/*
 * Check
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz.check;

import com.chua.common.support.file.xz.UnsupportedOptionsException;
import com.chua.common.support.file.xz.Xz;

/**
 * @author Administrator
 */
public abstract class Check {
    int size;
    String name;

    /**
     * 更新
     *
     * @param buf 数据
     * @param off 位置
     * @param len 长度
     */
    public abstract void update(byte[] buf, int off, int len);

    /**
     * 完成
     *
     * @return 完成
     */
    public abstract byte[] finish();

    /**
     * 更新
     *
     * @param buf 数据
     */
    public void update(byte[] buf) {
        update(buf, 0, buf.length);
    }

    /**
     * 获取数量
     *
     * @return 数量
     */
    public int getSize() {
        return size;
    }

    /**
     * 获取数量
     *
     * @return 数量
     */
    public String getName() {
        return name;
    }

    /**
     * 实例化
     *
     * @param checkType 检测类型
     * @return 实例
     * @throws UnsupportedOptionsException ex
     */
    public static Check getInstance(int checkType)
            throws UnsupportedOptionsException {
        switch (checkType) {
            case Xz.CHECK_NONE:
                return new None();

            case Xz.CHECK_CRC32:
                return new CRC32();

            case Xz.CHECK_CRC64:
                return new CRC64();

            case Xz.CHECK_SHA256:
                try {
                    return new SHA256();
                } catch (java.security.NoSuchAlgorithmException ignored) {
                }
                break;
            default:
                throw new UnsupportedOptionsException(
                        "Unsupported Check ID " + checkType);
        }

        throw new UnsupportedOptionsException(
                "Unsupported Check ID " + checkType);
    }
}
