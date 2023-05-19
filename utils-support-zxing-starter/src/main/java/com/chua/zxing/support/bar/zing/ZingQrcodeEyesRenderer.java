package com.chua.zxing.support.bar.zing;

import com.chua.common.support.lang.bar.BarCodeBuilder;
import com.google.zxing.common.BitMatrix;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 彩色二维颜色
 *
 * @author CH
 */
public class ZingQrcodeEyesRenderer implements ZingRenderer {


    private static final int IMAGE_WIDTH = 50;
    private static final int IMAGE_HALF_WIDTH = IMAGE_WIDTH / 2;
    private static final int FRAME_WIDTH = 2;
    private static final int WHITE = 0xFFFFFFFF;
    private final BitMatrix bitMatrix;

    public ZingQrcodeEyesRenderer(BitMatrix bitMatrix) {

        this.bitMatrix = bitMatrix;
    }

    @Override
    public void render(BufferedImage image, BarCodeBuilder.QrCodeEyesFormat format, ZingQrcodeEyesPosition position, Color slave, Color border, Color point) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        for (int y = 0; y < bitMatrix.getHeight(); y++) {
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                pixels[y * width + x] = bitMatrix.get(x, y) ? 0 : 16777215;
            }
        }

        image.getRaster().setDataElements(0, 0, width, height, pixels);
    }
}
