package com.chua.common.support.lang.download;

import com.chua.common.support.lang.process.ProgressBar;
import com.chua.common.support.lang.process.ProgressBarBuilder;
import com.chua.common.support.lang.process.ProgressStyle;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

/**
 * \rangle
 *
 * @author CH
 */
public class RangDownloader implements DownloadHandler {
    private final String newFileName;
    private final URL url;
    private final int start;
    private final int end;
    private final Downloader downloader;

    public RangDownloader(String newFileName, URL url, int start, int end, Downloader downloader) {
        this.newFileName = newFileName;
        this.url = url;
        this.start = start;
        this.end = end;
        this.downloader = downloader;
    }

    @Override
    @SneakyThrows
    public void execute() {
        URLConnection urlConnection = url.openConnection();
        File file = new File(downloader.savePath, newFileName);
        long sizeAlreadyExists = file.length();
        if (sizeAlreadyExists >= end - start) {
            return;
        }

        try (ProgressBar consoleProgressBar = ProgressBarBuilder.newBuilder().setTaskName(newFileName).setInitialMax(end - start).setProgressStyle(ProgressStyle.SIZE).build()) {
            consoleProgressBar.stepTo(sizeAlreadyExists);
            urlConnection.addRequestProperty("Range", "bytes=" + start + "-" + end);

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

    }

    @Override
    public String getFileName() {
        return newFileName;
    }
}
