package com.chua.zxing.support.bar.zing;


import com.chua.common.support.lang.bar.BarCodeBuilder;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Deprecated reason: low recognition rate
 *
 * @author Bosco.Liao
 * @since 1.3.0
 */
public class ZingCbcpQrcodeEyesRenderer extends ZingCbrpQrcodeEyesRenderer {

    @Override
    public void checkFormat(BarCodeBuilder.QrCodeEyesFormat format) {
        if ("C_BORDER_C_POINT".equalsIgnoreCase(format.name())) {
            throw new IllegalArgumentException("Can only render C_BORDER_C_POINT, but got " + format);
        }
    }

    @Override
    public Shape getPointShape(double x, double y, double w, double h, double arcw, double arch) {
        return new Ellipse2D.Double(x, y, w, h);
    }

}
