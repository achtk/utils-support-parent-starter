package com.chua.zxing.support.bar.codegen.qrcode;

import com.chua.zxing.support.bar.codegen.qrcode.Qrcode.Logo;

import java.awt.image.BufferedImage;

public class ThreadQrcode extends ThreadLocal<Qrcode> {

    @Override
    protected Qrcode initialValue() {
        return new Qrcode();
    }

    public void setLogo(String path, boolean remote) {
        get().setLogo(new Logo(path, remote));
    }

    public void setImage(BufferedImage image) {
        get().setImage(image);
    }

    public BufferedImage getImage() {
        return get().getImage();
    }

    public Logo getLogo() {
        return get().getLogo();
    }

}
