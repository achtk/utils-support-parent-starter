package com.chua.zxing.support.bar.zing;

import com.chua.common.support.lang.bar.BarCodeBuilder;
import com.google.zxing.common.BitMatrix;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author CH
 * @since 2022-05-16
 */
public class ZingMultiFormatQrcodeEyesRenderer implements ZingRenderer {

    private final BitMatrix bitMatrix;

    public ZingMultiFormatQrcodeEyesRenderer(BitMatrix bitMatrix) {
        this.bitMatrix = bitMatrix;
    }

    @Override
    public void render(
            BufferedImage image,
            BarCodeBuilder.QrCodeEyesFormat format,
            ZingQrcodeEyesPosition position,
            Color slave,
            Color border,
            Color point) {
        ZingRenderer renderer;

        switch (format) {
            case R_BORDER_GRADIENT_POINT:
                renderer = new ZingGradientQrcodeEyesRenderer(bitMatrix);
                break;
            case GRADIENT:
                renderer = new ZingGradientV1QrcodeEyesRenderer(bitMatrix);
                break;
            case R_RANDOM:
                renderer = new ZingRandomQrcodeEyesRenderer(bitMatrix);
                break;
//            case R_BORDER_R_POINT:
//                renderer = new ZingRbrpQrcodeEyesRenderer();
//                break;
//            case R_BORDER_C_POINT:
//                renderer = new ZingRbcpQrcodeEyesRenderer();
//                break;
//            case C_BORDER_R_POINT:
//                renderer = new ZingCbrpQrcodeEyesRenderer();
//                break;
//            case C_BORDER_C_POINT:
//                renderer = new ZingCbcpQrcodeEyesRenderer();
//                break;
//            case R2_BORDER_R_POINT:
//                renderer = new ZingR2brpQrcodeEyesRenderer();
//                break;
//            case R2_BORDER_C_POINT:
//                renderer = new ZingR2bcpQrcodeEyesRenderer();
//                break;
            case DR2_BORDER_R_POINT:
                renderer = new ZingDr2brpQrcodeEyesRenderer();
                break;
//            case DR2_BORDER_C_POINT:
//                renderer = new ZingDr2BcpQrcodeEyesRenderer();
//                break;
            default:
                renderer = new ZingQrcodeEyesRenderer(bitMatrix);
        }

        renderer.render(image, format, position, slave, border, point);
    }
}
