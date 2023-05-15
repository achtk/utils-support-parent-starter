package com.chua.common.support.file;

import com.chua.common.support.function.SafeBiConsumer;
import com.chua.common.support.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 压缩
 *
 * @author CH
 */
public abstract class AbstractCompress implements Compress {

    private final List<File> files = new LinkedList<>();
    private final Map<String, InputStream> stream = new LinkedHashMap<>();
    private final Map<String, File> preFile = new LinkedHashMap<>();
    private final Map<String, byte[]> bytes = new LinkedHashMap<>();

    @Override
    public Compress addFile(File file) {
        files.add(file);
        return this;
    }

    @Override
    public Compress addFile(String prefix, File file) {
        preFile.put(prefix, file);
        return this;
    }

    @Override
    public Compress addFile(String name, InputStream stream) {
        this.stream.put(name, stream);
        return this;
    }

    @Override
    public Compress addFile(String name, byte[] bytes) {
        this.bytes.put(name, bytes);
        return this;
    }

    /**
     * 文件
     *
     * @param consumer 消费文件
     */
    protected void withFile(SafeBiConsumer<String, File> consumer) {
        doAnalysisFile(consumer);
        doAnalysisPrefixFile(consumer);
    }

    private void doAnalysisPrefixFile(SafeBiConsumer<String, File> consumer) {
        for (Map.Entry<String, File> entry : preFile.entrySet()) {
            File file = entry.getValue();
            String prefix = entry.getKey();
            if (file.isFile()) {
                consumer.accept(prefix + "/" + file.getName(), file);
                continue;
            }
            String root = file.getPath();
            try {
                Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        String path = file.toFile().getPath();
                        String replace = StringUtils.removePrefix(path.replace(root, ""), File.separator);
                        if (StringUtils.isEmpty(replace)) {
                            return FileVisitResult.CONTINUE;
                        }

                        consumer.accept(prefix + "/" + replace, file.toFile());

                        return super.visitFile(file, attrs);
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        String path = dir.toFile().getPath();
                        String replace = StringUtils.removePrefix(path.replace(root, ""), File.separator);
                        if (StringUtils.isEmpty(replace)) {
                            return FileVisitResult.CONTINUE;
                        }
                        consumer.accept(prefix + "/" + dir.getFileName().toString(), file);
                        return super.preVisitDirectory(dir, attrs);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void doAnalysisFile(SafeBiConsumer<String, File> consumer) {
        for (File file : files) {
            if (file.isFile()) {
                consumer.accept(file.getName(), file);
                continue;
            }
            String root = file.getPath();
            try {
                Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        String path = file.toFile().getPath();
                        String replace = StringUtils.removePrefix(path.replace(root, ""), File.separator);
                        if (StringUtils.isEmpty(replace)) {
                            return FileVisitResult.CONTINUE;
                        }

                        consumer.accept(replace, file.toFile());

                        return super.visitFile(file, attrs);
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        String path = dir.toFile().getPath();
                        String replace = StringUtils.removePrefix(path.replace(root, ""), File.separator);
                        if (StringUtils.isEmpty(replace)) {
                            return FileVisitResult.CONTINUE;
                        }
                        return super.preVisitDirectory(dir, attrs);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 文件
     *
     * @param consumer 消费文件
     */
    protected void withStream(SafeBiConsumer<String, InputStream> consumer) {
        stream.forEach(consumer);
    }

    /**
     * 文件
     *
     * @param consumer 消费文件
     */
    protected void withSource(SafeBiConsumer<String, byte[]> consumer) {
        bytes.forEach(consumer);
    }
}
