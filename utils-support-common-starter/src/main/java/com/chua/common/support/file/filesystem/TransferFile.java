package com.chua.common.support.file.filesystem;

import com.chua.common.support.file.transfer.BaseMediaConverter;
import com.chua.common.support.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;

/**
 * 转化文件
 *
 * @author CH
 */
public interface TransferFile {

    /**
     * 转化
     *
     * @return 对象
     * @throws IOException ex
     */
    File transfer() throws IOException;

    /**
     * 转化
     *
     * @param outFile 輸出目錄
     * @return 对象
     * @throws IOException ex
     */
    File transfer(String outFile) throws IOException;

    /**
     * 对象转化
     *
     * @param suffix 目标类型
     * @return 结果
     * @throws IOException ex
     */
    BaseOsFileSystem transferFileSystem(String suffix) throws IOException;

    /**
     * 对象转化
     *
     * @param outFile 目标类型
     * @return 结果
     * @throws IOException ex
     */
    File transferFile(String outFile) throws IOException;

    class FileTransferFile implements TransferFile {

        private final BaseOsFileSystem fileSystem;

        public FileTransferFile(BaseOsFileSystem fileSystem) {
            this.fileSystem = fileSystem;
        }

        @Override
        public File transfer() throws IOException {
            String suffix = fileSystem.suffix();
            Path path = Files.createTempFile("transfer_", "." + suffix);
            try {
                Files.copy(fileSystem.openStream(), path);
            } catch (FileAlreadyExistsException ignored) {
                Files.copy(fileSystem.openStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            return path.toFile();
        }

        @Override
        public File transfer(String outFile) throws IOException {
            Path path = Paths.get(outFile);
            FileUtils.forceMkdirParent(path.toFile());
            try {
                Files.copy(fileSystem.openStream(), path);
            } catch (FileAlreadyExistsException ignored) {
                Files.copy(fileSystem.openStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            return path.toFile();
        }

        @Override
        public BaseOsFileSystem transferFileSystem(String suffix) throws IOException {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                BaseMediaConverter.of(fileSystem.openStream()).convert(suffix, outputStream);
                return BaseOsFileSystem.open(outputStream.toByteArray());
            }
        }

        @Override
        public File transferFile(String outFile) throws IOException {
            File file = new File(outFile);
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                BaseMediaConverter.of(fileSystem).convert(FileUtils.getExtension(outFile), outputStream);
            }
            return file;
        }

    }
}
