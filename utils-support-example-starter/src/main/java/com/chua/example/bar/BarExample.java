package com.chua.example.bar;

import com.chua.common.support.lang.bar.BarCodeBuilder;

/**
 * @author CH
 */
public class BarExample {

    public static void main(String[] args) {
        BarCodeBuilder
                .newBuilder()
                .codeEyesFormat(BarCodeBuilder.QrCodeEyesFormat.C_BORDER_C_POINT)
    }
}
