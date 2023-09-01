package com.chua.apache.support.file;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.binary.BaseByteSource;
import com.chua.common.support.binary.ByteSourceArray;
import com.chua.common.support.binary.ByteSourceInputStream;
import com.chua.common.support.file.AbstractResourceFile;
import com.chua.common.support.file.CompressFile;
import com.chua.common.support.file.ResourceFileConfiguration;
import com.chua.common.support.io.CompressInputStream;
import com.chua.common.support.matcher.AntPathMatcher;
import com.chua.common.support.resource.resource.ByteSourceResource;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.FileUtils;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 7z
 *
 * @author CH
 */
@Spi("7z")
public class SevenCompressFile extends AbstractResourceFile implements CompressFile<SevenZFile, SevenZArchiveEntry> {

    public SevenCompressFile(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration);
    }

    @Override
    public String printTree() {
        return null;
    }

    @Override
    public void forEach(BiConsumer<InputStream, SevenZArchiveEntry> action) {
    }


    @Override
    public void unpack(String folder, boolean deleteSource, String pattern) throws Exception {
        list(pattern, (sevenZFile, sevenZArchiveEntry) -> {
            String name = sevenZArchiveEntry.getName();
            if (sevenZArchiveEntry.isDirectory()) {
                return;
            }

            File temp = new File(folder, name);
            try {
                FileUtils.forceMkdirParent(temp);
            } catch (IOException ignored) {
            }

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                int count;
                byte[] data = new byte[2048];
                while ((count = sevenZFile.read(data)) != -1) {
                    bos.write(data, 0, count);
                }

                FileUtils.write(bos.toByteArray(), temp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void pack(String folder, boolean deleteSource, String pattern) throws Exception {

    }

    @Override
    public List<Resource> list(String pattern, BiConsumer<SevenZFile, SevenZArchiveEntry> consumer) {
        List<Resource> rs = new LinkedList<>();
        File file = toFile();
        try (SevenZFile zFile = new SevenZFile(file)) {
            SevenZArchiveEntry nextEntry = null;
            while ((nextEntry = zFile.getNextEntry()) != null) {
                if (AntPathMatcher.INSTANCE.match(pattern, nextEntry.getName())) {
                    if (null != consumer) {
                        consumer.accept(zFile, nextEntry);
                        continue;
                    }
                    try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                        int count;
                        byte[] data = new byte[2048];
                        while ((count = zFile.read(data)) != -1) {
                            bos.write(data, 0, count);
                        }
                        rs.add(new ByteSourceResource(nextEntry.getName(), new ByteSourceArray(bos.toByteArray())));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    @Override
    public BaseByteSource openInputStream(String name) throws IOException {
        File file = toFile();
        try {
            return new ByteSourceInputStream(new CompressInputStream(file, name));
        } finally {
            if (isTempFile()) {
                FileUtils.delete(file);
            }
        }
    }
}
