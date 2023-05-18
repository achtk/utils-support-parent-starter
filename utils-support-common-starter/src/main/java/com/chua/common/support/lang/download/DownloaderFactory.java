package com.chua.common.support.lang.download;

import lombok.SneakyThrows;

import java.net.URL;
import java.net.URLConnection;

/**
 * 下载器
 *
 * @author CH
 * @since 2022/8/9 23:48
 */
public class DownloaderFactory {

    private final int total;
    private final Downloader downloader;
    private final URL url;
    private final URLConnection connection;

    /**
     * 下载工厂
     *
     * @param url 连接
     */
    @SneakyThrows
    public DownloaderFactory(Downloader downloader, String url) {
        this.downloader = downloader;
        this.url = new URL(url);
        this.connection = this.url.openConnection();
        this.total = connection.getContentLength();
    }


    /**
     * 下载
     *
     * @param savePath 保存位置
     * @param threads  线程数
     * @return DownloadHandler
     */
    public DownloadHandler handler(String savePath, int threads) {
        //已下载数量
        if (threads == 1) {
            return new SingleDownloadHandler(url, total, downloader);
        }
        return new MultiDownloadHandler(url, total, downloader);
    }
}
