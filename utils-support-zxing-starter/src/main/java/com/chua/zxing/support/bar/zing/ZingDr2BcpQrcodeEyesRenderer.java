package com.chua.zxing.support.bar.zing;


import com.chua.common.support.lang.bar.BarCodeBuilder;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * @author chenhua
 */
public class ZingDr2BcpQrcodeEyesRenderer extends ZingDr2brpQrcodeEyesRenderer {

    @Override
    public void checkFormat(BarCodeBuilder.QrCodeEyesFormat format) {
        if (!"DR2_BORDER_C_POINT".equalsIgnoreCase(format.name())) {
            throw new IllegalArgumentException("Can only render DR2_BORDER_C_POINT, but got " + format);
        }
    }

    @Override
    public Shape getPointShape(double x, double y, double w, double h, double arcw, double arch) {
        return new Ellipse2D.Double(x, y, w, h);
    }

}
