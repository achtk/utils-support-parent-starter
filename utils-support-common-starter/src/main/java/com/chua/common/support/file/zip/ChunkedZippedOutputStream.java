package com.chua.common.support.file.zip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * zip
 *
 * @author CH
 */
public class ChunkedZippedOutputStream {

    private final long MAX_FILE_SIZE = 16000000;
    private final String PART_POSTFIX = ".part.";
    private final String FILE_EXTENSION = ".zip";
    public ZipOutputStream zipOutputStream;
    private String path;
    private String name;
    private long currentSize;
    private int currentChunkIndex;

    public ChunkedZippedOutputStream(String path, String name) throws Exception {
        this.path = path;
        this.name = name;
        constructNewStream();
    }

    public void addEntry(ZipEntry entry) throws Exception {
        long entrySize = entry.getCompressedSize();
        if ((currentSize + entrySize) > MAX_FILE_SIZE) {
            closeStream();
            constructNewStream();

        } else {
            currentSize += entrySize;
            zipOutputStream.putNextEntry(entry);
        }

    }

    private void closeStream() throws IOException {
        zipOutputStream.close();
    }

    private void constructNewStream() throws Exception {
        zipOutputStream = new ZipOutputStream(Files.newOutputStream(new File(path, constructCurrentPartName()).toPath()));
        currentChunkIndex++;
        currentSize = 0;
    }

    private String constructCurrentPartName() {
        StringBuilder partNameBuilder = new StringBuilder(name);
        partNameBuilder.append(PART_POSTFIX);
        partNameBuilder.append(currentChunkIndex);
        partNameBuilder.append(FILE_EXTENSION);
        return partNameBuilder.toString();

    }
}
