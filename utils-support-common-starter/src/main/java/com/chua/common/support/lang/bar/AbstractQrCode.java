package com.chua.common.support.lang.bar;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 抽象条码
 *
 * @author CH
 * @version 1.0.0
 */
public abstract class AbstractQrCode implements QrCode {
    protected int width = 300;
    protected int height = 300;
    protected String type = "jpg";
    protected BarType barType = BarType.BAR_CODE2;
    protected Charset charset = StandardCharsets.UTF_8;
    protected String word = "";
    protected int fontSize = 3;
    protected File logo;
    private boolean align;

    @Override
    public QrCode align(boolean align) {
        this.align = align;
        return this;
    }

    @Override
    public QrCode logo(File logo) {
        this.logo = logo;
        return this;
    }

    @Override
    public QrCode fontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    @Override
    public QrCode width(int width) {
        this.width = width;
        return this;
    }

    @Override
    public QrCode height(int height) {
        this.height = height;
        return this;
    }

    @Override
    public QrCode content(String content) {
        this.word = content;
        return this;
    }

    @Override
    public QrCode type(String type) {
        this.type = type;
        return this;
    }

    @Override
    public QrCode barType(BarType barType) {
        this.barType = barType;
        return this;
    }

    @Override
    public QrCode charset(Charset charset) {
        this.charset = charset;
        return this;
    }
}
