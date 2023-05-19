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
public class ZingGradientV1QrcodeEyesRenderer implements ZingRenderer {


    private static final int IMAGE_WIDTH = 50;
    private static final int IMAGE_HALF_WIDTH = IMAGE_WIDTH / 2;
    private static final int FRAME_WIDTH = 2;
    private static final int WHITE = 0xFFFFFFFF;
    private final BitMatrix bitMatrix;

    public ZingGradientV1QrcodeEyesRenderer(BitMatrix bitMatrix) {

        this.bitMatrix = bitMatrix;
    }

    @Override
    public void render(BufferedImage image, BarCodeBuilder.QrCodeEyesFormat format, ZingQrcodeEyesPosition position, Color slave, Color border, Color point) {
        int halfW = bitMatrix.getWidth() / 2;
        int halfH = bitMatrix.getHeight() / 2;
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        for (int y = 0; y < bitMatrix.getHeight(); y++) {
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                int num1 = (int) (50 - (50.0 - 13.0) / height * (y + 1));
                int num2 = (int) (165 - (165 - 72.0) / height * (y + 1));
                int num3 = (int) (162 - (162 - 107.0) / height * (y + 1));
                Color color = new Color(num1, num2, num3);
                int colorInt = color.getRGB();

                pixels[y * width + x] = bitMatrix.get(x, y) ? colorInt : 16777215;
            }
        }

        image.getRaster().setDataElements(0, 0, width, height, pixels);
    }
}
