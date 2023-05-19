package com.chua.zxing.support.bar.zing;


import com.chua.common.support.lang.bar.BarCodeBuilder;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * zing render
 *
 * @author CH
 * @since 2022-05-16
 */
public interface ZingRenderer {

    /**
     * Render code-eyes by format.
     *
     * @param image    image
     * @param format   format
     * @param position position
     * @param slave    slave
     * @param border   border
     * @param point    point
     */
    void render(BufferedImage image, BarCodeBuilder.QrCodeEyesFormat format, ZingQrcodeEyesPosition position, Color slave, Color border,
                Color point);

    /**
     * Set code-eyes render shape.
     *
     * @param x    x
     * @param y    y
     * @param w    w
     * @param h    h
     * @param arcw aw
     * @param arch ah
     * @return Shape
     */
    default Shape getPointShape(double x, double y, double w, double h, double arcw, double arch) {
        return new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Check the format match target renderer.
     *
     * @param format format
     */
    default void checkFormat(BarCodeBuilder.QrCodeEyesFormat format) {
        return;
    }
}
