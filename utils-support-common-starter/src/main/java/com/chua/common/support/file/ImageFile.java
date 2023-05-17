package com.chua.common.support.file;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 图片文件
 *
 * @author CH
 */
public interface ImageFile {
    /**
     * 转BufferedImage
     *
     * @return BufferedImage
     * @throws IOException ex
     */
    BufferedImage toBufferedImage() throws IOException;

    /**
     * 压缩图片
     *
     * @param quality      质量
     * @param outputStream 输出
     */
    void toCompress(final float quality, final OutputStream outputStream);

    /**
     * 可编辑文件
     *
     * @param type 类型
     * @return 可编辑文件
     * @throws IOException ex
     */
    ExifFile toExifFile(String type) throws IOException;

    /**
     * 可编辑文件
     *
     * @return 可编辑文件
     * @throws IOException ex
     */
    default ExifFile toExifFile() throws IOException {
        return toExifFile(null);
    }

    /**
     * 可编辑文件
     *
     * @param type 类型
     * @return 可编辑文件
     * @throws IOException ex
     */
    ImageEditFile toEditFile(String type) throws IOException;

    /**
     * 可编辑文件
     *
     * @return 可编辑文件
     * @throws IOException ex
     */
    default ImageEditFile toEditFile() throws IOException {
        return toEditFile(null);
    }
}
