package com.chua.example.view;

import com.chua.common.support.image.filter.ImageWaterImageFilter;
import com.chua.common.support.image.filter.TextImgWaterImageFilter;
import com.chua.common.support.image.filter.TextWaterImageFilter;
import com.chua.common.support.view.HttpViewServer;
import com.chua.common.support.view.ViewConfig;
import com.chua.common.support.view.ViewServer;

import java.awt.*;
import java.net.URL;

/**
 * @author CH
 */
public class ViewFactoryExample {

    public static void main(String[] args) throws Exception {
        ViewServer viewServer = new HttpViewServer();
        viewServer.addPlugin("water", new TextWaterImageFilter("我是水印", Color.BLACK));
        viewServer.addPlugin("logo", new ImageWaterImageFilter(new URL("https://cdn.qirenit.com/8d11da41-4f8d-4bcc-a36e-d36cddea2021").openStream()));
        viewServer.addPlugin("logo-text", new TextImgWaterImageFilter("我是水印", new URL("https://cdn.qirenit.com/8d11da41-4f8d-4bcc-a36e-d36cddea2021").openStream(), new com.chua.common.support.protocol.image.ImagePoint(40, 20)));
        viewServer.addContext("demo", ViewConfig.newBuilder().setPath("D://"));
        viewServer.addContext("demo1", ViewConfig.newBuilder().setType("minio").setPath("http://127.0.0.1:9000").setAppKey("minioadmin").setAppSecret("minioadmin"));
        viewServer.addContext("achtk", ViewConfig.newBuilder().setType("alibaba").setAppKey("").setAppSecret("NElUp68vSAosQRCAKAOZNgGCiPeswQ"));
        viewServer.run(new String[]{"97"});
    }
}
