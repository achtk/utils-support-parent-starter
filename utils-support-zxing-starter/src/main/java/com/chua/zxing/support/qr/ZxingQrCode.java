package com.chua.zxing.support.qr;

import com.chua.common.support.lang.qr.QrCode;
import com.chua.common.support.lang.qr.QrCodeConfigure;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.UrlUtils;
import org.iherus.codegen.qrcode.QrcodeConfig;
import org.iherus.codegen.qrcode.QreyesFormat;
import org.iherus.codegen.qrcode.SimpleQrcodeGenerator;

import java.io.IOException;
import java.io.OutputStream;

/**
 * zxing qr码
 *
 * @author CH
 * @since 2023/09/05
 */
public class ZxingQrCode implements QrCode {

    private QrCodeConfigure qrCodeConfigure;

    public ZxingQrCode(QrCodeConfigure qrCodeConfigure) {
        this.qrCodeConfigure = qrCodeConfigure;
    }


    @Override
    public void out(OutputStream outputStream) {
        QrcodeConfig config = new QrcodeConfig();
        config.setHeight(qrCodeConfigure.getHeight()).setWidth(qrCodeConfigure.getWidth());

        if (null != qrCodeConfigure.getEyes()) {
            config.setCodeEyesFormat(QreyesFormat.valueOf(qrCodeConfigure.getEyes().name()));
        }

        SimpleQrcodeGenerator generator = new SimpleQrcodeGenerator(config);

        generator.generate(qrCodeConfigure.getContent());
        String logo = qrCodeConfigure.getLogo();
        if (StringUtils.isNotEmpty(logo)) {
            if (UrlUtils.isUrl(logo)) {
                generator.setRemoteLogo(logo);
            } else {
                generator.setLogo(logo);
            }
        }
        try {
            generator.toStream(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
