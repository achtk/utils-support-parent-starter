package com.chua.common.support.file.transfer;

import com.chua.common.support.binary.ByteSource;
import com.chua.common.support.binary.ByteSourceArray;
import com.chua.common.support.collection.ConcurrentReferenceTable;
import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.collection.Table;
import com.chua.common.support.file.filesystem.OsFileSystem;
import com.chua.common.support.file.transfer.FileConverter;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.FileTypeUtils;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.SneakyThrows;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

import static com.chua.common.support.constant.CommonConstant.FILE_URL_PREFIX;

/**
 * 图片转化
 *
 * @author CH
 */
public abstract class MediaConverter {

    private static final Table<String, String, SortedList<FileConverter>> TABLE = new ConcurrentReferenceTable<>();

    static {
        if (TABLE.isEmpty()) {
            ServiceProvider<FileConverter> serviceProvider =
                    ServiceProvider.of(FileConverter.class);

            serviceProvider.moreEach((k, v) -> {
                String[] source = v.source().split(",");
                String[] target = v.target().split(",");
                for (String s : target) {
                    for (String s1 : source) {
                        SortedList<FileConverter> fileConverters = TABLE.get(s1, s);

                        if (null == fileConverters) {
                            fileConverters = new SortedArrayList<>(Comparator.comparingInt(FileConverter::order));
                        }
                        fileConverters.add(v);
                        TABLE.put(s1, s, fileConverters);
                    }
                }

            });
        }
    }

    protected MediaConverter() {

    }

    /**
     * 是否是可以转化
     * @param source 源
     * @param target 目标
     * @return 是否是可以转化
     */
    public static boolean canConvert(String source, String target) {
        return TABLE.contains(source, target);
    }

    /**
     * 转化
     *
     * @param suffix       輸出類型
     * @param outputStream 流
     */
    public abstract void convert(String suffix, OutputStream outputStream);

    /**
     * 转化
     *
     * @param file 输出
     */
    public abstract void convert(File file);

    /**
     * 初始化
     *
     * @param file 输入
     * @return 对象
     */
    public static MediaConverter of(String file) {
        return of(new File(file));
    }

    /**
     * 初始化
     *
     * @param fileSystem 输入
     * @return 对象
     */
    public static MediaConverter of(OsFileSystem fileSystem) throws IOException {
        return of(fileSystem.openStream());
    }

    /**
     * 初始化
     *
     * @param file 输入
     * @return 对象
     */
    public static MediaConverter of(Path file) {
        return of(file.toFile());
    }

    /**
     * 初始化
     *
     * @param file 输入
     * @return 对象
     */
    public static MediaConverter of(File file) {
        return new FileMediaConverter(file);
    }

    /**
     * 获取转化器
     *
     * @param inputType  输入
     * @param outputType 输出
     * @return 转化器
     */
    public FileConverter findConverter(String inputType, String outputType) {
        SortedList<FileConverter> imageConverters = TABLE.get(inputType, outputType);
        return null == imageConverters || imageConverters.isEmpty() ? null : imageConverters.first();
    }

