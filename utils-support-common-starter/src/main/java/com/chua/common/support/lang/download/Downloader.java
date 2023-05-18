package com.chua.common.support.lang.download;

import com.chua.common.support.utils.FileUtils;
import lombok.SneakyThrows;

/**
 * 下载器
 *
 * @author CH
 */
public class Downloader {
    protected int threads = 1;
    protected String savePath = ".";
    protected int buffer = 128 * 1024;
    private DownloadHandler downloadHandler;

    /**
     * 创建下载器
     *
     * @return 下载器
     */
    public static DownloaderBuilder newBuilder() {
        return new DownloaderBuilder();
    }

    /**
     * 下载
     *
     * @param url 地址
     */
    @SneakyThrows
    public void download(String url) {
        FileUtils.mkdir(savePath);
        DownloaderFactory downloaderFactory = new DownloaderFactory(this, url);
        this.downloadHandler = downloaderFactory.handler(savePath, threads);
        downloadHandler.execute();
    }

    /**
     * 文件名称
     * @return 文件名称
     */
    public String getFileName() {
        return downloadHandler.getFileName();
    }

    /**
     * 下载器
     */
    public static class DownloaderBuilder {
        private final Downloader downloader = new Downloader();

        /**
         * 下载线程数
         *
         * @param threads 线程数
         * @return this
         */
        public DownloaderBuilder threads(int threads) {
            downloader.threads = threads;
            return this;
        }

        /**
         * 块
         *
         * @param buffer 块
         * @return this
         */
        public DownloaderBuilder buffer(int buffer) {
            downloader.buffer = buffer;
            return this;
        }

        /**
         * 保存位置
         *
         * @param savePath 保存位置
         * @return this
         */
        public DownloaderBuilder savePath(String savePath) {
            downloader.savePath = savePath;
            return this;
        }

        /**
         * 下载器
         *
         * @return 下载器
         */
        public Downloader build() {
            return downloader;
        }
    }
}
