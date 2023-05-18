package com.chua.common.support.file.transfer;

import com.chua.common.support.lang.process.ProgressBar;
import com.chua.common.support.utils.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * folder - zip
 *
 * @author CH
 */
public class FolderToZipFileConverter extends AbstractFileConverter {

    @Override
    public void convert(InputStream inputStream, String suffix, OutputStream outputStream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void convert(InputStream inputStream, File output) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void convert(File sourceFile, String suffix, OutputStream output) {
        boolean clearParent = getBooleanValue("clear", false);
        //声明输出zip流
        try (ZipOutputStream out = createStream(output)) {

            String clearParentPath = "";
            if (clearParent) {
                clearParentPath = sourceFile.getName();
            }
            try (ProgressBar process = new ProgressBar("进度", 1)) {
                putEntity("", sourceFile, out, getIntValue("byteLength", 2048), process, clearParentPath);
            } finally {
                try {
                    out.finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 转化流
     *
     * @param outputStream 目标
     * @return 结果
     * @throws IOException
     */
    protected ZipOutputStream createStream(OutputStream outputStream) throws IOException {
        Object checksum = getObject("checksum");
        if (checksum instanceof Checksum) {
            return new ZipOutputStream(new BufferedOutputStream(new CheckedOutputStream(outputStream, (Checksum) checksum)));
        }
        return new ZipOutputStream(outputStream);
    }

    /**
     * 放入文件
     *
     * @param base            基础目录
     * @param file            文件
     * @param out             输出
     * @param line            位置
     * @param process         经度
     * @param clearParentPath 父目录
     */
    private void putEntity(String base, File file, ZipOutputStream out, int line, ProgressBar process, String clearParentPath) {
        compress(file, out, base, line, process, clearParentPath);
    }

    /**
     * 创建文件
     *
     * @param base 目录
     * @return 文件
     */
    protected ZipEntry createFile(String base) {
        return new ZipEntry(base);
    }

    /**
     * 创建文件
     *
     * @param base 目录
     * @return 文件
     */
    protected ZipEntry createFolder(String base) {
        return new ZipEntry(base);
    }


    /**
     * 判断传参类型:是目录还是文件
     * <p>
     * 1.如果是文件,则调用压缩文件方法
     * 2.如果是目录,则调用压缩目录方法
     * </p>
     *
     * @param file            文件
     * @param out             输出
     * @param basedir         根目录
     * @param line            字节长度
     * @param process         进度
     * @param clearParentPath 清除父目录
     */
    private void compress(File file, ZipOutputStream out, String basedir, int line, ProgressBar process, String clearParentPath) {
        if (file.isDirectory()) {
            //调用压缩目录方法
            this.compressDirectory(file, out, basedir, line, process, clearParentPath);
        } else {
            //调用压缩文件方法
            this.compressFile(file, out, basedir, line, process, clearParentPath);
        }
    }

    /**
     * 压缩一个目录
     *
     * @param dir             目录
     * @param out             zip输出流
     * @param basedir         基础路径前缀  例如: 第一层 “” 第二层 /
     * @param line            字节长度
     * @param process         进度
     * @param clearParentPath 清除父目录
     */
    private void compressDirectory(File dir, ZipOutputStream out, String basedir, int line, ProgressBar process, String clearParentPath) {
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        if (null == files) {
            return;
        }
        process.stepBy(files.length);
        for (File file : files) {
            //名称
            String name = basedir + dir.getName() + "/";
            if (!StringUtils.isNullOrEmpty(clearParentPath)) {
                name = StringUtils.startWithMove(name, clearParentPath);
            }
            /* 递归 */
            compress(file, out, name, line, process, clearParentPath);
        }
    }

    /**
     * 压缩一个文件
     *
     * @param file            文件
     * @param out             zip输出流
     * @param basedir         基础路径前缀  例如: 第一层 “” 第二层 /
     * @param line            字节长度
     * @param process         进度
     * @param clearParentPath 清除父目录
     */
    private void compressFile(File file, ZipOutputStream out, String basedir, int line, ProgressBar process, String clearParentPath) {
        if (!file.exists()) {
            System.out.println("压缩文件不存在,请核实！");
            return;
        }
        process.step();
        try (BufferedInputStream bis = new BufferedInputStream(
                Files.newInputStream(file.toPath()))) {
            ZipEntry entry = new ZipEntry(StringUtils.startWithMove(basedir + file.getName(), "/"));
            out.putNextEntry(entry);
            int count;
            byte[] data = new byte[line];
            while ((count = bis.read(data)) != -1) {
                out.write(data, 0, count);
            }
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String target() {
        return "zip";
    }

    @Override
    public String source() {
        return "file:";
    }
}
