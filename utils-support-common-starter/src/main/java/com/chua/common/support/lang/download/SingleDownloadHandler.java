package com.chua.common.support.lang.download;

import com.chua.common.support.lang.process.ProgressBar;
import com.chua.common.support.lang.process.ProgressBarBuilder;
import com.chua.common.support.lang.process.ProgressBarStyle;
import com.chua.common.support.lang.process.ProgressStyle;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

/**
 * 单例
 *
 * @author CH
 */
public class SingleDownloadHandler implements DownloadHandler {
    private final URL url;
    private final String fileName;
    private final int total;
    private final String savePath;
    private final Downloader downloader;
    private final File file;
    private final long sizeAlreadyExists;
    private final ProgressBar consoleProgressBar;

    @SneakyThrows
    public SingleDownloadHandler(URL url, int total, Downloader downloader) {
        this.url = url;
        this.fileName = createFileName(url.openConnection());
        this.total = total;
        this.savePath = downloader.savePath;
        this.downloader = downloader;
        this.file = new File(savePath, fileName);
        this.sizeAlreadyExists = file.length();
        this.consoleProgressBar = ProgressBarBuilder.newBuilder()
                .setTaskName(url.toExternalForm())
                .setInitialMax(total)
                .setProgressStyle(ProgressStyle.SIZE)
                .build();
        this.consoleProgressBar.stepBy(sizeAlreadyExists);
    }

    @Override
    @SneakyThrows
    public void execute() {
        URLConnection urlConnection = url.openConnection();
        urlConnection.addRequestProperty("Range", "bytes=" + sizeAlreadyExists + "-");
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            randomAccessFile.seek(sizeAlreadyExists);
            try (InputStream inputStream = urlConnection.getInputStream()) {
                int line = 0;
                byte[] bytes = new byte[downloader.buffer];
                while ((line = inputStream.read(bytes)) != -1) {
                    randomAccessFile.write(bytes, 0, line);
                    consoleProgressBar.stepBy(line);
                }
            }
        }

    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