    /**
     * 初始化
     *
     * @param url 输入
     * @return 对象
     */
    public static MediaConverter of(URL url) {
        try {
            return new InputStreamMediaConverter(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化
     *
     * @param inputStream 输入
     * @return 对象
     */
    public static MediaConverter of(InputStream inputStream) {
        return new InputStreamMediaConverter(inputStream);
    }

    /**
     * 图片
     */
    public static class FileMediaConverter extends MediaConverter {

        private final File file;

        public FileMediaConverter(File file) {
            super();
            this.file = file;
        }

        @Override
        public void convert(String suffix, OutputStream outputStream) {
            String extension = FileUtils.getSimpleExtension(file);
            if (file.isDirectory()) {
                extension = FILE_URL_PREFIX;
            }

            Map<String, SortedList<FileConverter>> row = TABLE.row(extension);
            if (row.isEmpty()) {
                return;
            }

            SortedList<FileConverter> value = row.get(suffix);
            if (null == value) {
                return;
            }

            FileConverter fileConverter = value.first();
            if (null == fileConverter) {
                return;
            }

            try {
                fileConverter.convert(file, suffix, outputStream);
            } catch (Exception e) {
                throw new IllegalStateException("转化异常");
            }
        }

        @Override
        public void convert(File output) {
            String extension = FileUtils.getSimpleExtension(file);
            if (file.isDirectory()) {
                extension = FILE_URL_PREFIX;
            }

            String targetSuffix = FileUtils.getSimpleExtension(output);
            FileConverter fileConverter = findConverter(extension, targetSuffix);
            if (null == fileConverter) {
                return;
            }

            if (file.isDirectory()) {
                try (FileOutputStream stream = new FileOutputStream(output)) {
                    fileConverter.convert(file, FileUtils.getExtension(output), stream);
                } catch (Exception e) {
                    throw new IllegalStateException(extension + "转化" + targetSuffix + "异常");
                }
                return;
            }

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                fileConverter.convert(fileInputStream, output);
            } catch (Exception e) {
                throw new IllegalStateException(extension + "转化" + targetSuffix + "异常");
            }
        }
    }

    /**
     * 流
     */
    public static class InputStreamMediaConverter extends MediaConverter {

        private ByteSource byteSource;

        @SneakyThrows
        @Override
        public void convert(String suffix, OutputStream outputStream) {
            try (InputStream inputStream = byteSource.getInputStream()) {
                String type = FileTypeUtils.getType(inputStream);
                if (StringUtils.isNullOrEmpty(type)) {
                    type = FILE_URL_PREFIX;
                }

                Map<String, SortedList<FileConverter>> row = TABLE.row(type);
                if (row.isEmpty()) {
                    return;
                }

                SortedList<FileConverter> value = row.get(suffix);
                if (null == value) {
                    return;
                }

                FileConverter imageConverter = value.first();
                if (null == imageConverter) {
                    return;
                }
                try (InputStream inputStream1 = byteSource.getInputStream()) {
                    imageConverter.convert(type, inputStream1, suffix, outputStream);
                }
            }
        }

        public InputStreamMediaConverter(InputStream inputStream) {
            super();
            try {
                this.byteSource = new ByteSourceArray(IoUtils.toByteArray(inputStream));
            } catch (IOException ignored) {
            }
        }


        @Override
        @SneakyThrows
        public void convert(File output) {
            InputStream inputStream = byteSource.getInputStream();
            String type;
            try (InputStream inputStream1 = byteSource.getInputStream()) {
                type = FileTypeUtils.getType(inputStream1);
            }
            if (StringUtils.isNullOrEmpty(type)) {
                type = FILE_URL_PREFIX;
            }

            FileConverter fileConverter = findConverter(type, FileUtils.getSimpleExtension(output));
            if (null == fileConverter) {
                return;
            }

            try {
                fileConverter.convert(type, inputStream, FileUtils.getExtension(output), new FileOutputStream(output));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 流
     */
    public static class FileSystemMediaConverter extends MediaConverter {

        private ByteSource byteSource;
        private final OsFileSystem fileSystem;

        public FileSystemMediaConverter(OsFileSystem fileSystem) {
            super();
            this.fileSystem = fileSystem;
            try (InputStream is = fileSystem.openStream()) {
                this.byteSource = new ByteSourceArray(IoUtils.toByteArray(is));
            } catch (IOException ignored) {
            }
        }

        @SneakyThrows
        @Override
        public void convert(String suffix, OutputStream outputStream) {
            String type = fileSystem.suffix();

            Map<String, SortedList<FileConverter>> row = TABLE.row(type);
            if (row.isEmpty()) {
                return;
            }

            SortedList<FileConverter> value = row.get(suffix);
            if (null == value) {
                return;
            }

            FileConverter imageConverter = value.first();
            if (null == imageConverter) {
                return;
            }
            try (InputStream inputStream1 = byteSource.getInputStream()) {
                imageConverter.convert(suffix, inputStream1, suffix, outputStream);
            }
        }


        @Override
        @SneakyThrows
        public void convert(File output) {
            InputStream inputStream = byteSource.getInputStream();
            String type = fileSystem.suffix();
            if (StringUtils.isNullOrEmpty(type)) {
                type = FILE_URL_PREFIX;
            }

            FileConverter fileConverter = findConverter(type, FileUtils.getSimpleExtension(output));
            if (null == fileConverter) {
                return;
            }

            try {
                fileConverter.convert(type, inputStream, FileUtils.getExtension(output), new FileOutputStream(output));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
