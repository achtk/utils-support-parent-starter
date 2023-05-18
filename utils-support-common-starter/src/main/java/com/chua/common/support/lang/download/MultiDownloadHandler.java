package com.chua.common.support.lang.download;

import com.chua.common.support.lang.process.ProgressBar;
import com.chua.common.support.lang.process.ProgressBarBuilder;
import com.chua.common.support.lang.process.ProgressStyle;
import com.chua.common.support.utils.ThreadUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * 多线程
 *
 * @author CH
 * @since 2022/8/10 0:38
 */
public class MultiDownloadHandler implements DownloadHandler {
    private final int total;
    private final Downloader downloader;
    private final URL url;
    private final String savePath;
    private final String fileName;

    @SneakyThrows
    public MultiDownloadHandler(URL url, int total, Downloader downloader) {
        this.url = url;
        this.total = total;
        this.downloader = downloader;
        this.fileName = createFileName(url.openConnection());
        this.savePath = downloader.savePath;
    }

    @Override
    @SneakyThrows
    public void execute() {
        ExecutorService executorService = ThreadUtils.newFixedThreadExecutor(downloader.threads);
        CountDownLatch countDownLatch = new CountDownLatch(downloader.threads);
        int size = total / downloader.threads;
        int less = total % downloader.threads;

        List<RangeFile> rangeFileList = new LinkedList<>();
        for (int i = 0; i < downloader.threads; i++) {
            String newFileName = fileName + ".tmp" + i;
            int start = i * size;
            int end = (i + 1) * size + ((i + 1) == downloader.threads ? less : 0) - 1;
            rangeFileList.add(new RangeFile(newFileName, start, end));
            RangDownloader handler = new RangDownloader(
                    newFileName,
                    url,
                    start,
                    end,
                    downloader);

            executorService.execute(() -> {
                try {
                    handler.execute();
                } finally {
                    countDownLatch.countDown();
                }
            });


        }
        try {
            countDownLatch.await();
        } finally {
            executorService.shutdownNow();
        }

        int offset = 0;
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
        int line = 0;
        try (ProgressBar consoleProgressBar = ProgressBarBuilder.newBuilder().setTaskName(url.toExternalForm()).setInitialMax(total).setProgressStyle(ProgressStyle.SIZE).build()) {
            byte[] bytes = new byte[downloader.buffer];
            for (RangeFile rangeFile : rangeFileList) {
                File temp = new File(savePath, rangeFile.getName());
                try (FileInputStream fis = new FileInputStream(temp)) {
                    while ((line = fis.read(bytes)) != -1) {
                        randomAccessFile.write(bytes, 0, line);
                        consoleProgressBar.stepBy(line);
                    }
                }
            }
        }
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Data
    @AllArgsConstructor
    private static class RangeFile {

        private String name;
        private int start;
        private int end;
    }
}
