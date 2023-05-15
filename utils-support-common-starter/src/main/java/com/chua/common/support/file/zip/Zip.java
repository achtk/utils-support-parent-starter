package com.chua.common.support.file.zip;

import com.chua.common.support.file.AbstractCompress;
import com.chua.common.support.file.Decompress;
import com.chua.common.support.file.FileMedia;
import com.chua.common.support.spi.Spi;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.chua.common.support.constant.FileType.FILE;
import static com.chua.common.support.constant.FileType.FOLDER;

/**
 * zip
 *
 * @author CH
 */
@Spi("zip")
public class Zip extends AbstractCompress implements Decompress {

    @Override
    public void to(OutputStream outputStream) {
        try (ZipOutputStream out = new ZipOutputStream(outputStream)) {
            withFile((name, file) -> {
                if (file.isFile()) {
                    ZipEntry zipEntry = new ZipEntry(name);
                    out.putNextEntry(zipEntry);
                    IoUtils.write(Files.newInputStream(file.toPath()), out);
                    out.closeEntry();
                }


            });
            withStream((name, inputStream) -> {
                try {
                    ZipEntry zipEntry = new ZipEntry(name);
                    out.putNextEntry(zipEntry);
                    IoUtils.write(inputStream, out);
                    out.closeEntry();
                } catch (IOException ignored) {
                }
                return;
            });
            withSource((name, source) -> {
                try {
                    ZipEntry zipEntry = new ZipEntry(name);
                    out.putNextEntry(zipEntry);
                    out.write(source);
                    out.closeEntry();
                } catch (IOException ignored) {
                }
            });
            out.flush();
            out.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解压
     *
     * @param zipInputStream zip
     * @param consumer       输出
     * @param needStream     是否需要流
     */
    public static void unZip(ZipInputStream zipInputStream, Consumer<FileMedia> consumer, boolean needStream) throws IOException {
        ZipEntry nextEntry = null;
        while ((nextEntry = zipInputStream.getNextEntry()) != null) {
            String name = nextEntry.getName();
            FileMedia.FileMediaBuilder builder = FileMedia.builder();
            builder.name(name)
                    .size(nextEntry.getSize())
                    .fileType(nextEntry.isDirectory() ? FOLDER : FILE)
                    .build();
            if (needStream) {
                int count = 0;
                byte[] data = new byte[4096];
                try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
                    while ((count = zipInputStream.read(data)) != -1) {
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

    /**
     * 解压
     *
     * @param zipInputStream zip
     * @param output         输出
     */
    public static void unZip(ZipInputStream zipInputStream, File output) throws IOException {
        ZipEntry nextEntry = null;
        while ((nextEntry = zipInputStream.getNextEntry()) != null) {
            String name = nextEntry.getName();
            File directory = new File(output, name);
            if (nextEntry.isDirectory()) {
                try {
                    FileUtils.forceMkdir(directory);
                } catch (IOException ignore) {
                }
                continue;
            }

            write(zipInputStream, directory);

        }
    }

    /**
     * 写入文件
     *
     * @param stream       流
     * @param outputStream 输出
     */
    protected static void write(ZipInputStream stream, ByteArrayOutputStream outputStream) throws IOException {
        int count;
        byte[] data = new byte[4096];
        while ((count = stream.read(data)) != -1) {
            outputStream.write(data, 0, count);
        }
        outputStream.flush();
    }

    /**
     * 写入文件
     *
     * @param stream 流
     * @param file   输出
     */
    protected static void write(ZipInputStream stream, File file) {
        try {
            FileUtils.forceMkdirParent(file);
        } catch (IOException ignored) {
        }

        int count;
        byte[] data = new byte[4096];
        try (FileOutputStream fos = new FileOutputStream(file)) {
            while ((count = stream.read(data)) != -1) {
                fos.write(data, 0, count);
            }
            fos.flush();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void unFile(InputStream inputStream, File output) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            unZip(zipInputStream, output);
        }
    }

    @Override
    public void unFile(InputStream stream, Consumer<FileMedia> consumer, boolean needStream) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(stream)) {
            unZip(zipInputStream, consumer, needStream);
        }
    }
}
