package com.chua.zxing.support.bar.zing;


import com.chua.common.support.lang.bar.BarCodeBuilder;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author chenhua
 */
public class ZingRbrpQrcodeEyesRenderer implements ZingRenderer {

    @Override
    public void render(BufferedImage image, BarCodeBuilder.QrCodeEyesFormat format, ZingQrcodeEyesPosition position, Color slave, Color border,
                       Color point) {

        checkFormat(format);

        int width = image.getWidth(), height = image.getHeight();
        int borderSize = position.getBorderSize(width);

        final String[] directions = {"topLeft", "topRight", "bottomLeft"};

        Graphics2D graphics = image.createGraphics();
        graphics.setBackground(slave);
        for (String direction : directions) {

            // clear area by slave color
            int[] rect = (int[]) ZingUtils.invokeMethod(position, direction + "Rect");
            graphics.clearRect(rect[0], rect[1], rect[2], rect[3]);

            // draw code-eyes border
            Shape shape = new Rectangle2D.Float(rect[0] + borderSize / 2, rect[1] + borderSize / 2,
                    rect[2] - borderSize, rect[3] - borderSize);
            graphics.setColor(slave);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.fill(shape);
            graphics.setStroke(new BasicStroke(borderSize));
            graphics.setColor(border);
            graphics.draw(shape);

            // draw code-eyes point
            rect = (int[]) ZingUtils.invokeMethod(position.focusPoint(width, height), direction + "Point");
            shape = getPointShape(rect[0], rect[1], rect[2], rect[3], 5, 5);
            graphics.setColor(point);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.fill(shape);

            // reset border setting
            graphics.setStroke(new BasicStroke(0));
            graphics.setColor(point);
            graphics.draw(shape);
        }

        graphics.dispose();
        image.flush();
    }

    @Override
    public void checkFormat(BarCodeBuilder.QrCodeEyesFormat format) {
        if ("R_BORDER_R_POINT".equalsIgnoreCase(format.name())) {
            throw new IllegalArgumentException("Can only render R_BORDER_R_POINT, but got " + format);
        }
    }

    @Override
    public Shape getPointShape(double x, double y, double w, double h, double arcw, double arch) {
        return new RoundRectangle2D.Double(x, y, w, h, arcw, arch);
    }

}
