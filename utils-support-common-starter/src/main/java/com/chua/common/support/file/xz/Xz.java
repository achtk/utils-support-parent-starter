/*
 * XZ
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.AbstractCompress;
import com.chua.common.support.file.Decompress;
import com.chua.common.support.file.FileMedia;
import com.chua.common.support.utils.IoUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * XZ constants.
 *
 * @author Administrator
 */
@Spi("xz")
public class Xz extends AbstractCompress implements Decompress {
    /**
     * XZ Header Magic Bytes begin a XZ file.
     * This can be useful to detect XZ compressed data.
     */
    public static final byte[] HEADER_MAGIC = {
            (byte) 0xFD, '7', 'z', 'X', 'Z', '\0'};

    /**
     * XZ Footer Magic Bytes are the last bytes of a XZ Stream.
     */
    public static final byte[] FOOTER_MAGIC = {'Y', 'Z'};

    /**
     * Integrity check ID indicating that no integrity check is calculated.
     * <p>
     * Omitting the integrity check is strongly discouraged except when
     * the integrity of the data will be verified by other means anyway,
     * and calculating the check twice would be useless.
     */
    public static final int CHECK_NONE = 0;

    /**
     * Integrity check ID for CRC32.
     */
    public static final int CHECK_CRC32 = 1;

    /**
     * Integrity check ID for CRC64.
     */
    public static final int CHECK_CRC64 = 4;

    /**
     * Integrity check ID for SHA-256.
     */
    public static final int CHECK_SHA256 = 10;

    @Override
    public void to(OutputStream outputStream) {
        final AtomicBoolean isWrite = new AtomicBoolean(false);
        try (XZOutputStream out = new XZOutputStream(outputStream, new LZMA2Options())) {
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
    public void unFile(InputStream stream, File output) throws IOException {
        try (
                XZInputStream xzInputStream = new XZInputStream(stream);
                FileOutputStream fileOutputStream = new FileOutputStream(output);
        ) {
            IoUtils.write(xzInputStream, fileOutputStream);
        }
    }

    @Override
    public void unFile(InputStream stream, Consumer<FileMedia> consumer, boolean needStream) throws IOException {

    }

    @Override
    public void unFile(InputStream stream, Consumer<FileMedia> consumer) throws IOException {

    }

    @Override
    public void unFile(File file, File output) throws IOException {
        try (
                XZInputStream xzInputStream = new XZInputStream(Files.newInputStream(file.toPath()));
                FileOutputStream fileOutputStream = new FileOutputStream(new File(output, file.getName().replace(".gz", "")));
        ) {
            IoUtils.write(xzInputStream, fileOutputStream);
        }
    }
}
