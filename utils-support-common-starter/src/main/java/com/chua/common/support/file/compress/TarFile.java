package com.chua.common.support.file.compress;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.binary.BaseByteSource;
import com.chua.common.support.binary.ByteSourceArray;
import com.chua.common.support.binary.ByteSourceInputStream;
import com.chua.common.support.file.AbstractResourceFile;
import com.chua.common.support.file.CompressFile;
import com.chua.common.support.file.ResourceFileConfiguration;
import com.chua.common.support.file.tar.TarEntry;
import com.chua.common.support.file.tar.TarInputStream;
import com.chua.common.support.file.tar.TarOutputStream;
import com.chua.common.support.io.CompressInputStream;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.printer.MapPrinter;
import com.chua.common.support.printer.Printer;
import com.chua.common.support.resource.resource.ByteSourceResource;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_SLASH;

/**
 * tar
 *
 * @author CH
 */
@Spi({"tar"})
@SuppressWarnings("ALL")
public class TarFile extends AbstractResourceFile implements CompressFile<TarInputStream, TarEntry> {
    public TarFile(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration.setType("tar"));
    }

    @Override
    public String printTree() {
        Map tp = new LinkedHashMap<>();
        try (TarInputStream tis = (TarInputStream) openInputStream()) {
            TarEntry entry;
            while ((entry = tis.getNextEntry()) != null) {
                String name = entry.getName();
                CompressFile.mergeTree(tp, name);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new MapPrinter().print(tp, Printer.Type.OUT);
    }

    @Override
    public void forEach(BiConsumer<InputStream, TarEntry> action) {
        try (TarInputStream tis = (TarInputStream) openInputStream()) {
            TarEntry entry;
            while ((entry = tis.getNextEntry()) != null) {
                action.accept(tis, entry);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public InputStream openInputStream() throws IOException {
        return new TarInputStream(new BufferedInputStream(resourceConfiguration.getByteSource().getInputStream()));
    }

    @Override
    public void unpack(String folder, boolean deleteSource, String pattern) throws Exception {
        list(pattern, new BiConsumer<TarInputStream, TarEntry>() {
            @Override
            public void accept(TarInputStream tarInputStream, TarEntry tarEntry) {
                String name = tarEntry.getName();
                if (tarEntry.isDirectory()) {
                    File directory = new File(folder, name);
                    try {
                        FileUtils.forceMkdir(directory);
                    } catch (IOException ignore) {
                    }
                }
                write(tarInputStream, new File(folder, name));
            }
        });
    }

    /**
     * 写入文件
     *
     * @param tis  流
     * @param file 输出
     */
    protected void write(TarInputStream tis, File file) {
        try {
            FileUtils.forceMkdirParent(file);
        } catch (IOException ignored) {
        }

        int count;
        byte[] data = new byte[resourceConfiguration.getBuffer()];
        try (FileOutputStream fos = new FileOutputStream(file)) {
            while ((count = tis.read(data)) != -1) {
                fos.write(data, 0, count);
            }
            fos.flush();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void pack(String folder, boolean deleteSource, String pattern) throws Exception {
        File file = toFile();
        try (TarOutputStream tarOutputStream = new TarOutputStream(Files.newOutputStream(file.toPath()))) {
            File fileToZip = new File(folder);
            compressTarFile(file, fileToZip, fileToZip, "", tarOutputStream);
        } finally {
            if (isTempFile() || deleteSource) {
                FileUtils.delete(file);
            }
        }
    }

    /**
     * 遍历压缩
     *
     * @param source          源文件
     * @param fileToZip       源目录
     * @param fileName        文件名
     * @param tarOutputStream 输出目录
     * @throws IOException ex
     */
    protected void compressTarFile(File source, File fileToZip, File root, String fileName, TarOutputStream tarOutputStream) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }

        if (fileToZip.isDirectory()) {
            if (!StringUtils.isNullOrEmpty(fileName)) {
                if (fileName.endsWith(SYMBOL_LEFT_SLASH)) {
                    tarOutputStream.putNextEntry(new TarEntry(new File(fileName), fileName));
                } else {
                    String s = fileName + SYMBOL_LEFT_SLASH;
                    tarOutputStream.putNextEntry(new TarEntry(new File(s), s));
                }
            }
            File[] children = fileToZip.listFiles();
            if (null != children) {
                for (File childFile : children) {
                    if (childFile.getAbsolutePath().equals(source.getAbsolutePath())) {
                        continue;
                    }
                    compressTarFile(source, childFile, fileToZip, StringUtils.isNullOrEmpty(fileName) ? childFile.getName() : fileName + SYMBOL_LEFT_SLASH + childFile.getName(), tarOutputStream);
                }
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            tarOutputStream.putNextEntry(new TarEntry(fileToZip, fileToZip.getName()));
            byte[] bytes = new byte[4096];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                tarOutputStream.write(bytes, 0, length);
            }
        }
    }

    @Override
    public List<Resource> list(String pattern, BiConsumer<TarInputStream, TarEntry> consumer) {
        List<Resource> rs = new LinkedList<>();
        File file = toFile();
        try (TarInputStream tis = (TarInputStream) openInputStream()) {
            TarEntry entry;
            while ((entry = tis.getNextEntry()) != null) {
                if (StringUtils.isNullOrEmpty(pattern) || PathMatcher.INSTANCE.match(pattern, entry.getName())) {
                    if (null != consumer) {
                        consumer.accept(tis, entry);
                        continue;
                    }
                    rs.add(new ByteSourceResource(
                            entry.getName(), new ByteSourceArray(createByteArray(tis))));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    /**
     * 写入文件
     *
     * @param tis 流
     */
    private byte[] createByteArray(TarInputStream tis) {
        int count;
        byte[] data = new byte[resourceConfiguration.getBuffer()];
        try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
            while ((count = tis.read(data)) != -1) {
                fos.write(data, 0, count);
            }
            fos.flush();
            return fos.toByteArray();
        } catch (Exception ignored) {
        }

        return new byte[0];
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
