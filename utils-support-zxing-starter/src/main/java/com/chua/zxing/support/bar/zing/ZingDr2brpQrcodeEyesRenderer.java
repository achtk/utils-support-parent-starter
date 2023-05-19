package com.chua.zxing.support.bar.zing;


import com.chua.common.support.lang.bar.BarCodeBuilder;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author chenhua
 */
public class ZingDr2brpQrcodeEyesRenderer implements ZingRenderer {

    private static final int ARC = 15;

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
            int w1 = rect[2], h1 = rect[3];
            int x1 = rect[0], y1 = rect[1];
            if (directions[0].equals(direction)) {
                w1 += 1;
                h1 += 1;
            } else if (directions[1].equals(direction)) {
                x1 -= 1;
                h1 += 1;
                w1 += 1;
            } else if (directions[2].equals(direction)) {
                y1 -= 2;
                h1 += 2;
                w1 += 1;
            }
            graphics.clearRect(x1, y1, w1, h1);
//
            // draw code-eyes border
            int arc = ARC / 3;
            int x = rect[0] + borderSize / 2, y = rect[1] + borderSize / 2;
            int w = rect[2] - borderSize - arc, h = rect[3] - borderSize - arc;
            if (directions[1].equals(direction)) {
                x = x + arc;
            } else if (directions[2].equals(direction)) {
                y = y + arc;
            }
            Shape shape = new RoundRectangle2D.Double(x, y, w, h,
                    arc, arc);
            graphics.setColor(slave);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.fill(shape);
            graphics.setStroke(new BasicStroke(borderSize));
            graphics.setColor(border);
            graphics.draw(shape);

//             draw right-angle
            Graphics graphicsX = graphics.create();
            if (directions[0].equals(direction)) {
                graphicsX.drawLine(x, y, x, y + arc);
                graphicsX.drawLine(x, y, x + arc, y);
                x = position.getLeftEndX() - borderSize / 2 - arc;
                y = position.getTopEndY() - borderSize / 2 - arc;
                graphicsX.drawLine(x, y, x, y - arc);
                graphicsX.drawLine(x, y, x - arc, y);
            } else if (directions[1].equals(direction)) {
                x = position.getRightStartX() + borderSize / 2 + arc;
                y = position.getTopEndY() - borderSize / 2 - arc;
                graphicsX.drawLine(x, y, x, y - ARC);
                graphicsX.drawLine(x, y, x + ARC, y);
                x = position.getRightEndX() - borderSize / 2;
                y = position.getTopStartY() + borderSize / 2;
                graphicsX.drawLine(x, y, x, y + arc);
                graphicsX.drawLine(x, y, x - arc, y);
            } else {
                x = position.getLeftStartX() + borderSize / 2;
                y = position.getBottomEndY() - borderSize / 2 - 1;
                graphicsX.drawLine(x, y, x, y - arc);
                graphicsX.drawLine(x, y, x + arc, y);
                x = position.getLeftEndX() - borderSize / 2 - arc;
                y = position.getBottomStartY() + borderSize / 2 + arc;
                graphicsX.drawLine(x, y, x, y + arc);
                graphicsX.drawLine(x, y, x - arc, y);
            }

//                         draw code-eyes point
            rect = (int[]) ZingUtils.invokeMethod(position.focusPoint(width, height), direction + "Point");
            int x2 = rect[0], y2 = rect[1];
            if (directions[0].equals(direction)) {
                x2 -= 2;
                y2 -= 2;
            } else if (directions[1].equals(direction)) {
                x2 += 2;
                y2 -= 2;
            } else if (directions[2].equals(direction)) {
                x2 -= 2;
                y2 += 2;
            }
            shape = getPointShape(x2, y2, rect[2], rect[3], 5, 5);
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
    public Shape getPointShape(double x, double y, double w, double h, double arcw, double arch) {
        return new RoundRectangle2D.Double(x, y, w, h, arcw, arch);
    }

    @Override
    public void checkFormat(BarCodeBuilder.QrCodeEyesFormat format) {
        if (!"DR2_BORDER_R_POINT".equalsIgnoreCase(format.name())) {
            throw new IllegalArgumentException("Can only render DR2_BORDER_R_POINT, but got " + format);
        }
    }

}
