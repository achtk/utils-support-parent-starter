package com.chua.zxing.support.bar.zing;

import com.chua.common.support.annotations.Spi;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.OutputStream;


/**
 * 条形码/二维码输出
 *
 * @author CH
 * @version 1.0.0
 */
@Spi("zing-bar")
public class ZingBarCodeWriter extends ZingCodeCodeWriter {


    @Override
    public void toStream(OutputStream outputStream) throws Exception {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix matrix = writer.encode(barCodeBuilder.generate,
                BarcodeFormat.CODE_128,
                barCodeBuilder.width,
                barCodeBuilder.height,
                createConfig());
        MatrixToImageWriter.writeToStream(matrix, barCodeBuilder.outType, outputStream);
    }


}
