package com.chua.common.support.file.gzip;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.AbstractCompress;
import com.chua.common.support.file.Decompress;
import com.chua.common.support.file.FileMedia;
import com.chua.common.support.utils.IoUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * gzip
 *
 * @author CH
 */
@Spi("gz")
public class Gzip extends AbstractCompress implements Decompress {
    @Override
    public void to(OutputStream outputStream) {
        final AtomicBoolean isWrite = new AtomicBoolean(false);
        try (GZIPOutputStream out = new GZIPOutputStream(outputStream)) {
            withFile((name, file) -> {
                if (file.isFile() && !isWrite.get()) {
                    IoUtils.write(Files.newInputStream(file.toPath()), out);
                    isWrite.set(true);
                    return;
                }


            });
            withStream((name, inputStream) -> {
                if (!isWrite.get()) {
                    try {
                        IoUtils.write(inputStream, out);
                        isWrite.set(true);
                    } catch (IOException ignored) {
                    }
                }
            });
            withSource((name, source) -> {
                if (!isWrite.get()) {
                    try {
                        IoUtils.write(source, out);
                        isWrite.set(true);
                    } catch (IOException ignored) {
                    }
                }
            });
            out.flush();
            out.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unFile(InputStream inputStream, File output) throws IOException {
        try (
                GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(output);
        ) {
            IoUtils.write(gzipInputStream, fileOutputStream);

        }
    }

    @Override
    public void unFile(InputStream stream, Consumer<FileMedia> consumer, boolean needStream) throws IOException {

    }

    @Override
    public void unFile(File file, File output) throws IOException {
        try (
                GZIPInputStream gzipInputStream = new GZIPInputStream(Files.newInputStream(file.toPath()));
                FileOutputStream fileOutputStream = new FileOutputStream(new File(output, file.getName().replace(".gz", "")));
        ) {
            IoUtils.write(gzipInputStream, fileOutputStream);

        }
    }

}
