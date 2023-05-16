package com.chua.common.support.file.tailer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * commons-io
 *
 * @author CH
 */
public class Tailer implements Runnable {

    private static final int DEFAULT_DELAY_MILLIS = 1000;
    private static final String RAF_MODE = "r";
    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
    private static final int DEFAULT_BUFFER_SIZE = 2048;
    private static final int EOF = -1;
    public static final int CR = '\r';
    /**
     * LF char.
     *
     * @since 2.9.0
     */
    public static final int LF = '\n';
    private final byte[] inBuf;
    private final File file;
    private final Charset charset;
    private final long delayMillis;
    private final boolean end;
    private final TailerListener listener;
    private final boolean reOpen;
    private volatile boolean run = true;

    /**
     * 初始化
     *
     * @param file     文件
     * @param listener 监听
     */
    public Tailer(final File file, final TailerListener listener) {
        this(file, listener, DEFAULT_DELAY_MILLIS);
    }

    /**
     * 初始化
     *
     * @param file        文件
     * @param listener    监听
     * @param delayMillis 延迟
     */
    public static Tailer create(final File file, final TailerListener listener, final int delayMillis) {
        return new Tailer(file, listener, delayMillis);
    }

    /**
     * 初始化
     *
     * @param file        文件
     * @param listener    监听
     * @param delayMillis 延迟
     * @param charset     編碼
     */
    public static Tailer create(final File file, final TailerListener listener, final int delayMillis, final Charset charset) {
        return new Tailer(file, listener, delayMillis, false, DEFAULT_BUFFER_SIZE, charset);
    }

    /**
     * 初始化
     *
     * @param file        文件
     * @param listener    监听
     * @param delayMillis 延迟
     */
    public Tailer(final File file, final TailerListener listener, final long delayMillis) {
        this(file, listener, delayMillis, false, DEFAULT_BUFFER_SIZE, Charset.defaultCharset());
    }


    /**
     * 初始化
     *
     * @param file        文件
     * @param listener    监听
     * @param delayMillis 延迟
     * @param end         是否结尾
     */
    public Tailer(
            final File file,
            final TailerListener listener,
            final long delayMillis,
            final boolean end,
            final int bufSize,
            final Charset charset) {
        this.file = file;
        this.delayMillis = delayMillis;
        this.end = end;

        this.inBuf = new byte[bufSize];


        this.listener = listener;
        listener.init(this);
        this.reOpen = false;
        this.charset = charset;
    }

    @Override
    public void run() {
        RandomAccessFile reader = null;
        try {
            long last = 0;
            long position = 0;

            while (isRun() && reader == null) {
                try {
                    reader = new RandomAccessFile(file, RAF_MODE);
                } catch (final FileNotFoundException e) {
                    listener.fileNotFound();
                }
                if (reader == null) {
                    Thread.sleep(delayMillis);
                } else {
                    position = end ? file.length() : 0;
                    last = lastModified(file);
                    //设置上一次结束位置
                    reader.seek(position);
                }
            }

            while (isRun()) {
                final boolean newer = isChanged(file, last);
                final long length = file.length();
                if (length < position) {
                    try (RandomAccessFile save = reader) {
                        reader = new RandomAccessFile(file, RAF_MODE);
                        try {
                            readLines(save);
                        } catch (final IOException ioe) {
                            listener.handle(ioe);
                        }
                        position = 0;
                    } catch (final FileNotFoundException e) {
                        listener.fileNotFound();
                        Thread.sleep(delayMillis);
                    }
                    continue;
                }
                if (length > position) {
                    position = readLines(reader);
                    last = lastModified(file);
                } else if (newer) {
                    position = 0;
                    reader.seek(position);
                    position = readLines(reader);
                    last = lastModified(file);
                }
                if (reOpen && reader != null) {
                    reader.close();
                }

                Thread.sleep(delayMillis);
                if (isRun() && reOpen) {
                    reader = new RandomAccessFile(file, RAF_MODE);
                    reader.seek(position);
                }
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            listener.handle(e);
        } catch (final Exception e) {
            listener.handle(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (final IOException e) {
                listener.handle(e);
            }
            stop();
        }
    }

    /**
     * 按行解析
     *
     * @param reader 解析流
     * @return 位置
     * @throws IOException ex
     */
    private long readLines(final RandomAccessFile reader) throws IOException {
        try (ByteArrayOutputStream lineBuf = new ByteArrayOutputStream(64)) {
            long pos = reader.getFilePointer();
            long rePos = pos;
            int num;
            boolean seenCr = false;
            while (isRun() && ((num = reader.read(inBuf)) != EOF)) {
                for (int i = 0; i < num; i++) {
                    final byte ch = inBuf[i];
                    switch (ch) {
                        case LF:
                            seenCr = false;
                            listener.handle(new String(lineBuf.toByteArray(), charset));
                            lineBuf.reset();
                            rePos = pos + i + 1;
                            break;
                        case CR:
                            if (seenCr) {
                                lineBuf.write(CR);
                            }
                            seenCr = true;
                            break;
                        default:
                            if (seenCr) {
                                seenCr = false;
                                listener.handle(new String(lineBuf.toByteArray(), charset));
                                lineBuf.reset();
                                rePos = pos + i + 1;
                            }
                            lineBuf.write(ch);
                    }
                }
                pos = reader.getFilePointer();
            }

            reader.seek(rePos);

            return rePos;
        }
    }

    /**
     * 文件是否修改
     *
     * @param file       文件
     * @param timeMillis 最后一次修改时间
     * @return 文件是否修改
     */
    private boolean isChanged(File file, long timeMillis) {
        return file.exists() && lastModified(file) > timeMillis;
    }

    /**
     * 修改时间
     *
     * @param file 文件
     * @return 修改时间
     */
    private long lastModified(File file) {
        try {
            return Files.getLastModifiedTime(file.toPath()).toMillis();
        } catch (IOException e) {
            return file.lastModified();
        }
    }

    /**
     * 停止
     */
    public void stop() {
        this.run = false;
    }

    /**
     * 获取运行状态
     *
     * @return 获取运行状态
     */
    protected boolean isRun() {
        return run;
    }
}
