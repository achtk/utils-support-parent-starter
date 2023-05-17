package com.chua.image.support.filter;

import com.chua.image.support.composite.SubtractComposite;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * do gf
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImageDogFilter extends AbstractImageFilter {

    private float radius1 = 1;
    private float radius2 = 2;
    private boolean normalize = true;
    private boolean invert;


    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage image1 = new ImageBoxBlurFilter(radius1, radius1, 3).filter(src, null);
        BufferedImage image2 = new ImageBoxBlurFilter(radius2, radius2, 3).filter(src, null);
        Graphics2D g2d = image2.createGraphics();
        g2d.setComposite(new SubtractComposite(1.0f));
        g2d.drawImage(image1, 0, 0, null);
        g2d.dispose();
        if (normalize && !Float.valueOf(radius1).equals(radius2)) {
            int[] pixels = null;
            int max = 0;
            for (int y = 0; y < height; y++) {
                pixels = getRgb(image2, 0, y, width, 1, pixels);
                for (int x = 0; x < width; x++) {
                    int rgb = pixels[x];
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    if (r > max) {
                        max = r;
                    }
                    if (g > max) {
                        max = g;
                    }
                    if (b > max) {
                        max = b;
                    }
                }
            }

            for (int y = 0; y < height; y++) {
                pixels = getRgb(image2, 0, y, width, 1, pixels);
                for (int x = 0; x < width; x++) {
                    int rgb = pixels[x];
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    r = r * 255 / max;
                    g = g * 255 / max;
                    b = b * 255 / max;
                    pixels[x] = (rgb & 0xff000000) | (r << 16) | (g << 8) | b;
                }
                setRgb(image2, 0, y, width, 1, pixels);
            }

        }

        if (invert) {
            image2 = new ImageInvertFilter().filter(image2, image2);
        }

        return image2;
    }
}
