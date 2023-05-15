package com.chua.common.support.file.tar;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.FileType;
import com.chua.common.support.file.AbstractCompress;
import com.chua.common.support.file.Decompress;
import com.chua.common.support.file.FileMedia;
import com.chua.common.support.lang.process.ProgressStyle;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.lang.process.ProgressBar;

import java.io.*;
import java.nio.file.Files;
import java.util.function.Consumer;

/**
 * tar
 *
 * @author CH
 */
@Spi("tar")
public class Tar extends AbstractCompress implements Decompress {

    public static void unTar(TarInputStream tarInputStream, Consumer<FileMedia> consumer, boolean needStream) throws IOException {
        TarEntry nextEntry = null;
        while ((nextEntry = tarInputStream.getNextEntry()) != null) {
            String name = nextEntry.getName();
            FileMedia.FileMediaBuilder builder = FileMedia.builder();
            builder.name(name)
                    .fileType(nextEntry.isDirectory() ? FileType.FOLDER: FileType.FILE)
                    .size(nextEntry.getSize());
            if(needStream) {
                int count = 0;
                byte[] data = new byte[4096];
                try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
                    while ((count = tarInputStream.read(data)) != -1) {
                        fos.write(data, 0, count);
                    }
                    fos.flush();
                    builder.stream(new ByteArrayInputStream(fos.toByteArray()));
                } catch (Exception ignored) {
                }
            }
            consumer.accept(builder.build());
        }
    }

    public static void unTar(TarInputStream tarInputStream, File output) throws IOException {
        TarEntry nextEntry = null;
        while ((nextEntry = tarInputStream.getNextEntry()) != null) {
            String name = nextEntry.getName();
            File directory = new File(output, name);
            if (nextEntry.isDirectory()) {
                try {
                    FileUtils.forceMkdir(directory);
                } catch (IOException ignore) {
                }
                continue;
            }
            write(tarInputStream, directory);
        }
    }

    /**
     * 写入文件
     *
     * @param tis  流
     * @param file 输出
     */
    protected static void write(TarInputStream tis, File file) {
        try {
            FileUtils.forceMkdirParent(file);
        } catch (IOException ignored) {
        }

        int count;
        byte[] data = new byte[4096];
        try (ProgressBar consoleProgressBar = new ProgressBar(file.getName() + "安装进度: ", -1, ProgressStyle.SIZE)) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                while ((count = tis.read(data)) != -1) {
                    fos.write(data, 0, count);
                    consoleProgressBar.stepBy(count);
                }
                fos.flush();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void to(OutputStream outputStream) {
        try (TarOutputStream out = new TarOutputStream(outputStream)) {
            withFile((name, file) -> {
                TarEntry zipEntry = new TarEntry(file, name);
                out.putNextEntry(zipEntry);
                try {
                    if (file.isFile()) {
                        IoUtils.write(Files.newInputStream(file.toPath()), out);
                        return;
                    }
                } finally {
                    out.closeEntry();
                }

            });
            withStream((name, inputStream) -> {
                try {
                    TarEntry zipEntry = new TarEntry(name);
                    zipEntry.setSize(inputStream.available());
                    out.putNextEntry(zipEntry);
                    IoUtils.write(inputStream, out);
                    out.closeEntry();
                } catch (IOException ignored) {
                }
                return;
            });
            withSource((name, source) -> {
                try {
                    TarEntry zipEntry = new TarEntry(name);
                    zipEntry.setSize(source.length);
                    out.putNextEntry(zipEntry);
                    out.write(source);
                    out.closeEntry();
                } catch (IOException ignored) {
                }
                return;
            });
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unFile(InputStream stream, File output) throws IOException {
        try (TarInputStream tarInputStream = new TarInputStream(stream)) {
            unTar(tarInputStream, output);
        }
    }

    @Override
    public void unFile(InputStream stream, Consumer<FileMedia> consumer, boolean needStream) throws IOException {
        try (TarInputStream tarInputStream = new TarInputStream(stream)) {
            unTar(tarInputStream, consumer, needStream);
        }
    }
}
