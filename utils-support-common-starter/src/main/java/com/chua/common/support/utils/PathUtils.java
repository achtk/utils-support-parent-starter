package com.chua.common.support.utils;


import com.chua.common.support.constant.CommonConstant;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_DOT;

/**
 * 路径工具
 *
 * @author CH
 */
public class PathUtils {

    /**
     * Empty {@link CopyOption} array.
     *
     * @since 2.8.0
     */
    public static final CopyOption[] EMPTY_COPY_OPTIONS = {};

    /**
     * Empty {@link FileVisitOption} array.
     */
    public static final FileVisitOption[] EMPTY_FILE_VISIT_OPTION_ARRAY = {};

    /**
     * Empty {@link LinkOption} array.
     */
    public static final LinkOption[] EMPTY_LINK_OPTION_ARRAY = {};

    /**
     * {@link LinkOption} array for {@link LinkOption#NOFOLLOW_LINKS}.
     *
     * @since 2.9.0
     */
    public static final LinkOption[] NOFOLLOW_LINK_OPTION_ARRAY = {LinkOption.NOFOLLOW_LINKS};

    /**
     * Empty {@link OpenOption} array.
     */
    public static final OpenOption[] EMPTY_OPEN_OPTION_ARRAY = {};

    /**
     * Empty {@link Path} array.
     *
     * @since 2.9.0
     */
    public static final Path[] EMPTY_PATH_ARRAY = {};
    public static final String SLASH = CommonConstant.SYMBOL_LEFT_SLASH;

    /**
     * 规范化
     *
     * @param path 路径
     * @return {@link String}
     */
    public static String normalize(String path) {
        String newPath = Paths.get(path).normalize().toString();
        if (newPath.isEmpty()) {
            return SLASH;
        }
        if (!newPath.startsWith(SLASH)) {
            newPath = SLASH + newPath;
        }
        return newPath;
    }

    /**
     * 常量前缀
     *
     * @param normalizePath 规范化路径
     * @return {@link String}
     */
    public static String constantPrefix(String normalizePath) {
        int slashIndex = 0;
        boolean foundWildcardChar = false;
        for (int i = 0; i < normalizePath.length(); ++i) {
            char c = normalizePath.charAt(i);
            if (c == '*' || c == '?' || c == '{') {
                foundWildcardChar = true;
                break;
            } else if (c == '/') {
                slashIndex = i;
            }
        }
        if (!foundWildcardChar) {
            return normalizePath;
        }
        if (slashIndex == 0) {
            return SLASH;
        }
        return normalizePath.substring(0, slashIndex);
    }

    /**
     * 删除最后一个
     *
     * @param normalizePath 规范化路径
     * @return {@link String}
     */
    public static String removeLast(String normalizePath) {
        if ("".equals(normalizePath)) {
            return "";
        }
        int index = normalizePath.length() - 1;
        while (index > 0 && normalizePath.charAt(index) != '/') {
            --index;
        }
        return normalizePath.substring(0, index);
    }

    /**
     * 获取第一部分
     *
     * @param normalizePath 规范化路径
     * @return {@link String}
     */
    public static String getFirstPart(String normalizePath) {
        final int secondSlashIndex = normalizePath.indexOf(SLASH, 1);
        if (secondSlashIndex == -1) {
            return normalizePath;
        } else {
            return normalizePath.substring(0, secondSlashIndex);
        }
    }

    /**
     * 删除目录
     *
     * @param path        目录
     * @param linkOptions 连接参数
     * @throws IOException
     */
    public static void delete(final Path path, final LinkOption[] linkOptions) throws IOException {
        if (Files.isDirectory(path, linkOptions)) {
            deleteDirectory(path, linkOptions);
            return;
        }
        deleteFile(path, linkOptions);
    }

