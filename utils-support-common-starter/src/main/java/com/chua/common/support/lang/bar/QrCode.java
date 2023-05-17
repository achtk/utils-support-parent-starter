package com.chua.common.support.lang.bar;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * 二维码
 *
 * @author CH
 * @version 1.0.0
 */
public interface QrCode {
    /**
     * logo
     *
     * @param logo logo
     * @return this
     */
    QrCode logo(File logo);

    /**
     * 字体大小
     *
     * @param fontSize 字体大小
     * @return this
     */
    QrCode fontSize(int fontSize);

    /**
     * 宽度
     *
     * @param width 宽度
     * @return this
     */
    QrCode width(int width);

    /**
     * 高度
     *
     * @param height 高度
     * @return this
     */
    QrCode height(int height);

    /**
     * 文本
     *
     * @param content 文本
     * @return this
     */
    QrCode content(String content);

    /**
     * 图片类型
     *
     * @param type 图片类型
     * @return this
     */
    QrCode type(String type);

    /**
     * 居中
     *
     * @param align align
     * @return this
     */
    QrCode align(boolean align);

    /**
     * 条码类型
     *
     * @param barType 条码类型
     * @return this
     */
    QrCode barType(BarType barType);

    /**
     * 编码
     *
     * @param charset 编码
     * @return this
     */
    QrCode charset(Charset charset);

    /**
     * 输出到文件
     *
     * @param outPath 输出目录
     * @return 文件
     */
    File toFile(String outPath);

    /**
     * 输出到文件
     *
     * @param stream 输出
     */
    void toStream(OutputStream stream);

    /**
     * 条码类型
     */
    enum BarType {
        /**
         * 条码
         */
        BAR_CODE,
        /**
         * 二维码
         */
        BAR_CODE2
    }
}
