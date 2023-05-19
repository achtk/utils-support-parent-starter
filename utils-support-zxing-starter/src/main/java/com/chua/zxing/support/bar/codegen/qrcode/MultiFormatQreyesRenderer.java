package com.chua.zxing.support.bar.codegen.qrcode;

import com.chua.zxing.support.bar.codegen.qrcode.renderer.*;

import java.awt.*;
import java.awt.image.BufferedImage;


public final class MultiFormatQreyesRenderer implements QreyesRenderer {

    @Override
    public void render(BufferedImage image, QreyesFormat format, QreyesPosition position, Color slave, Color border,
                       Color point) {

        QreyesRenderer renderer;

        switch (format) {

            case R_BORDER_R_POINT:
                renderer = new RBRPQreyesRenderer();
                break;
            case R_BORDER_C_POINT:
                renderer = new RBCPQreyesRenderer();
                break;
            case C_BORDER_R_POINT:
                renderer = new CBRPQreyesRenderer();
                break;
            case C_BORDER_C_POINT:
                renderer = new CBCPQreyesRenderer();
                break;
            case R2_BORDER_R_POINT:
                renderer = new R2BRPQreyesRenderer();
                break;
            case R2_BORDER_C_POINT:
                renderer = new R2BCPQreyesRenderer();
                break;
            case DR2_BORDER_R_POINT:
                renderer = new DR2BRPQreyesRenderer();
                break;
            case DR2_BORDER_C_POINT:
                renderer = new DR2BCPQreyesRenderer();
                break;
            default:
                throw new IllegalArgumentException("No encoder available for format " + format);
        }

        renderer.render(image, format, position, slave, border, point);
    }

}
