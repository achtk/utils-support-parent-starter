package com.chua.zxing.support.bar;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.bar.BarCodeBuilder;
import com.chua.common.support.lang.bar.BarCodeWriter;
import com.chua.common.support.utils.StringUtils;
import com.chua.zxing.support.bar.codegen.Codectx;
import com.chua.zxing.support.bar.codegen.qrcode.QrcodeConfig;
import com.chua.zxing.support.bar.codegen.qrcode.QreyesFormat;
import com.chua.zxing.support.bar.codegen.qrcode.SimpleQrcodeGenerator;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * google bar实现
 *
 * @author CH
 * @version 1.0.0
 */
@Spi("zing2-code")
public class Zing2QrCodeWriter extends BaseBarCodeWriter {

    private BarCodeBuilder barCodeBuilder;

    @Override
    public BarCodeWriter config(BarCodeBuilder barCodeBuilder) {
        this.barCodeBuilder = barCodeBuilder;
        return this;
    }

    @Override
    public File toFile(String outPath) {
        File temp = new File(outPath);
        try {
            toStream(Files.newOutputStream(temp.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return temp;
    }

    @Override
    public void toStream(OutputStream stream) {
        QrcodeConfig qrcodeConfig = new QrcodeConfig();
        qrcodeConfig.setMasterColor(barCodeBuilder.masterColor)
                .setWidth(barCodeBuilder.width)
                .setHeight(barCodeBuilder.height)
                .setBorderColor(barCodeBuilder.borderColor)
                .setBorderDashGranularity(barCodeBuilder.borderDashGranularity)
                .setBorderRadius(barCodeBuilder.borderRadius)
                .setBorderSize(barCodeBuilder.borderSize)
                .setBorderStyle(Codectx.BorderStyle.valueOf(barCodeBuilder.borderStyle.name()))
                .setCodeEyesBorderColor(barCodeBuilder.codeEyesBorderColor)
                .setCodeEyesFormat(QreyesFormat.valueOf(barCodeBuilder.qrCodeEyesFormat.name()))
                .setCodeEyesPointColor(barCodeBuilder.codeEyesPointColor)
                .setPadding(barCodeBuilder.padding)
                .setLogoPanelArcHeight((int) barCodeBuilder.panelArcHeight)
                .setLogoPanelArcWidth((int) barCodeBuilder.panelArcWidth)
                .setLogoArcWidth((int) barCodeBuilder.arcWidth)
                .setLogoArcHeight((int) barCodeBuilder.arcHeight)
                .setMargin(barCodeBuilder.margin)
                .setLogoRatio(barCodeBuilder.ratio)
                .setErrorCorrectionLevel(ErrorCorrectionLevel.valueOf(barCodeBuilder.level))
        ;

        SimpleQrcodeGenerator simpleQrcodeGenerator = new SimpleQrcodeGenerator(qrcodeConfig);
        if (!StringUtils.isNullOrEmpty(barCodeBuilder.logoPath)) {
            simpleQrcodeGenerator.setLogo(barCodeBuilder.logoPath);
        }

        simpleQrcodeGenerator.generate(barCodeBuilder.generate);
        try {
            simpleQrcodeGenerator.toStream(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
