package com.chua.ffmpeg.support.utils;

import org.bytedeco.opencv.opencv_core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * mat
 *
 * @author CH
 */
public class MatUtils {

    /**
     * 将mat转BufferedImage
     *
     * @param matrix matrix
     */
    public static BufferedImage matToBuffer(Mat matrix) {
        int cols = matrix.cols();
        int rows = matrix.rows();
        int elemSize = (int) matrix.elemSize();
        byte[] data = new byte[cols * rows * elemSize];

        matrix.data().get(data);

        int type = 0;
        switch (matrix.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;
            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;
                byte b;
                for (int i = 0; i < data.length; i = i + 3) {
                    b = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = b;
                }
                break;
            default:
                return null;
        }
        BufferedImage image = new BufferedImage(cols, rows, type);
        image.getRaster().setDataElements(0, 0, cols, rows, data);
        return image;
    }

    /**
     * 将BufferedImage转mat
     *
     * @param original BufferedImage
     * @param matType  类型
     */
    public static Mat bufferToMat(BufferedImage original, int matType) {
        original = convert(original, BufferedImage.TYPE_3BYTE_BGR);
        Mat mat = new Mat(original.getHeight(), original.getWidth(), matType);
        mat.data().put(((DataBufferByte) original.getRaster().getDataBuffer()).getData());
        return mat;
    }

    /**
     * 将BufferedImage类型转换
     *
     * @param bufferedImage bufferedImage
     * @param bufImgType    ImgType
     */
    public static BufferedImage convert(BufferedImage bufferedImage, int bufImgType) {
        BufferedImage img = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufImgType);
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(bufferedImage, 0, 0, null);
        g2d.dispose();
        return img;
    }
}
