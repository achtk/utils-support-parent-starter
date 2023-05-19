package com.chua.zxing.support.bar.zing;

import com.chua.common.support.lang.bar.BarCodeBuilder;
import com.google.zxing.common.BitMatrix;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * random
 *
 * @author CH
 * @since 2022/8/12 15:22
 */
public class ZingRandomQrcodeEyesRenderer implements ZingRenderer {


    private static final int IMAGE_WIDTH = 50;
    private static final int IMAGE_HALF_WIDTH = IMAGE_WIDTH / 2;
    private static final int FRAME_WIDTH = 2;
    private static final int WHITE = 0xFFFFFFFF;
    private final BitMatrix bitMatrix;

    public ZingRandomQrcodeEyesRenderer(BitMatrix bitMatrix) {

        this.bitMatrix = bitMatrix;
    }

    @Override
    public void render(BufferedImage image, BarCodeBuilder.QrCodeEyesFormat format, ZingQrcodeEyesPosition position, Color slave, Color border, Color point) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        for (int y = 0; y < bitMatrix.getHeight(); y++) {
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                int num1 = (int) (50 - (50.0 - 13.0) / height * (y + 1));
                int num2 = (int) (165 - (165 - 72.0) / height * (y + 1));
                int num3 = (int) (162 - (162 - 107.0) / height * (y + 1));
                Color color = new Color(num1, num2, num3);
                int colorInt = randomColor().getRGB() | color.getRGB();

                pixels[y * width + x] = bitMatrix.get(x, y) ? colorInt : 16777215;
            }
        }

        image.getRaster().setDataElements(0, 0, width, height, pixels);
    }

    /**
     * 随机
     *
     * @return Color
     */
    private Color randomColor() {
        Random mRandom = new Random();
        StringBuilder mBuilder = new StringBuilder();
        String haxString;
        for (int i = 0; i < 3; i++) {
            haxString = Integer.toHexString(mRandom.nextInt(0xFF));
            if (haxString.length() == 1) {
                haxString = String.format("0%s", haxString);
            }
            mBuilder.append(haxString);
        }
        return Color.decode("#" + mBuilder.toString());
    }
}