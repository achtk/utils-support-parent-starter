package com.chua.apache.support.file;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.FileType;
import com.chua.common.support.file.AbstractCompress;
import com.chua.common.support.file.Decompress;
import com.chua.common.support.file.FileMedia;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

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
        File tempFile = FileUtils.createTempFile(UUID.randomUUID().toString() + ".7z", stream);

        try (SevenZFile zFile = new SevenZFile(tempFile)) {
            SevenZArchiveEntry nextEntry = null;
            while ((nextEntry = zFile.getNextEntry()) != null) {
                File file1 = new File(output, nextEntry.getName());
                if(nextEntry.isDirectory()) {
                    FileUtils.forceMkdir(file1);
                    continue;
                }

                try (FileOutputStream fileOutputStream = new FileOutputStream(file1)) {
                    IoUtils.write(fileOutputStream, 2048, zFile::read);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.forceDelete(tempFile);
        }
    }


    @Override
    public void unFile(InputStream stream, Function<FileMedia, Boolean> consumer, boolean needStream) throws IOException {
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
                if(consumer.apply(FileMedia.builder()
                        .name(name)
                        .fileType(nextEntry.isDirectory() ? FileType.FOLDER: FileType.FILE)
                        .size(nextEntry.getSize())
                        .build())) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.forceDelete(file);
        }
    }
}
