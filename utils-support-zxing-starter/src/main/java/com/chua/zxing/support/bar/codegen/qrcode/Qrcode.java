package com.chua.zxing.support.bar.codegen.qrcode;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Qrcode implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -9064635833671724433L;

    private BufferedImage image;

    private Logo logo;

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Logo getLogo() {
        return logo;
    }

    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    public static final class Logo {

        private String path;

        private boolean remote;

        public Logo(String path, boolean remote) {
            this.path = path;
            this.remote = remote;
        }

        public String getPath() {
            return path;
        }

        public boolean isRemote() {
            return remote;
        }

    }

}
