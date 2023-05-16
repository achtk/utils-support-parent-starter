package com.chua.common.support.lang.lock;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * 文件锁
 *
 * @author CH
 * @since 2022-05-27
 */
public final class FileLock implements Lock {

    private final File file;
    private static final String DEFAULT_PATH = System.getProperty("user.dir");
    private RandomAccessFile randomAccessFile;
    private java.nio.channels.FileLock fileLock;
    private FileChannel fileChannel;

    public FileLock() {
        this(new File(DEFAULT_PATH + "/service.lock"));
    }

    public FileLock(String file) {
        this(new File(DEFAULT_PATH + File.separator + file + ".lock"));
    }

    public FileLock(File file) {
        this.file = file;
    }

    @Override
    public boolean lock(int timeout) {
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            fileChannel = randomAccessFile.getChannel();
            fileLock = fileChannel.tryLock();
            return fileLock != null;
        } catch (Throwable e) {
            return false;
        }
    }


    @Override
    public void unlock() {
        try {
            if (fileLock != null) {
                fileLock.release();
            }

            if (fileChannel != null && fileChannel.isOpen()) {
                fileChannel.close();
            }

            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            try {
                file.delete();
            } catch (Exception ignored) {
                System.out.println();
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public boolean tryLock() {
        return lock();
    }

    @Override
    public void close() throws Exception {
        unlock();
    }
}