    /**
     * 是否是空目录
     *
     * @param directory 目录
     * @return 是否是空目录
     * @throws IOException 异常
     */
    public static boolean isEmptyDirectory(final Path directory) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            return !directoryStream.iterator().hasNext();
        }
    }

    /**
     * 删除目录
     *
     * @param directory   目录
     * @param linkOptions 连接参数
     * @throws IOException 异常
     */
    public static void deleteDirectory(final Path directory, final LinkOption[] linkOptions) throws IOException {
       Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
           @Override
           public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
               Files.delete(file);
               return super.visitFile(file, attrs);
           }
       });
    }

    /**
     * Performs {@link Files#walkFileTree(Path, FileVisitor)} and returns the given visitor.
     * <p>
     * Note that {@link Files#walkFileTree(Path, FileVisitor)} returns the given path.
     *
     * @param visitor   See {@link Files#walkFileTree(Path, FileVisitor)}.
     * @param directory See {@link Files#walkFileTree(Path, FileVisitor)}.
     * @param <T>       See {@link Files#walkFileTree(Path, FileVisitor)}.
     * @return the given visitor.
     * @throws IOException if an I/O error is thrown by a visitor method
     */
    public static <T extends FileVisitor<? super Path>> T visitFileTree(final T visitor, final Path directory)
            throws IOException {
        Files.walkFileTree(directory, visitor);
        return visitor;
    }

    /**
     * 删除文件
     *
     * @param file        文件
     * @param linkOptions 连接参数
     * @throws NoSuchFileException 文件不存在
     * @throws IOException         删除异常
     */
    public static void deleteFile(final Path file, final LinkOption[] linkOptions) throws NoSuchFileException, IOException {
        if (Files.isDirectory(file, linkOptions)) {
            throw new NoSuchFileException(file.toString());
        }
        final boolean exists = Files.exists(file, linkOptions);
        final long size = exists && !Files.isSymbolicLink(file) ? Files.size(file) : 0;
        if (overrideReadOnly() && exists) {
            setReadOnly(file, false, linkOptions);
            Files.delete(file);
        }
    }

    /**
     * 设置只读参数
     *
     * @param path        目录
     * @param readOnly    是否只读
     * @param linkOptions 连接参数
     * @return 目录
     * @throws IOException 异常
     */
    public static Path setReadOnly(final Path path, final boolean readOnly, final LinkOption... linkOptions)
            throws IOException {
        final List<Exception> causeList = new ArrayList<>(2);
        final DosFileAttributeView fileAttributeView = Files.getFileAttributeView(path, DosFileAttributeView.class,
                linkOptions);
        if (fileAttributeView != null) {
            try {
                fileAttributeView.setReadOnly(readOnly);
                return path;
            } catch (final IOException e) {
                // ignore for now, retry with PosixFileAttributeView
                causeList.add(e);
            }
        }
        final PosixFileAttributeView posixFileAttributeView = Files.getFileAttributeView(path,
                PosixFileAttributeView.class, linkOptions);
        if (posixFileAttributeView != null) {
            final PosixFileAttributes readAttributes = posixFileAttributeView.readAttributes();
            final Set<PosixFilePermission> permissions = readAttributes.permissions();
            permissions.remove(PosixFilePermission.OWNER_WRITE);
            permissions.remove(PosixFilePermission.GROUP_WRITE);
            permissions.remove(PosixFilePermission.OTHERS_WRITE);
            try {
                return Files.setPosixFilePermissions(path, permissions);
            } catch (final IOException e) {
                causeList.add(e);
            }
        }
        if (!causeList.isEmpty()) {
            throw new IllegalStateException(path.toString());
        }
        throw new IOException(
                String.format("No DosFileAttributeView or PosixFileAttributeView for '%s' (linkOptions=%s)", path,
                        Arrays.toString(linkOptions)));
    }

    /**
     * 是否删除只读
     *
     * @return 是否删除只读
     */
    private static boolean overrideReadOnly() {
        return true;
    }

    /**
     * 创建文件
     *
     * @param pageCount  页面数量
     * @param pageIndex  当前页码
     * @param targetPath 目标文件
     * @param type       类型
     * @return 文件
     */
    public static File createFile(int pageCount, int pageIndex, String targetPath, String type) {
        if (pageCount == 1 && targetPath.contains(SYMBOL_DOT)) {
            return new File(targetPath);
        }
        return new File(targetPath + "-" + pageIndex + SYMBOL_DOT + type);
    }

    /**
     * 创建文件
     *
     * @param out        输出
     * @param targetPath 输出
     */
    public static void createFile(ByteArrayOutputStream out, OutputStream targetPath) {
        try {
            targetPath.write(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取属性
     *
     * @param path 属性
     * @return 属性
     */
    public static BasicFileAttributes readBasicFileAttributesUnchecked(final Path path) {
        try {
            return readBasicFileAttributes(path);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 获取属性
     *
     * @param path 属性
     * @return 属性
     */
    public static BasicFileAttributes readBasicFileAttributes(final Path path) throws IOException {
        return Files.readAttributes(path, BasicFileAttributes.class);
    }
}
