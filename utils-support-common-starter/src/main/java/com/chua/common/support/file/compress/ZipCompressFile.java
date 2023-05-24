package com.chua.common.support.file.compress;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.binary.ByteSource;
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
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_SLASH;

/**
 * @author CH
 */
@Spi({"zip"})
public class ZipCompressFile extends AbstractResourceFile implements CompressFile<ZipFile, ZipEntry> {
    public ZipCompressFile(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration);
    }

    public ZipCompressFile(File file) {
        super(ResourceFileConfiguration.builder().source(file).type("zip").build());
    }

    @Override
    public String printTree() {
        Map tp = new LinkedHashMap<>();
        try (ZipInputStream zipInputStream = new ZipInputStream(openInputStream())) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();
                CompressFile.mergeTree(tp, name);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void forEach(BiConsumer<InputStream, ZipEntry> action) {
        try (ZipInputStream zipInputStream = new ZipInputStream(openInputStream())) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                action.accept(zipInputStream, entry);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void unpack(String folder, boolean deleteSource, String pattern) throws Exception {
        File file = toFile();
        try {
            list(pattern, (zipFile, zipEntry) -> {
                String name = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    File directory = new File(folder, name);
                    try {
                        FileUtils.forceMkdir(directory);
                    } catch (IOException ignored) {
                    }
                }
                try (InputStream is = zipFile.getInputStream(zipEntry)) {
                    write(is, new File(folder, name));
                } catch (IOException ignored) {
                }
            });

        } finally {
            if (isTempFile() || deleteSource) {
                FileUtils.delete(file);
            }
        }
    }

    @Override
    public void pack(String folder, boolean deleteSource, String pattern) throws Exception {
        File file = toFile();
        try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(file.toPath()))) {
            File fileToZip = new File(folder);
            compressZipFile(file, fileToZip, fileToZip, "", zipOut);
        } finally {
            if (isTempFile() || deleteSource) {
                FileUtils.delete(file);
            }
        }
    }

    @Override
    public List<Resource> list(String pattern, BiConsumer<ZipFile, ZipEntry> consumer) {
        List<Resource> rs = new LinkedList<>();
        File file = toFile();
        try (ZipFile zipFile = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            ZipEntry zipEntry;
            while (entries.hasMoreElements()) {
                zipEntry = entries.nextElement();
                if (StringUtils.isNullOrEmpty(pattern) || AntPathMatcher.INSTANCE.match(pattern, zipEntry.getName())) {
                    if (null != consumer) {
                        consumer.accept(zipFile, zipEntry);
                        continue;
                    }
                    rs.add(new ByteSourceResource(zipEntry.getName(),
                            new ByteSourceArray(IoUtils.toByteArray(zipFile.getInputStream(zipEntry)))));
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (isTempFile()) {
                try {
                    FileUtils.delete(file);
                } catch (IOException ignored) {
                }
            }
        }

        return rs;
    }

    @Override
    public ByteSource openInputStream(String name) throws IOException {
        File file = toFile();
        try {
            return new ByteSourceInputStream(new CompressInputStream(file, name));
        } finally {
            if (isTempFile()) {
                FileUtils.delete(file);
            }
        }
    }

    /**
     * 遍历压缩
     *
     * @param source    源文件
     * @param fileToZip 源目录
     * @param fileName  文件名
     * @param zipOut    输出目录
     * @throws IOException ex
     */
    private void compressZipFile(File source, File fileToZip, File root, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }

        if (fileToZip.isDirectory()) {
            if (!StringUtils.isNullOrEmpty(fileName)) {
                if (fileName.endsWith(SYMBOL_LEFT_SLASH)) {
                    zipOut.putNextEntry(new ZipEntry(fileName));
                    zipOut.closeEntry();
                } else {
                    zipOut.putNextEntry(new ZipEntry(fileName + SYMBOL_LEFT_SLASH));
                    zipOut.closeEntry();
                }
            }
            File[] children = fileToZip.listFiles();
            if (null != children) {
                for (File childFile : children) {
                    if (childFile.getAbsolutePath().equals(source.getAbsolutePath())) {
                        continue;
                    }
                    compressZipFile(source, childFile, fileToZip, StringUtils.isNullOrEmpty(fileName) ? childFile.getName() : fileName + SYMBOL_LEFT_SLASH + childFile.getName(), zipOut);
                }
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[4096];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }

    /**
     * 生成文件
     *
     * @param inputStream 流
     * @param file        文件
     */
    protected void write(InputStream inputStream, File file) {
        try {
            FileUtils.forceMkdirParent(file);
        } catch (IOException ignored) {
        }

        int size;
        byte[] buffer = new byte[resourceConfiguration.getBuffer()];

        try (FileOutputStream fos = new FileOutputStream(file)) {
            while ((size = inputStream.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, size);
            }
            fos.flush();
        } catch (Exception ignored) {
        }
    }

}
