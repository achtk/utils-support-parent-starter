package com.chua.example.bar;

import com.chua.common.support.lang.qr.QrCode;
import com.chua.common.support.lang.qr.QrCodeConfigure;
import com.chua.common.support.spi.ServiceProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author CH
 */
public class BarExample {

    public static void main(String[] args) throws IOException {
        QrCode qrCode = ServiceProvider.of(QrCode.class).getNewExtension("zxing", QrCodeConfigure.builder().content("233").build());
        qrCode.out(Files.newOutputStream(Paths.get("Z://code.png")));
        System.out.println();
    }
}
