package com.chua.example.download;

import com.chua.common.support.lang.download.Downloader;

/**
 * @author CH
 * @since 2022/8/10 0:05
 */
public class DownloaderExample {

    public static void main(String[] args) {
        Downloader downloader = Downloader.newBuilder().buffer(2 * 1024 * 1024).threads(8).build();

        downloader.download("http://openrecord.ys7.com/REC_FILES/cd50a22c8fc54d41a55312d6a6b093a6/dining/K27933867-1/32f5aba3a0bc481191d97d11f2f8305a_20230411T090800Z_20230411T090900Z.mp4?Expires=1681186691&OSSAccessKeyId=LTAI4G6HFM3XPqa8rBjxHJRE&Signature=RQ7gIwNECCx5vgEFqWIRxT8eCu0%3D&response-content-type=video%2Fmp4&auth_key=1681186691-0-58281f45349e402bb7377071cc32bfdc-ae2dba6deca82de6b0b9cc6e80243e16");
    }
}
