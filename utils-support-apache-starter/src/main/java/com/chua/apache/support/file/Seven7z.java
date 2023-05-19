package com.chua.apache.support.file;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.FileType;
import com.chua.common.support.file.AbstractCompress;
import com.chua.common.support.file.Decompress;
import com.chua.common.support.file.FileMedia;
import com.chua.common.support.utils.FileUtils;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * 7z
 * @author CH
 */
@Spi({"7z"})
public class Seven7z extends AbstractCompress implements Decompress {
    @Override
    public void to(OutputStream outputStream) {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("7z", ".7z");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File file = tempFile.toFile();

        try (SevenZOutputFile out = new SevenZOutputFile(file)) {
            withFile((name, file1) -> {
                SevenZArchiveEntry entry = new SevenZArchiveEntry();
                entry.setName(name);
                out.putArchiveEntry(entry);
                try {
                    if (file1.isFile()) {
                        try (InputStream inputStream = Files.newInputStream(file1.toPath())) {
                            out.write(inputStream);
                        }
                        return;
                    }
                } finally {
                    out.closeArchiveEntry();
                }

            });
            withStream((name, inputStream) -> {
                try {
                    SevenZArchiveEntry entry = new SevenZArchiveEntry();
                    entry.setName(name);
                    entry.setSize(inputStream.available());
                    out.putArchiveEntry(entry);
                    out.write(inputStream);
                    out.closeArchiveEntry();
                } catch (IOException ignored) {
                }
                return;
            });
            withSource((name, source) -> {
                try {
                    SevenZArchiveEntry entry = new SevenZArchiveEntry();
                    entry.setName(name);
                    entry.setSize(source.length);

                    out.putArchiveEntry(entry);
                    out.write(source);
                    out.closeArchiveEntry();
                } catch (IOException ignored) {
                }
                return;
            });
            out.finish();

            FileUtils.write(tempFile.toFile(), outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public void unFile(InputStream stream, File output) throws IOException {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("7z", ".7z");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File file = tempFile.toFile();
        FileUtils.write(stream, file);

        try (SevenZFile zFile = new SevenZFile(file)) {
            SevenZArchiveEntry nextEntry = null;
            while ((nextEntry = zFile.getNextEntry()) != null) {
                if(nextEntry.isDirectory()) {
                    FileUtils.forceMkdir(new File(output, nextEntry.getName()));
                    continue;
                }

                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    int count;
                    byte[] data = new byte[2048];
                    while ((count = zFile.read(data)) != -1) {
                        bos.write(data, 0, count);
                    }

                    FileUtils.write(bos.toByteArray(), new File(output, nextEntry.getName()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.forceDelete(file);
        }
    }


    @Override
    public void unFile(InputStream stream, Consumer<FileMedia> consumer, boolean needStream) throws IOException {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("7z", ".7z");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File file = tempFile.toFile();
        FileUtils.write(stream, file);

        try (SevenZFile zFile = new SevenZFile(file)) {
            SevenZArchiveEntry nextEntry = null;
            while ((nextEntry = zFile.getNextEntry()) != null) {
                String name = nextEntry.getName();
                consumer.accept(FileMedia.builder()
                        .name(name)
                        .fileType(nextEntry.isDirectory() ? FileType.FOLDER: FileType.FILE)
                        .size(nextEntry.getSize())
                        .build());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.forceDelete(file);
        }
    }
}
