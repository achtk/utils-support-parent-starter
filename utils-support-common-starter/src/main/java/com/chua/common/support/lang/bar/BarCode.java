package com.chua.common.support.lang.bar;

import java.io.File;
import java.io.OutputStream;

/**
 * 条码
 *
 * @author CH
 */
public interface BarCode {

    /**
     * 右上角文字
     *
     * @param word 文字
     * @return this
     */
    BarCode rightUpWords(String word);

    /**
     * 条形码右下角文字
     *
     * @param word 文字
     * @return this
     */
    BarCode rightDownWords(String word);

    /**
     * 条形码左下角文字
     *
     * @param word 文字
     * @return this
     */
    BarCode leftDownWords(String word);

    /**
     * 容错
     *
     * @param correction 容错
     * @return this
     */
    BarCode errorCorrection(String correction);

    /**
     * 边框
     *
     * @param margin 边框
     * @return this
     */
    BarCode margin(int margin);

    /**
     * 宽度
     *
     * @param width 宽度
     * @return this
     */
    BarCode width(int width);

    /**
     * 高度
     *
     * @param height 高度
     * @return this
     */
    BarCode height(int height);

    /**
     * 打开日期
     *
     * @return this
     */
    BarCode openDate();

    /**
     * 实现
     *
     * @param barcode 实现
     * @return this
     */
    BarCode writer(String barcode);

    /**
     * 输出到文件
     *
     * @param va      va
     * @param outPath 输出目录
     * @return 文件
     */
    File toFile(String va, String outPath);

    /**
     * 输出到文件
     *
     * @param va
     * @param stream 输出
     */
    void toStream(String va, OutputStream stream);

}
