package com.chua.common.support.utils;

import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.media.MediaTypeFactory;
import com.chua.common.support.unit.size.CapacitySize;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chua.common.support.constant.CommonConstant.*;
import static com.chua.common.support.constant.NumberConstant.SECOND;
import static com.chua.common.support.utils.IoUtils.toCharset;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 文件工具
 *
 * @author CH
 * @version 1.0.0
 * @since 2021-07-13
 */
public class FileUtils {

    private static final String[] ZIP_COMPRESS = new String[]{
            "zip", "jar"
    };
    private static final String[] COMPRESS = new String[]{
            "zip", "jar", "tar", "xz", "tar.xz", "zip.xz", "zip.gz", "tar.gz"
    };


    /**
     * 可读的文件大小
     *
     * @param file 文件
     * @return 大小
     */
    public static String readableFileSize(File file) {
        return readableFileSize(file.length());
    }

    /**
     * 可读的文件大小<br>
     * 参考 http://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc
     *
     * @param size Long类型大小
     * @return 大小
     * @see CapacitySize#format(long)
     */
    public static String readableFileSize(long size) {
        return CapacitySize.format(size);
    }

    /**
     * 获得一个输出流对象
     *
     * @param file 文件
     * @return 输出流对象
     * @throws IOException IO异常
     */
    public static BufferedOutputStream getOutputStream(File file) throws IOException {
        final OutputStream out;
        try {
            out = Files.newOutputStream(touch(file).toPath());
        } catch (IOException e) {
            throw new IOException(e);
        }
        return IoUtils.toBuffered(out);
    }

    /**
     * 创建文件
     *
     * @param inputStream 流
     * @param file        文件
     * @return 文件
     */
    public static File createFile(InputStream inputStream, File file) {
        if (null == inputStream) {
            return null;
        }
        try {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            FileUtils.write(buffer, file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 删除文件
     *
     * @param prefix 前缀
     * @param suffix 后缀
     */
    public static void delete(final String prefix, String... suffix) {
        for (String s : suffix) {
            File file = new File(prefix + s);
            if (!file.exists()) {
                continue;
            }

            try {
                delete(file);
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return 删除的文件
     * @throws IOException 异常
     */
    public static File delete(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        Files.delete(file.toPath());
        return file;
    }

    /**
     * 删除目录
     *
     * @param directory 目录
     * @throws IOException              in case deletion is unsuccessful
     * @throws IllegalArgumentException if {@code directory} is not a directory
     */
    public static void deleteDirectory(final Path directory) throws IOException {
        Objects.requireNonNull(directory, "directory");
        if (!Files.exists(directory)) {
            return;
        }
        if (!isSymlink(directory)) {
            cleanDirectory(directory.toFile());
        }
        delete(directory.toFile());
    }

    /**
     * 删除目录
     *
     * @param directory 目录
     * @throws IOException              in case deletion is unsuccessful
     * @throws IllegalArgumentException if {@code directory} is not a directory
     */
    public static void deleteDirectory(final File directory) throws IOException {
        Objects.requireNonNull(directory, "directory");
        if (!directory.exists()) {
            return;
        }
        if (!isSymlink(directory)) {
            cleanDirectory(directory);
        }
        delete(directory);
    }

    /**
     * Cleans a directory without deleting it.
     *
     * @param directory directory to clean
     * @throws NullPointerException     if the given {@code File} is {@code null}.
     * @throws IllegalArgumentException if directory does not exist or is not a directory.
     * @throws IOException              if an I/O error occurs.
     * @see #forceDelete(File)
     */
    public static void cleanDirectory(final File directory) throws IOException {
        final File[] files = listFiles(directory, null);

        final List<Exception> causeList = new ArrayList<>();
        for (final File file : files) {
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                causeList.add(ioe);
            }
        }

        if (!causeList.isEmpty()) {
            throw new IOException(directory.toString());
        }
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @throws IOException 删除异常
     */
    public static void forceDelete(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        try {
            PathUtils.delete(file.toPath(), PathUtils.EMPTY_LINK_OPTION_ARRAY);
        } catch (final IOException e) {
            throw new IOException("Cannot delete file: " + file, e);
        }
    }

    /**
     * 遍历文件
     *
     * @param directory  目录
     * @param fileFilter 文件过滤器
     * @return 文件
     * @throws IOException 异常
     */
    private static File[] listFiles(final File directory, final FileFilter fileFilter) throws IOException {
        requireDirectoryExists(directory, "directory");
        final File[] files = fileFilter == null ? directory.listFiles() : directory.listFiles(fileFilter);
        if (files == null) {
            // null if the directory does not denote a directory, or if an I/O error occurs.
            throw new IOException("Unknown I/O error listing contents of directory: " + directory);
        }
        return files;
    }

    /**
     * 判断文件存在
     *
     * @param directory 目录
     * @param name      名称
     * @return 文件
     */
    private static File requireDirectoryExists(final File directory, final String name) {
        requireExists(directory, name);
        requireDirectory(directory, name);
        return directory;
    }

    /**
     * 判断目录必须存在
     *
     * @param directory 目录
     * @param name      文件名
     * @return 文件存在返回文件
     */
    private static File requireDirectory(final File directory, final String name) {
        Objects.requireNonNull(directory, name);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Parameter '" + name + "' is not a directory: '" + directory + "'");
        }
        return directory;
    }

    /**
     * 判断文件必须存在
     *
     * @param file          文件
     * @param fileParamName 文件名
     * @return 文件存在返回文件
     */
    private static File requireExists(final File file, final String fileParamName) {
        Objects.requireNonNull(file, fileParamName);
        if (!file.exists()) {
            throw new IllegalArgumentException(
                    "File system element for parameter '" + fileParamName + "' does not exist: '" + file + "'");
        }
        return file;
    }

    /**
     * Tests whether the specified file is a symbolic link rather than an actual file.
     * <p>
     * This method delegates to {@link Files#isSymbolicLink(Path path)}
     * </p>
     *
     * @param file the file to test.
     * @return true if the file is a symbolic link, see {@link Files#isSymbolicLink(Path path)}.
     * @see Files#isSymbolicLink(Path)
     * @since 2.0
     */
    public static boolean isSymlink(final File file) {
        return file != null && Files.isSymbolicLink(file.toPath());
    }

    /**
     * Tests whether the specified file is a symbolic link rather than an actual file.
     * <p>
     * This method delegates to {@link Files#isSymbolicLink(Path path)}
     * </p>
     *
     * @param file the file to test.
     * @return true if the file is a symbolic link, see {@link Files#isSymbolicLink(Path path)}.
     * @see Files#isSymbolicLink(Path)
     * @since 2.0
     */
    public static boolean isSymlink(final Path file) {
        return file != null && Files.isSymbolicLink(file);
    }

    /**
     * 创建父目录
     *
     * @param file 文件
     * @throws IOException 异常
     */
    public static void forceMkdirParent(File file) throws IOException {
        Objects.requireNonNull(file, "file");
        File parent = getParentFile(file);
        if (parent != null) {
            forceMkdir(parent);
        }
    }

    /**
     * 父目录
     *
     * @param file 文件
     * @return 父目录
     */
    private static File getParentFile(File file) {
        return file == null ? null : file.getParentFile();
    }

    /**
     * 创建目录
     *
     * @param directory 目录
     * @throws IOException 异常
     */
    public static void forceMkdir(File directory) throws IOException {
        mkdirs(directory);
    }

    /**
     * 创建目录
     *
     * @param directory 目录
     * @throws IOException 异常
     */
    private static File mkdirs(File directory) throws IOException {
        if (directory != null && !directory.mkdirs() && !directory.isDirectory()) {
            throw new IOException("Cannot create directory '" + directory + "'.");
        } else {
            return directory;
        }
    }

//    /**
//     * 文件大小
//     *
//     * @param bytes 文件大小
//     * @return 文件大小
//     */
//    public static String getSize(long bytes, boolean format) {
//        int unit = 1024;
//        int exp = (int) (Math.log(bytes) / Math.log(unit));
//        double pre = 0;
//        if (bytes > 1024) {
//            pre = bytes / Math.pow(unit, exp);
//        } else if (bytes > 0) {
//            pre = (double) bytes / (double) unit;
//        } else {
//            pre = 0;
//            exp = 0;
//        }
//        if (format) {
//            try {
//                return String.format(Locale.ENGLISH, "%.1f%s", pre, units[(int) exp]);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return String.format(Locale.ENGLISH, "%.1f", pre);
//    }


    /**
     * 是否是文件夹
     *
     * @param local 路径
     * @return 是文件夹返回true
     */
    public static boolean isFolder(String local) {
        return file(local).isDirectory();
    }

    /**
     * 是否是文件
     *
     * @param local 路径
     * @return 是文件返回true
     */
    public static boolean isFile(String local) {
        return file(local).isFile();
    }

    /**
     * 获取文件
     *
     * @param local 路径
     * @return File
     */
    public static File file(String local) {
        return new File(getAbsolutePath(local));
    }

    /**
     * 获取绝对路径，相对于ClassPath的目录<br>
     * 如果给定就是绝对路径，则返回原路径，原路径把所有\替换为/<br>
     * 兼容Spring风格的路径表示，例如：classpath:config/example.setting也会被识别后转换
     *
     * @param path 相对路径
     * @return 绝对路径
     */
    public static String getAbsolutePath(String path) {
        return getAbsolutePath(path, null);
    }

    /**
     * 获取绝对路径<br>
     * 此方法不会判定给定路径是否有效（文件或目录存在）
     *
     * @param path      相对路径
     * @param baseClass 相对路径所相对的类
     * @return 绝对路径
     */
    public static String getAbsolutePath(String path, Class<?> baseClass) {
        String normalPath;
        if (path == null) {
            normalPath = SYMBOL_EMPTY;
        } else {
            normalPath = normalize(path);
            if (isAbsolutePath(normalPath)) {
                // 给定的路径已经是绝对路径了
                return normalPath;
            }
        }

        if (null == baseClass) {
            return normalPath;
        }
        // 相对于ClassPath路径
        URL url = null;
        try {
            url = baseClass.getResource(normalPath);
        } catch (Throwable e) {
            return normalPath;
        }
        if (null != url) {
            // 对于jar中文件包含file:前缀，需要去掉此类前缀，在此做标准化，since 3.0.8 解决中文或空格路径被编码的问题
            return FileUtils.normalize(UrlUtils.getDecodedPath(url));
        }

        // 如果资源不存在，则返回一个拼接的资源绝对路径
        final String classPath = ClassUtils.getClassPath();
        if (null == classPath) {
            // throw new NullPointerException("ClassPath is null !");
            // 在jar运行模式中，ClassPath有可能获取不到，此时返回原始相对路径（此时获取的文件为相对工作目录）
            return path;
        }

        // 资源不存在的情况下使用标准化路径有问题，使用原始路径拼接后标准化路径
        return normalize(classPath.concat(Objects.requireNonNull(path)));
    }


    /**
     * 给定路径已经是绝对路径<br>
     * 此方法并没有针对路径做标准化，建议先执行{@link #normalize(String)}方法标准化路径后判断
     *
     * @param path 需要检查的Path
     * @return 是否已经是绝对路径
     */
    public static boolean isAbsolutePath(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }

        // 给定的路径已经是绝对路径了
        return SYMBOL_LEFT_SLASH_CHAR == path.charAt(0) || path.matches("^[a-zA-Z]:([/\\\\].*)?");
    }

    /**
     * 创建File对象<br>
     * 此方法会检查slip漏洞，漏洞说明见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parent 父文件对象
     * @param path   文件路径
     * @return File
     */
    public static File file(File parent, String path) {
        if (StringUtils.isEmpty(path)) {
            throw new NullPointerException("File path is blank!");
        }
        return checkSlip(parent, new File(parent, path));
    }

    /**
     * 检查父完整路径是否为自路径的前半部分，如果不是说明不是子路径，可能存在slip注入。
     * <p>
     * 见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parentFile 父文件或目录
     * @param file       子文件或目录
     * @return 子文件或目录
     * @throws IllegalArgumentException 检查创建的子文件不在父目录中抛出此异常
     */
    public static File checkSlip(File parentFile, File file) throws IllegalArgumentException {
        if (null != parentFile && null != file) {
            String parentCanonicalPath = null;
            String canonicalPath = null;
            try {
                parentCanonicalPath = parentFile.getCanonicalPath();
                canonicalPath = file.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!canonicalPath.startsWith(parentCanonicalPath)) {
                throw new IllegalArgumentException("New file is outside of the parent dir: " + file.getName());
            }
        }
        return file;
    }

    /**
     * 创建文件
     *
     * @param path 文件
     * @return 路径不存在返回null, 否则返回file {@link File}
     */
    public static File newFile(final String path) {
        return null == path ? null : new File(path);
    }

    /**
     * 获取目录下文件
     *
     * @param path      目录
     * @param extension 后缀
     * @return 目录下文件
     */
    public static Collection<String> files(String path, final String extension) {
        if (null == path) {
            return Collections.emptySet();
        }
        File file = newFile(path);
        String[] list = file.list();
        if (StringUtils.isEmpty(extension)) {
            return CollectionUtils.newArrayList(list);
        }
        Set<String> result = new HashSet<>();
        for (String oneFile : Objects.requireNonNull(list)) {
            if (wildcardMatch(oneFile, extension)) {
                result.add(oneFile);
            }
        }

        return result;
    }

    /**
     * 格式化路径
     * <pre>
     * "/foo//" =》 "/foo/"
     * "/foo/./" =》 "/foo/"
     * "/foo/../bar" =》 "/bar"
     * "/foo/../bar/" =》 "/bar/"
     * "/foo/../bar/../baz" =》 "/baz"
     * "/../" =》 "/"
     * "foo/bar/.." =》 "foo"
     * "foo/../bar" =》 "bar"
     * "foo/../../bar" =》 "bar"
     * "//server/foo/../bar" =》 "/server/bar"
     * "//server/../bar" =》 "/bar"
     * "C:\\foo\\..\\bar" =》 "C:/bar"
     * "C:\\..\\bar" =》 "C:/bar"
     * "~/foo/../bar/" =》 "~/bar/"
     * "~/../bar" =》 "bar"
     * </pre>
     *
     * @param path 文件目录
     * @return 格式化路径
     */
    public static String normalize(final String path) {
        if (path == null) {
            return null;
        }


        // 兼容Spring风格的ClassPath路径，去除前缀，不区分大小写
        String pathToUse = StringUtils.removePrefixIgnoreCase(path, CLASSPATH_URL_PREFIX);
        // 去除file:前缀
        pathToUse = StringUtils.removePrefixIgnoreCase(pathToUse, FILE_URL_PREFIX);

        // 识别home目录形式，并转换为绝对路径
        if (pathToUse.startsWith(SYMBOL_WAVY_LINE)) {
            pathToUse = pathToUse.replace(SYMBOL_WAVY_LINE, getUserHomePath());
        }

        // 统一使用斜杠
        pathToUse = pathToUse.replaceAll("[/\\\\]+", SYMBOL_LEFT_SLASH).trim();
        //兼容Windows下的共享目录路径（原始路径如果以\\开头，则保留这种路径）
        if (path.startsWith(SYMBOL_RIGHT_SLASH + SYMBOL_RIGHT_SLASH)) {
            pathToUse = SYMBOL_RIGHT_SLASH + pathToUse;
        }

        String prefix = "";
        int prefixIndex = pathToUse.indexOf(SYMBOL_COLON);
        if (prefixIndex > -1) {
            // 可能Windows风格路径
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (prefix.startsWith(SYMBOL_LEFT_SLASH)) {
                // 去除类似于/C:这类路径开头的斜杠
                prefix = prefix.substring(1);
            }
            if (!prefix.contains(SYMBOL_LEFT_SLASH)) {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            } else {
                // 如果前缀中包含/,说明非Windows风格path
                prefix = SYMBOL_EMPTY;
            }
        }
        if (pathToUse.startsWith(SYMBOL_LEFT_SLASH)) {
            prefix += SYMBOL_LEFT_SLASH;
            pathToUse = pathToUse.substring(1);
        }

        List<String> pathList = Splitter.on(SYMBOL_LEFT_SLASH_CHAR).splitToList(pathToUse);
        List<String> pathElements = new LinkedList<>();
        int tops = 0;

        String element;
        for (int i = pathList.size() - 1; i >= 0; i--) {
            element = pathList.get(i);
            // 只处理非.的目录，即只处理非当前目录
            if (!SYMBOL_DOT.equals(element)) {
                if (SYMBOL_DOUBLE_DOT.equals(element)) {
                    tops++;
                } else {
                    if (tops > 0) {
                        // 有上级目录标记时按照个数依次跳过
                        tops--;
                    } else {
                        // Normal path element found.
                        pathElements.add(0, element);
                    }
                }
            }
        }

        return prefix + Joiner.on(SYMBOL_LEFT_SLASH).join(pathElements);
    }

    /**
     * 文件后缀是否合法
     *
     * @param filename   文件名
     * @param extensions 后缀
     * @return 文件后缀是否合法
     */
    public static boolean isExtension(final String filename, final Collection<String> extensions) {
        if (filename == null) {
            return false;
        }
        failIfNullBytePresent(filename);

        if (extensions == null || extensions.isEmpty()) {
            return indexOfExtension(filename) == INDEX_NOT_FOUND;
        }
        final String fileExt = getExtension(filename);
        for (final String extension : extensions) {
            if (fileExt.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 文件后缀是否合法
     *
     * @param filename  文件名
     * @param extension 后缀
     * @return 文件后缀是否合法
     */
    public static boolean isExtension(final String filename, final String extension) {
        if (filename == null) {
            return false;
        }
        failIfNullBytePresent(filename);

        if (extension == null || extension.isEmpty()) {
            return indexOfExtension(filename) == INDEX_NOT_FOUND;
        }
        final String fileExt = getExtension(filename);
        return fileExt.equals(extension);
    }

    /**
     * 文件后缀是否合法
     *
     * @param filename   文件名
     * @param extensions 后缀
     * @return 文件后缀是否合法
     */
    public static boolean isExtension(final String filename, final String[] extensions) {
        if (filename == null) {
            return false;
        }
        failIfNullBytePresent(filename);

        if (extensions == null || extensions.length == 0) {
            return indexOfExtension(filename) == INDEX_NOT_FOUND;
        }
        final String fileExt = getExtension(filename);
        for (final String extension : extensions) {
            if (fileExt.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取后缀
     *
     * @param file 文件
     * @return 后缀
     */
    public static String getSimpleExtension(final File file) {
        return getSimpleExtension(file.getName());
    }

    /**
     * 获取后缀
     *
     * @param name 文件
     * @return 后缀
     */
    public static String getSimpleExtension(String name) {
        if (null == name) {
            return "";
        }
        int index = name.lastIndexOf("/");
        if (index > -1) {
            name = name.substring(index);
        }
        String[] split = name.split("\\.");
        List<String> rs = new LinkedList<>();
        for (int i = 1; i < split.length; i++) {
            String t = split[i];
            if (NumberUtils.isNumber(t)) {
                continue;
            }
            rs.add(t);
        }
        return Joiner.on(".").join(rs);
    }

    /**
     * 获取后缀
     *
     * @param file 文件
     * @return 后缀
     */
    public static String getExtension(final File file) {
        return null == file ? "" : getExtension(file.getName());
    }

    /**
     * 获取后缀
     *
     * @param url 文件
     * @return 后缀
     */
    public static String getExtension(final URL url) {
        if (null == url) {
            return "";
        }

        if (FILE.equals(url.getProtocol())) {
            return getExtension(url.getFile());
        }

        return MediaTypeFactory.getExtension(url);
    }

    /**
     * 获取后缀
     * <pre>
     * foo.txt      --&gt; "txt"
     * a/b/c.jpg    --&gt; "jpg"
     * a/b.txt/c    --&gt; ""
     * a/b/c        --&gt; ""
     * </pre>
     *
     * @param filename the filename to retrieve the extension of.
     * @return the extension of the file or an empty string if none exists or {@code null}
     * if the filename is {@code null}.
     */
    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index1 = filename.indexOf(JAR_URL_SEPARATOR);
        if (index1 > -1) {
            filename = filename.substring(0, index1);
        }
        final int index = indexOfExtension(filename);
        if (index == INDEX_NOT_FOUND) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    /**
     * 获取基础名称
     * <pre>
     * a/b/c.txt --&gt; c
     * a.txt     --&gt; a
     * a/b/c     --&gt; c
     * a/b/c/    --&gt; ""
     * </pre>
     *
     * @param file the file to query, null returns null
     * @return the name of the file without the path, or an empty string if none exists. Null bytes inside string
     * will be removed
     */
    public static String getBaseName(final File file) {
        if (null == file) {
            return null;
        }
        return getBaseName(file.getName());
    }

    /**
     * 获取基础名称
     * <pre>
     * a/b/c.txt --&gt; c
     * a.txt     --&gt; a
     * a/b/c     --&gt; c
     * a/b/c/    --&gt; ""
     * </pre>
     *
     * @param filename the filename to query, null returns null
     * @return the name of the file without the path, or an empty string if none exists. Null bytes inside string
     * will be removed
     */
    public static String getBaseName(final String filename) {
        return removeExtension(getName(filename));
    }
    //-----------------------------------------------------------------------

    /**
     * 删除后缀
     * foo.txt    --&gt; foo
     * a\b\c.jpg  --&gt; a\b\c
     * a\b\c      --&gt; a\b\c
     * a.b\c      --&gt; a.b\c
     * </pre>
     *
     * @param filename the filename to query, null returns null
     * @return the filename minus the extension
     */
    public static String removeExtension(final String filename) {
        if (filename == null) {
            return null;
        }
        failIfNullBytePresent(filename);

        final int index = indexOfExtension(filename);
        if (index == INDEX_NOT_FOUND) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    /**
     * 获取全路径
     * C:\a\b\c.txt --&gt; C:\a\b\
     * ~/a/b/c.txt  --&gt; ~/a/b/
     * a.txt        --&gt; ""
     * a/b/c        --&gt; a/b/
     * a/b/c/       --&gt; a/b/c/
     * C:           --&gt; C:
     * C:\          --&gt; C:\
     * ~            --&gt; ~/
     * ~/           --&gt; ~/
     * ~user        --&gt; ~user/
     * ~user/       --&gt; ~user/
     * </pre>
     *
     * @param filename the filename to query, null returns null
     * @return the path of the file, an empty string if none exists, null if invalid
     */
    public static String getFullPath(final String filename) {
        return doGetFullPath(filename, true);
    }

    /**
     * 获取路径
     * <pre>
     * C:\a\b\c.txt --&gt; a\b\
     * ~/a/b/c.txt  --&gt; a/b/
     * a.txt        --&gt; ""
     * a/b/c        --&gt; a/b/
     * a/b/c/       --&gt; a/b/c/
     * </pre>
     *
     * @param filename the filename to query, null returns null
     * @return the path of the file, an empty string if none exists, null if invalid.
     * Null bytes inside string will be removed
     */
    public static String getPath(final String filename) {
        return doGetPath(filename, 1);
    }

    /**
     * 获取前缀
     * <pre>
     * Windows:
     * a\b\c.txt           --&gt; ""          --&gt; relative
     * \a\b\c.txt          --&gt; "\"         --&gt; current drive absolute
     * C:a\b\c.txt         --&gt; "C:"        --&gt; drive relative
     * C:\a\b\c.txt        --&gt; "C:\"       --&gt; absolute
     * \\server\a\b\c.txt  --&gt; "\\server\" --&gt; UNC
     *
     * Unix:
     * a/b/c.txt           --&gt; ""          --&gt; relative
     * /a/b/c.txt          --&gt; "/"         --&gt; absolute
     * ~/a/b/c.txt         --&gt; "~/"        --&gt; current user
     * ~                   --&gt; "~/"        --&gt; current user (slash added)
     * ~user/a/b/c.txt     --&gt; "~user/"    --&gt; named user
     * ~user               --&gt; "~user/"    --&gt; named user (slash added)
     * </pre>
     *
     * @param filename the filename to query, null returns null
     * @return the prefix of the file, null if invalid. Null bytes inside string will be removed
     */
    public static String getPrefix(final String filename) {
        if (filename == null) {
            return null;
        }
        final int len = getPrefixLength(filename);
        if (len < 0) {
            return null;
        }
        if (len > filename.length()) {
            failIfNullBytePresent(filename + SYMBOL_LEFT_SLASH);
            return filename + SYMBOL_LEFT_SLASH;
        }
        final String path = filename.substring(0, len);
        failIfNullBytePresent(path);
        return path;
    }

    /**
     * Does the work of getting the path.
     *
     * @param filename         the filename
     * @param includeSeparator true to include the end separator
     * @return the path
     */
    private static String doGetFullPath(final String filename, final boolean includeSeparator) {
        if (filename == null) {
            return null;
        }
        final int prefix = getPrefixLength(filename);
        if (prefix < 0) {
            return null;
        }
        if (prefix >= filename.length()) {
            if (includeSeparator) {
                return getPrefix(filename);
            } else {
                return filename;
            }
        }
        final int index = indexOfLastSeparator(filename);
        if (index < 0) {
            return filename.substring(0, prefix);
        }
        int end = index + (includeSeparator ? 1 : 0);
        if (end == 0) {
            end++;
        }
        return filename.substring(0, end);
    }

    /**
     * Returns the length of the filename prefix, such as <code>C:/</code> or <code>~/</code>.
     * <p>
     * This method will handle a file in either Unix or Windows format.
     * <p>
     * The prefix length includes the first slash in the full filename
     * if applicable. Thus, it is possible that the length returned is greater
     * than the length of the input string.
     * <pre>
     * Windows:
     * a\b\c.txt           --&gt; ""          --&gt; relative
     * \a\b\c.txt          --&gt; "\"         --&gt; current drive absolute
     * C:a\b\c.txt         --&gt; "C:"        --&gt; drive relative
     * C:\a\b\c.txt        --&gt; "C:\"       --&gt; absolute
     * \\server\a\b\c.txt  --&gt; "\\server\" --&gt; UNC
     * \\\a\b\c.txt        --&gt;  error, length = -1
     *
     * Unix:
     * a/b/c.txt           --&gt; ""          --&gt; relative
     * /a/b/c.txt          --&gt; "/"         --&gt; absolute
     * ~/a/b/c.txt         --&gt; "~/"        --&gt; current user
     * ~                   --&gt; "~/"        --&gt; current user (slash added)
     * ~user/a/b/c.txt     --&gt; "~user/"    --&gt; named user
     * ~user               --&gt; "~user/"    --&gt; named user (slash added)
     * //server/a/b/c.txt  --&gt; "//server/"
     * ///a/b/c.txt        --&gt; error, length = -1
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * ie. both Unix and Windows prefixes are matched regardless.
     * <p>
     * Note that a leading // (or \\) is used to indicate a UNC name on Windows.
     * These must be followed by a server name, so double-slashes are not collapsed
     * to a single slash at the start of the filename.
     *
     * @param filename the filename to find the prefix in, null returns -1
     * @return the length of the prefix, -1 if invalid or null
     */
    public static int getPrefixLength(final String filename) {
        if (filename == null) {
            return INDEX_NOT_FOUND;
        }
        final int len = filename.length();
        if (len == 0) {
            return 0;
        }
        char ch0 = filename.charAt(0);
        if (ch0 == SYMBOL_COLON_CHAR) {
            return INDEX_NOT_FOUND;
        }
        if (len == 1) {
            if (ch0 == SYMBOL_WAVY_LINE_CHAR) {
                return 2;
            }
            return isSeparator(ch0) ? 1 : 0;
        } else {
            if (ch0 == SYMBOL_WAVY_LINE_CHAR) {
                int posUnix = filename.indexOf(SYMBOL_LEFT_SLASH, 1);
                int posWin = filename.indexOf(SYMBOL_RIGHT_SLASH, 1);
                if (posUnix == INDEX_NOT_FOUND && posWin == INDEX_NOT_FOUND) {
                    return len + 1;
                }
                posUnix = posUnix == INDEX_NOT_FOUND ? posWin : posUnix;
                posWin = posWin == INDEX_NOT_FOUND ? posUnix : posWin;
                return Math.min(posUnix, posWin) + 1;
            }
            final char ch1 = filename.charAt(1);
            if (ch1 == SYMBOL_COLON_CHAR) {
                ch0 = Character.toUpperCase(ch0);
                if (ch0 >= LETTER_A && ch0 <= LETTER_Z) {
                    if (len == SECOND || !isSeparator(filename.charAt(SECOND))) {
                        return 2;
                    }
                    return 3;
                } else if (ch0 == SYMBOL_LEFT_SLASH_CHAR) {
                    return 1;
                }
                return INDEX_NOT_FOUND;

            } else if (isSeparator(ch0) && isSeparator(ch1)) {
                int posUnix = filename.indexOf(SYMBOL_LEFT_SLASH, 2);
                int posWin = filename.indexOf(SYMBOL_RIGHT_SLASH, 2);
                if (isNotFound(posUnix, posWin)) {
                    return INDEX_NOT_FOUND;
                }
                posUnix = posUnix == INDEX_NOT_FOUND ? posWin : posUnix;
                posWin = posWin == INDEX_NOT_FOUND ? posUnix : posWin;
                return Math.min(posUnix, posWin) + 1;
            } else {
                return isSeparator(ch0) ? 1 : 0;
            }
        }
    }

    /**
     * 未找到
     *
     * @param posUnix unix
     * @param posWin  win
     * @return boolean
     */
    private static boolean isNotFound(int posUnix, int posWin) {
        return posUnix == 2 || posWin == posUnix;
    }

    /**
     * Checks if the character is a separator.
     *
     * @param ch the character to check
     * @return true if it is a separator character
     */
    private static boolean isSeparator(final char ch) {
        return ch == SYMBOL_LEFT_SLASH_CHAR || ch == SYMBOL_RIGHT_SLASH_CHAR;
    }

    /**
     * 获取名称
     * <pre>
     * a/b/c.txt --&gt; c.txt
     * a.txt     --&gt; a.txt
     * a/b/c     --&gt; c
     * a/b/c/    --&gt; ""
     * </pre>
     * <p>
     *
     * @param filename the filename to query, null returns null
     * @return the name of the file without the path, or an empty string if none exists.
     * Null bytes inside string will be removed
     */
    public static String getName(final String filename) {
        if (filename == null) {
            return null;
        }
        failIfNullBytePresent(filename);
        final int index = indexOfLastSeparator(filename);
        return filename.substring(index + 1);
    }

    /**
     * 后缀索引
     *
     * @param filename 文件名
     * @return 索引
     */
    public static int indexOfExtension(final String filename) {
        if (filename == null) {
            return INDEX_NOT_FOUND;
        }
        final int extensionPos = filename.lastIndexOf(SYMBOL_DOT);
        final int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? INDEX_NOT_FOUND : extensionPos;
    }

    /**
     * 后缀索引
     *
     * @param filename 文件名
     * @return 索引
     */
    public static int indexOfLastSeparator(final String filename) {
        if (filename == null) {
            return INDEX_NOT_FOUND;
        }
        final int lastUnixPos = filename.lastIndexOf(SYMBOL_LEFT_SLASH);
        final int lastWindowsPos = filename.lastIndexOf(SYMBOL_RIGHT_SLASH);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    /**
     * @param path
     */
    private static void failIfNullBytePresent(final String path) {
        final int len = path.length();
        for (int i = 0; i < len; i++) {
            if (path.charAt(i) == 0) {
                throw new IllegalArgumentException("Null byte present in file/path name. There are no " +
                        "known legitimate use cases for such data, but several injection attacks may use it");
            }
        }
    }

    private static String doGetPath(final String filename, final int separatorAdd) {
        if (filename == null) {
            return null;
        }
        final int prefix = getPrefixLength(filename);
        if (prefix < 0) {
            return null;
        }
        final int index = indexOfLastSeparator(filename);
        final int endIndex = index + separatorAdd;
        if (prefix >= filename.length() || index < 0 || prefix >= endIndex) {
            return "";
        }
        final String path = filename.substring(prefix, endIndex);
        failIfNullBytePresent(path);
        return path;
    }

    /**
     * Checks a filename to see if it matches the specified wildcard matcher,
     * always testing case-sensitive.
     * <p>
     * The wildcard matcher uses the characters '?' and '*' to represent a
     * single or multiple (zero or more) wildcard characters.
     * This is the same as often found on Dos/Unix command lines.
     * The check is case-sensitive always.
     * <pre>
     * wildcardMatch("c.txt", "*.txt")      --&gt; true
     * wildcardMatch("c.txt", "*.jpg")      --&gt; false
     * wildcardMatch("a/b/c.txt", "a/b/*")  --&gt; true
     * wildcardMatch("c.txt", "*.???")      --&gt; true
     * wildcardMatch("c.txt", "*.????")     --&gt; false
     * </pre>
     * N.B. the sequence "*?" does not work properly at present in match
     *
     * @param filename        the filename to match on
     * @param wildcardMatcher the wildcard string to match against
     * @return true if the filename matches the wildcard string
     */
    public static boolean wildcardMatch(final String filename, final String wildcardMatcher) {
        return PathMatcher.INSTANCE.match(wildcardMatcher, filename);
    }

    /**
     * Checks a filename to see if it matches the specified wildcard matcher,
     * always testing case-sensitive.
     * <p>
     * The wildcard matcher uses the characters '?' and '*' to represent a
     * single or multiple (zero or more) wildcard characters.
     * This is the same as often found on Dos/Unix command lines.
     * The check is case-sensitive always.
     * <pre>
     * wildcardMatches("c.txt", "c *.txt")      --&gt; true
     * wildcardMatches("c.txt", "c *.tx")      --&gt; false
     * wildcardMatches("c.txt", "*.jpg")      --&gt; false
     * wildcardMatches("a/b/c.txt", "a/b/*")  --&gt; true
     * wildcardMatches("c.txt", "*.???")      --&gt; true
     * wildcardMatches("c.txt", "*.????")     --&gt; false
     * </pre>
     * N.B. the sequence "*?" does not work properly at present in match
     *
     * @param filename        the filename to match on
     * @param wildcardMatcher the wildcard string to match against
     * @return true if the filename matches the wildcard string
     */
    public static boolean wildcardMatches(final String filename, final String wildcardMatcher) {
        List<String> strings = Splitter.on(PATTERN_EMPTY).trimResults().omitEmptyStrings().splitToList(wildcardMatcher);
        for (String item : strings) {
            if (!item.startsWith(CommonConstant.SYMBOL_ASTERISK)) {
                item = CommonConstant.SYMBOL_ASTERISK + item;
            }

            if (!item.endsWith(CommonConstant.SYMBOL_ASTERISK)) {
                item += CommonConstant.SYMBOL_ASTERISK;
            }
            if (!wildcardMatch(filename, item)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 文件转流
     *
     * @param path
     * @return
     */
    public static InputStream toInputStream(String path) throws IOException {
        if (null == path) {
            return null;
        }
        return toInputStream(new File(path));
    }

    /**
     * 文件转流
     *
     * @param file 文件
     * @return
     */
    public static InputStream toInputStream(File file) throws IOException {
        return IoUtils.openStream(file);
    }
    /**
     * 写文件
     *
     * @param file 文件
     * @param data 数据
     * @throws IOException IOException
     */
    public static void writeAppend(final String data, final File file) throws IOException {
        Files.write(file.toPath(), data.getBytes(UTF_8), StandardOpenOption.APPEND);
    }
    /**
     * 写文件
     *
     * @param file 文件
     * @param data 数据
     * @throws IOException IOException
     */
    public static void write(final String data, final File file) throws IOException {
        Files.write(file.toPath(), data.getBytes(UTF_8));
    }
    /**
     * 写文件
     *
     * @param file 文件
     * @param data 数据
     * @throws IOException IOException
     */
    public static void write(final byte[] data, final File file) throws IOException {
        Files.write(file.toPath(), data);
    }

    /**
     * 写文件
     *
     * @param file        文件
     * @param inputStream 流
     * @throws IOException IOException
     */
    public static void write(final InputStream inputStream, final File file) throws IOException {
        Files.write(file.toPath(), IoUtils.toByteArray(inputStream));
    }

    /**
     * 写文件
     *
     * @param file     文件
     * @param data     数据
     * @param encoding 编码
     * @throws IOException
     */
    public static void write(final File file, final CharSequence data, final Charset encoding) throws IOException {
        write(file, data, encoding, false);
    }


    /**
     * 写文件
     *
     * @param file        文件
     * @param outputStream 数据
     * @throws IOException ex
     */
    public static void write(final File file, final OutputStream outputStream) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            IoUtils.copy(fileInputStream, outputStream);
        }
    }


    /**
     * 写文件
     *
     * @param file        文件
     * @param inputStream 数据
     * @throws IOException ex
     */
    public static void write(final File file, final InputStream inputStream) throws IOException {
        byte[] bytes = new byte[2048];
        int line = 0;
        try (InputStream is = inputStream; FileOutputStream fos = new FileOutputStream(file)) {
            while ((line = is.read(bytes)) != 0) {
                fos.write(bytes, 0, line);
            }
        }
    }

    /**
     * 写文件
     *
     * @param file     文件
     * @param data     数据
     * @param encoding 编码
     * @throws IOException ex
     */
    public static void write(final File file, final CharSequence data, final String encoding) throws IOException {
        write(file, data, encoding, false);
    }

    /**
     * 写文件
     *
     * @param file     文件
     * @param data     数据
     * @param encoding 编码
     * @param append   追加
     * @throws IOException
     */
    public static void write(final File file, final CharSequence data, final String encoding, final boolean append)
            throws IOException {
        write(file, data, toCharset(encoding), append);
    }

    /**
     * 写文件
     *
     * @param file     文件
     * @param data     数据
     * @param encoding 编码
     * @param append   追加
     * @throws IOException
     */
    public static void write(final File file, final CharSequence data, final Charset encoding, final boolean append)
            throws IOException {
        final String str = data == null ? null : data.toString();
        writeStringToFile(file, str, encoding, append);
    }

    /**
     * 写文件
     *
     * @param file   文件
     * @param data   数据
     * @param append 追加
     * @throws IOException
     */
    public static void writeStringToFile(final File file, final String data,
                                         final boolean append) throws IOException {
        writeStringToFile(file, data, UTF_8, append);
    }

    /**
     * 写文件
     *
     * @param file     文件
     * @param data     数据
     * @param encoding 编码
     * @param append   追加
     * @throws IOException
     */
    public static void writeStringToFile(final File file, final String data, final Charset encoding,
                                         final boolean append) throws IOException {
        try (OutputStream out = openOutputStream(file, append)) {
            IoUtils.write(data, out, encoding);
        }
    }


    /**
     * 打开文件
     *
     * @param file   文件
     * @param append 是否追加
     * @return
     * @throws IOException
     */
    public static FileOutputStream openOutputStream(final File file, final boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            final File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }

    /**
     * 新文件
     *
     * @param file
     * @param timeMillis
     * @return
     */
    public static boolean isFileNewer(final File file, final long timeMillis) {
        if (file == null) {
            throw new IllegalArgumentException("No specified file");
        }
        if (!file.exists()) {
            return false;
        }
        return file.lastModified() > timeMillis;
    }

    /**
     * 获取文件夹
     *
     * @param folder 文件夹
     * @return
     */
    public static String toFolder(final String folder) {
        return StringUtils.isEmpty(folder) ? SYMBOL_EMPTY : (folder.endsWith(SYMBOL_LEFT_SLASH) ? folder : folder + SYMBOL_LEFT_SLASH);
    }

    /**
     * 是否是window
     *
     * @return boolean
     */
    public static boolean isWindows() {
        return OS_NAME.toLowerCase().contains(WINDOW);
    }

    /**
     * 是否是 Linux
     *
     * @return boolean
     */
    public static boolean isLinux() {
        return OS_NAME.toLowerCase().contains(LINUX);
    }

    /**
     * 获取流
     *
     * @param file 文件
     * @return BufferedInputStream
     */
    public static BufferedInputStream getInputStream(File file) {
        try {
            return new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * 文件流操作
     *
     * @param sourceFile 源文件
     * @return Stream<String>,当前文件不存在或者无法解析返回null
     */
    public static Stream<String> stream(final File sourceFile) {
        if (null == sourceFile) {
            return null;
        }
        try {
            return Files.lines(sourceFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 拷贝文件
     *
     * @param sourceFile 源文件
     * @param destFile   目标文件
     */
    public static void copyFile(final File sourceFile, final File destFile) {
        if (null == sourceFile || null == destFile) {
            return;
        }
        try {
            Files.copy(sourceFile.toPath(), destFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除后缀名
     *
     * @param name      字符串
     * @param extension 后缀名
     * @return 删除后缀名的字符串
     */
    public static String deleteSuffix(String name, String extension) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(extension) || !name.endsWith(extension)) {
            return name;
        }
        return name.substring(0, name.length() - extension.length());
    }

    /**
     * 尝试获取文件
     *
     * @param url 路径
     * @return 文件
     */
    public static File tryFile(URL url) {
        File file;
        String path;

        try {
            path = url.toURI().getSchemeSpecificPart();
            if ((file = new File(path)).exists()) {
                return file;
            }
        } catch (URISyntaxException ignored) {
        }

        try {
            path = URLDecoder.decode(url.getPath(), "UTF-8");
            if (path.contains(JAR_FILE_EXTENSION_IN)) {
                path = path.substring(0, path.lastIndexOf(JAR_FILE_EXTENSION_IN) + JAR_FILE_EXTENSION.length());
            }
            if ((file = new File(path)).exists()) {
                return file;
            }

        } catch (UnsupportedEncodingException ignored) {
        }

        try {
            path = url.toExternalForm();
            if (path.startsWith(JAR_URL_PREFIX)) {
                path = path.substring(JAR_URL_PREFIX.length());
            }

            if (path.startsWith(WS_JAR_URL_PREFIX)) {
                path = path.substring(WS_JAR_URL_PREFIX.length());
            }

            if (path.startsWith(FILE_URL_PREFIX)) {
                path = path.substring(FILE_URL_PREFIX.length());
            }

            if (path.contains(JAR_FILE_EXTENSION_IN)) {
                path = path.substring(0, path.indexOf(JAR_FILE_EXTENSION_IN) + JAR_FILE_EXTENSION.length());
            }

            if (path.contains(WAR_FILE_EXTENSION_IN)) {
                path = path.substring(0, path.indexOf(WAR_FILE_EXTENSION_IN) + WAR_FILE_EXTENSION.length());
            }

            if ((file = new File(path)).exists()) {
                return file;
            }

            path = path.replace("%20", " ");

            if ((file = new File(path)).exists()) {
                return file;
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * 扫描目录
     *
     * @param path    目录
     * @param matcher 匹配器
     */
    public static void doWith(String path, Consumer<Path> matcher) {
        if (StringUtils.isEmpty(path) || null == matcher) {
            return;
        }
        Path path1 = Paths.get(path);
        try {
            Files.walkFileTree(path1, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    matcher.accept(dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    matcher.accept(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户路径（绝对路径）
     *
     * @return 用户路径
     * @since 4.0.6
     */
    public static String getUserHomePath() {
        return System.getProperty("user.home");
    }

    /**
     * 检索文件夹下的文件
     *
     * @param folder     文件夹
     * @param fileFilter 过滤器
     * @return 匹配的文件
     */
    public static List<File> of(final String folder, final FileFilter fileFilter) {
        if (StringUtils.isEmpty(folder)) {
            return Collections.emptyList();
        }
        return of(new File(folder), fileFilter, null);
    }

    /**
     * 检索文件夹下的文件
     *
     * @param folder 文件夹
     * @param match  匹配
     * @return 匹配的文件
     */
    public static List<File> of(final String folder, final String match) {
        if (StringUtils.isEmpty(folder)) {
            return Collections.emptyList();
        }
        return of(new File(folder), File::isDirectory, match);
    }

    /**
     * 检索文件夹下的文件
     *
     * @param folder     文件夹
     * @param fileFilter 过滤器
     * @param match      匹配
     * @return 匹配的文件
     */
    public static List<File> of(final File folder, final FileFilter fileFilter, final String match) {
        if (null == folder || !folder.isDirectory()) {
            return Collections.emptyList();
        }
        long startTime = System.currentTimeMillis();
        List<File> result = new ArrayList<>();
        try {
            Files.walkFileTree(folder.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    File file1 = file.toFile();
                    boolean accept = fileFilter.accept(file1);
                    if (accept) {
                        if (null == match) {
                            result.add(file1);
                        }
                        if (wildcardMatch(file1.getAbsolutePath(), match)) {
                            result.add(file1);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取系统卷标
     *
     * @return 系统卷标
     */
    public static List<File> ofVolumesFile() {
        return Arrays.asList(File.listRoots());
    }

    /**
     * 获取系统卷标
     *
     * @return 系统卷标
     */
    public static List<File> ofVolumes() {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        return ofVolumesFile().stream().map(file -> new File(fileSystemView.getSystemDisplayName(file))).collect(Collectors.toList());
    }

    /**
     * 文件拷贝
     *
     * @param urls       urls
     * @param targetPath 目标目录
     * @throws IOException IOException
     */
    public static void copyFiles(URL[] urls, String targetPath) throws IOException {
        copyFiles(urls, targetPath, false);
    }

    /**
     * 文件拷贝
     *
     * @param urls       urls
     * @param targetPath 目标目录
     * @param overlay    覆盖
     * @throws IOException IOException
     */
    public static void copyFiles(URL[] urls, String targetPath, boolean overlay) throws IOException {
        createIfNotExist(targetPath);
        copyFiles(Arrays.stream(urls).map(url -> new File(url.getFile())).toArray(File[]::new), targetPath, overlay);
    }

    /**
     * 文件拷贝
     *
     * @param files      文件
     * @param targetPath 目标目录
     * @throws IOException IOException
     */
    public static void copyFiles(File[] files, String targetPath) throws IOException {
        copyFiles(files, targetPath, false);
    }

    /**
     * 文件拷贝
     *
     * @param files      文件
     * @param targetPath 目标目录
     * @param overlay    覆盖
     * @throws IOException IOException
     */
    public static void copyFiles(File[] files, String targetPath, boolean overlay) throws IOException {
        createIfNotExist(targetPath);

        List<Exception> exceptions = new ArrayList<>();
        Arrays.stream(files).forEach(url -> {
            try {
                copyFile(url, targetPath, overlay);
            } catch (IOException e) {
                exceptions.add(e);
            }
        });

        exceptions.forEach(e -> e.printStackTrace());
    }

    /**
     * 文件拷贝
     *
     * @param url        url
     * @param targetPath 目标目录
     * @throws IOException IOException
     */
    public static void copyFile(URL url, String targetPath) throws IOException {
        if(null == url) {
            return;
        }
        copyFile(url, targetPath, false);
    }

    /**
     * 文件拷贝
     *
     * @param url        url
     * @param targetPath 目标目录
     * @param overlay    覆盖
     * @throws IOException IOException
     */
    public static void copyFile(URL url, String targetPath, boolean overlay) throws IOException {
        createIfNotExist(targetPath);
        copyFile(toFile(url), targetPath, overlay);
    }

    /**
     * 文件拷贝
     *
     * @param sourceFile 源文件
     * @param targetPath 目标目录
     * @throws IOException IOException
     */
    public static void copyFile(File sourceFile, String targetPath) throws IOException {
        copyFile(sourceFile, targetPath, false);
    }

    /**
     * 文件拷贝
     *
     * @param sourceFile 源文件
     * @param targetPath 目标目录
     * @param overlay    覆盖
     * @throws IOException IOException
     */
    public static void copyFile(File sourceFile, File targetPath, boolean overlay) throws IOException {
        copyFile(sourceFile, targetPath.getAbsolutePath(), overlay);
    }

    /**
     * 文件拷贝
     *
     * @param sourceFile 源文件
     * @param targetPath 目标目录
     * @param overlay    覆盖
     * @throws IOException IOException
     */
    public static void copyFile(File sourceFile, String targetPath, boolean overlay) throws IOException {
        if (null == sourceFile) {
            return;
        }
        createIfNotExist(targetPath);
        Path path = Paths.get(targetPath, sourceFile.getName());
        boolean exists = Files.exists(path);
        if (isExistAndOver(exists, overlay)) {
            CopyOption[] copyOptions = new CopyOption[0];
            if (exists) {
                copyOptions = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
            }
            Files.copy(sourceFile.toPath(), path, copyOptions);
        }
    }

    /**
     * 检测所在/覆盖
     *
     * @param exists  存在
     * @param overlay 覆盖
     * @return 不存在返回true
     */
    private static boolean isExistAndOver(boolean exists, boolean overlay) {
        return !exists || (exists && overlay);
    }

    /**
     * 如果不存在则创建文件夹
     *
     * @param pre    前缀目录
     * @param folder 文件夹
     * @return 目录
     */
    public static Path createIfNotExist(final Path pre, final String folder) throws IOException {
        if (StringUtils.isEmpty(folder)) {
            return pre;
        }

        Path path = Paths.get(pre.toFile().getAbsolutePath(), folder);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    /**
     * 如果不存在则创建文件夹
     *
     * @param folder 文件夹
     * @return
     */
    public static Path createIfNotExist(final String folder) throws IOException {
        if (StringUtils.isEmpty(folder)) {
            return null;
        }

        Path path = Paths.get(folder);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    /**
     * 如果不存在则创建文件夹
     *
     * @param folder 文件夹
     */
    public static void createIfNotExist(final File folder) throws IOException {
        if (null == folder) {
            return;
        }
        createIfNotExist(folder.getAbsolutePath());
    }

    /**
     * 获取文件
     *
     * @param url url
     * @return 文件
     */
    private static File toFile(URL url) {
        return null != url ? new File(url.getFile()) : null;
    }

    /**
     * 最终修改时间
     *
     * @param absolutePath 有效文件
     * @return 修改时间
     */
    public static long lastModified(String absolutePath) {
        if (StringUtils.isEmpty(absolutePath)) {
            return -1;
        }
        File temp = new File(absolutePath);
        if (!temp.exists()) {
            return -1;
        }
        return temp.lastModified();
    }

    /**
     * 内存映射文件
     *
     * @param file 文件
     * @param mode 模式.e.g. "r", "w", "rw"
     * @param size 映射大小
     * @return 内存映射文件
     */
    public static MappedByteBuffer mapper(File file, String mode, int size) throws IOException {
        if (null == file) {
            return null;
        }

        if (!file.exists()) {
            file.createNewFile();
        }
        FileChannel channel = new RandomAccessFile(file, mode).getChannel();
        return channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
    }

    /**
     * 当文件不存在创建文件,并写入数据
     *
     * @param file   文件
     * @param value  数据
     * @param append 是否追加
     */
    public static void writeNoExistCreate(File file, String value, boolean append) {
        if (null == file) {
            return;
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().exists();
        }

        try {
            write(file, value, UTF_8, append);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建文件夹，如果存在直接返回此文件夹<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param dirPath 文件夹路径，使用POSIX格式，无论哪个平台
     * @return 创建的目录
     */
    public static File mkdir(String dirPath) {
        if (dirPath == null) {
            return null;
        }
        final File dir = file(dirPath);
        return mkdir(dir);
    }

    /**
     * 创建目录
     *
     * @param dir 目录
     * @return 目录
     */
    public static File mkdir(File dir) {
        if (dir == null) {
            return null;
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 创建所给文件或目录的父目录
     *
     * @param file 文件或目录
     * @return 父目录
     */
    public static File mkParentDirs(File file) {
        final File parentFile = file.getParentFile();
        if (null != parentFile && !parentFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            parentFile.mkdirs();
        }
        return parentFile;
    }

    /**
     * 是否是window
     *
     * @return 是否是window
     */
    public static boolean isSystemWindows() {
        return File.separatorChar == SYMBOL_RIGHT_SLASH_CHAR;
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param file 文件对象
     * @return 文件，若路径为null，返回null
     * @throws IOException IO异常
     */
    public static File touch(File file) throws IOException {
        if (null == file) {
            return null;
        }
        if (!file.exists()) {
            mkParentDirs(file);
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param fullFilePath 文件的全路径，使用POSIX风格
     * @return 文件，若路径为null，返回null
     * @throws IOException IO异常
     */
    public static File touch(String fullFilePath) throws IOException {
        if (fullFilePath == null) {
            return null;
        }
        return touch(file(fullFilePath));
    }

    /**
     * 复制文件或目录<br>
     * 如果目标文件为目录，则将源文件以相同文件名拷贝到目标目录
     *
     * @param srcPath    源文件或目录
     * @param destPath   目标文件或目录，目标不存在会自动创建（目录、文件都创建）
     * @param isOverride 是否覆盖目标文件
     * @return 目标目录或文件
     * @throws IOException IO异常
     */
    public static File copy(String srcPath, String destPath, boolean isOverride) throws IOException {
        return copy(file(srcPath), file(destPath), isOverride);
    }

    /**
     * 复制文件或目录<br>
     * 情况如下：
     *
     * <pre>
     * 1、src和dest都为目录，则将src目录及其目录下所有文件目录拷贝到dest下
     * 2、src和dest都为文件，直接复制，名字为dest
     * 3、src为文件，dest为目录，将src拷贝到dest目录下
     * </pre>
     *
     * @param src        源文件
     * @param dest       目标文件或目录，目标不存在会自动创建（目录、文件都创建）
     * @param isOverride 是否覆盖目标文件
     * @return 目标目录或文件
     * @throws IOException IO异常
     */
    public static File copy(File src, File dest, boolean isOverride) throws IOException {
        StandardCopyOption[] standardCopyOptions = new StandardCopyOption[0];
        if (isOverride) {
            standardCopyOptions = new StandardCopyOption[]{StandardCopyOption.REPLACE_EXISTING};
        }

        Files.copy(src.toPath(), dest.toPath(), standardCopyOptions);
        return dest;
    }

    /**
     * 是否是zip
     *
     * @param extension 后缀
     * @return zip
     */
    public static boolean isZip(String extension) {
        for (String compress : ZIP_COMPRESS) {
            if (compress.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建临时文件
     *
     * @param name  文件名
     * @param bytes 字节码
     * @return 文件
     */
    public static File createTempFile(String name, byte[] bytes) {
        File file = new File(name + ".tmp");
        if (file.exists()) {
            return file;
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            bos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 创建临时文件
     *
     * @param name        文件名
     * @param inputStream 字节码
     * @return 文件
     */
    public static File createTempFile(String name, InputStream inputStream) {
        try {
            return createTempFile(name, IoUtils.toByteArray(inputStream));
        } catch (IOException e) {
            return createTempFile(name, EMPTY_BYTE);
        }
    }

    /**
     * 创建临时文件
     *
     * @param name   文件名
     * @param reader 字节码
     * @return 文件
     */
    public static File createTempFile(String name, Reader reader) {
        try {
            return createTempFile(name, IoUtils.toByteArray(reader));
        } catch (IOException e) {
            return createTempFile(name, EMPTY_BYTE);
        }
    }

    /**
     * 文件夹大小
     *
     * @param directory 文件夹
     * @return 大小
     */
    public static long sizeOfDirectory(File directory) {
        return sizeOfDirectory0(requireDirectoryExists(directory, "directory"));
    }

    /**
     * Gets the size of a directory.
     *
     * @param directory the directory to check
     * @return the size
     * @throws NullPointerException if the directory is {@code null}.
     */
    private static long sizeOfDirectory0(final File directory) {
        Objects.requireNonNull(directory, "directory");
        final File[] files = directory.listFiles();
        if (files == null) {
            return 0L;
        }
        long size = 0;

        for (final File file : files) {
            if (!isSymlink(file)) {
                size += sizeOf0(file);
                if (size < 0) {
                    break;
                }
            }
        }

        return size;
    }

    /**
     * 文件大小
     *
     * @param file 文件
     * @return 大小
     */
    private static long sizeOf0(final File file) {
        Objects.requireNonNull(file, "file");
        if (file.isDirectory()) {
            return sizeOfDirectory0(file);
        }
        return file.length();
    }

    /**
     * 获取后缀
     *
     * @param extensions 后缀
     * @return 后缀
     */
    private static String[] toSuffixes(final String... extensions) {
        Objects.requireNonNull(extensions, "extensions");
        final String[] suffixes = new String[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
            suffixes[i] = "." + extensions[i];
        }
        return suffixes;
    }

    /**
     * 深度
     *
     * @param recursive 是否深度迭代
     * @return 深度
     */
    private static int toMaxDepth(final boolean recursive) {
        return recursive ? Integer.MAX_VALUE : 1;
    }

    /**
     * 移动文件
     *
     * @param file     文件
     * @param director 目录
     */
    public static void move(File file, File director) throws IOException {
        File directors = new File(director, file.getName());
        forceMkdirParent(directors);
        Files.move(file.toPath(), directors.toPath());
    }

    /**
     * 将列表写入文件，覆盖模式，编码为UTF-8
     *
     * @param <T>  集合元素类型
     * @param list 列表
     * @param path 绝对路径
     * @return 目标文件
     * @throws IOException IO异常
     * @since 3.2.0
     */
    public static <T> File writeUtf8Lines(Collection<T> list, String path) throws IOException {
        return writeLines(list, path, UTF_8);
    }

    /**
     * 将列表写入文件，覆盖模式，编码为UTF-8
     *
     * @param <T>  集合元素类型
     * @param list 列表
     * @param file 绝对路径
     * @return 目标文件
     * @throws IOException IO异常
     * @since 3.2.0
     */
    public static <T> File writeUtf8Lines(Collection<T> list, File file) throws IOException {
        return writeLines(list, file, UTF_8);
    }

    /**
     * 将列表写入文件，覆盖模式
     *
     * @param <T>     集合元素类型
     * @param list    列表
     * @param path    绝对路径
     * @param charset 字符集
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static <T> File writeLines(Collection<T> list, String path, String charset) throws IOException {
        return writeLines(list, path, charset, false);
    }

    /**
     * 将列表写入文件，覆盖模式
     *
     * @param <T>     集合元素类型
     * @param list    列表
     * @param path    绝对路径
     * @param charset 字符集
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static <T> File writeLines(Collection<T> list, String path, Charset charset) throws IOException {
        return writeLines(list, path, charset, false);
    }

    /**
     * 将列表写入文件，覆盖模式
     *
     * @param <T>     集合元素类型
     * @param list    列表
     * @param file    文件
     * @param charset 字符集
     * @return 目标文件
     * @throws IOException IO异常
     * @since 4.2.0
     */
    public static <T> File writeLines(Collection<T> list, File file, String charset) throws IOException {
        return writeLines(list, file, charset, false);
    }

    /**
     * 将列表写入文件，覆盖模式
     *
     * @param <T>     集合元素类型
     * @param list    列表
     * @param file    文件
     * @param charset 字符集
     * @return 目标文件
     * @throws IOException IO异常
     * @since 4.2.0
     */
    public static <T> File writeLines(Collection<T> list, File file, Charset charset) throws IOException {
        return writeLines(list, file, charset, false);
    }

    /**
     * 将列表写入文件，追加模式
     *
     * @param <T>  集合元素类型
     * @param list 列表
     * @param file 文件
     * @return 目标文件
     * @throws IOException IO异常
     * @since 3.1.2
     */
    public static <T> File appendUtf8Lines(Collection<T> list, File file) throws IOException {
        return appendLines(list, file, UTF_8);
    }

    /**
     * 将列表写入文件，追加模式
     *
     * @param <T>  集合元素类型
     * @param list 列表
     * @param path 文件路径
     * @return 目标文件
     * @throws IOException IO异常
     * @since 3.1.2
     */
    public static <T> File appendUtf8Lines(Collection<T> list, String path) throws IOException {
        return appendLines(list, path, UTF_8);
    }

    /**
     * 将列表写入文件，追加模式
     *
     * @param <T>     集合元素类型
     * @param list    列表
     * @param path    绝对路径
     * @param charset 字符集
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static <T> File appendLines(Collection<T> list, String path, String charset) throws IOException {
        return writeLines(list, path, charset, true);
    }

    /**
     * 将列表写入文件，追加模式
     *
     * @param <T>     集合元素类型
     * @param list    列表
     * @param file    文件
     * @param charset 字符集
     * @return 目标文件
     * @throws IOException IO异常
     * @since 3.1.2
     */
    public static <T> File appendLines(Collection<T> list, File file, String charset) throws IOException {
        return writeLines(list, file, charset, true);
    }

    /**
     * 将列表写入文件，追加模式
     *
     * @param <T>     集合元素类型
     * @param list    列表
     * @param path    绝对路径
     * @param charset 字符集
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static <T> File appendLines(Collection<T> list, String path, Charset charset) throws IOException {
        return writeLines(list, path, charset, true);
    }

    /**
     * 将列表写入文件，追加模式，策略为：
     * <ul>
     *     <li>当文件为空，从开头追加，尾部不加空行</li>
     *     <li>当有内容，换行追加，尾部不加空行</li>
     *     <li>当有内容，并末尾有空行，依旧换行追加</li>
     * </ul>
     *
     * @param <T>     集合元素类型
     * @param list    列表
     * @param file    文件
     * @param charset 字符集
     * @return 目标文件
     * @throws IOException IO异常
     * @since 3.1.2
     */
    public static <T> File appendLines(Collection<T> list, File file, Charset charset) throws IOException {
        return writeLines(list, file, charset, true);
    }

    /**
     * 将列表写入文件
     *
     * @param <T>      集合元素类型
     * @param list     列表
     * @param path     文件路径
     * @param charset  字符集
     * @param isAppend 是否追加
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static <T> File writeLines(Collection<T> list, String path, String charset, boolean isAppend) throws IOException {
        return writeLines(list, file(path), charset, isAppend);
    }

    /**
     * 将列表写入文件
     *
     * @param <T>      集合元素类型
     * @param list     列表
     * @param path     文件路径
     * @param charset  字符集
     * @param isAppend 是否追加
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static <T> File writeLines(Collection<T> list, String path, Charset charset, boolean isAppend) throws IOException {
        return writeLines(list, file(path), charset, isAppend);
    }

    /**
     * 将列表写入文件
     *
     * @param <T>      集合元素类型
     * @param list     列表
     * @param file     文件
     * @param charset  字符集
     * @param isAppend 是否追加
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static <T> File writeLines(Collection<T> list, File file, String charset, boolean isAppend) throws IOException {
        return com.chua.common.support.file.FileWriter.create(file, Charset.forName(charset)).writeLines(list, isAppend);
    }

    /**
     * 将列表写入文件
     *
     * @param <T>      集合元素类型
     * @param list     列表
     * @param file     文件
     * @param charset  字符集
     * @param isAppend 是否追加
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static <T> File writeLines(Collection<T> list, File file, Charset charset, boolean isAppend) throws IOException {
        return com.chua.common.support.file.FileWriter.create(file, charset).writeLines(list, isAppend);
    }

    /**
     * 将Map写入文件，每个键值对为一行，一行中键与值之间使用kvSeparator分隔
     *
     * @param map         Map
     * @param file        文件
     * @param kvSeparator 键和值之间的分隔符，如果传入null使用默认分隔符" = "
     * @param isAppend    是否追加
     * @return 目标文件
     * @throws IOException IO异常
     * @since 4.0.5
     */
    public static File writeUtf8Map(Map<?, ?> map, File file, String kvSeparator, boolean isAppend) throws IOException {
        return com.chua.common.support.file.FileWriter.create(file, UTF_8).writeMap(map, kvSeparator, isAppend);
    }

    /**
     * 将Map写入文件，每个键值对为一行，一行中键与值之间使用kvSeparator分隔
     *
     * @param map         Map
     * @param file        文件
     * @param charset     字符集编码
     * @param kvSeparator 键和值之间的分隔符，如果传入null使用默认分隔符" = "
     * @param isAppend    是否追加
     * @return 目标文件
     * @throws IOException IO异常
     * @since 4.0.5
     */
    public static File writeMap(Map<?, ?> map, File file, Charset charset, String kvSeparator, boolean isAppend) throws IOException {
        return com.chua.common.support.file.FileWriter.create(file, charset).writeMap(map, kvSeparator, isAppend);
    }

    /**
     * 写数据到文件中<br>
     * 文件路径如果是相对路径，则相对ClassPath
     *
     * @param data 数据
     * @param path 相对ClassPath的目录或者绝对路径目录
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static File writeBytes(byte[] data, String path) throws IOException {
        return writeBytes(data, touch(path));
    }

    /**
     * 写数据到文件中
     *
     * @param dest 目标文件
     * @param data 数据
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static File writeBytes(byte[] data, File dest) throws IOException {
        return writeBytes(data, dest, 0, data.length, false);
    }

    /**
     * 写入数据到文件
     *
     * @param data     数据
     * @param dest     目标文件
     * @param off      数据开始位置
     * @param len      数据长度
     * @param isAppend 是否追加模式
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static File writeBytes(byte[] data, File dest, int off, int len, boolean isAppend) throws IOException {
        return com.chua.common.support.file.FileWriter.create(dest).write(data, off, len, isAppend);
    }

    /**
     * 将流的内容写入文件<br>
     * 此方法会自动关闭输入流
     *
     * @param dest 目标文件
     * @param in   输入流
     * @return dest
     * @throws IOException IO异常
     */
    public static File writeFromStream(InputStream in, File dest) throws IOException {
        return writeFromStream(in, dest, true);
    }

    /**
     * 将流的内容写入文件
     *
     * @param dest      目标文件
     * @param in        输入流
     * @param isCloseIn 是否关闭输入流
     * @return dest
     * @throws IOException IO异常
     * @since 5.5.6
     */
    public static File writeFromStream(InputStream in, File dest, boolean isCloseIn) throws IOException {
        return com.chua.common.support.file.FileWriter.create(dest).writeFromStream(in, isCloseIn);
    }

    /**
     * 将流的内容写入文件<br>
     * 此方法会自动关闭输入流
     *
     * @param in           输入流
     * @param fullFilePath 文件绝对路径
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static File writeFromStream(InputStream in, String fullFilePath) throws IOException {
        return writeFromStream(in, touch(fullFilePath));
    }

    /**
     * 目录是否为空
     *
     * @param file 目录
     * @return 是否为空，当提供非目录时，返回false
     */
    public static boolean isNotEmpty(File file) {
        return !isEmpty(file);
    }


    /**
     * 文件是否为空<br>
     * 目录：里面没有文件时为空 文件：文件大小为0时为空
     *
     * @param file 文件
     * @return 是否为空，当提供非目录时，返回false
     */
    public static boolean isEmpty(File file) {
        if (null == file || false == file.exists()) {
            return true;
        }

        if (file.isDirectory()) {
            String[] subFiles = file.list();
            return ArrayUtils.isEmpty(subFiles);
        } else if (file.isFile()) {
            return file.length() <= 0;
        }

        return false;
    }

    /**
     * 是否存在文件
     *
     * @param s 路径
     * @return 是否存在文件
     */
    public static boolean exist(File s) {
        if (null == s || !s.exists()) {
            return false;
        }

        return true;
    }

    /**
     * 是否存在文件
     *
     * @param s 路径
     * @return 是否存在文件
     */
    public static boolean exist(String s) {
        if (null == s || !new File(s).exists()) {
            return false;
        }

        return true;
    }

    /**
     * uri
     *
     * @param s 路径
     * @return uri
     */
    public static String toUri(String s) {
        return new File(s).toURI().toString();
    }

    /**
     * 是否是压缩文件
     * @param name 名称
     * @return 是否是压缩文件
     */
    public static boolean isCompressFile(String name) {
        String extension = FileUtils.getSimpleExtension(name);
        if(ArrayUtils.containsIgnoreCase(COMPRESS, extension)) {
            return true;
        }
        while (!StringUtils.isEmpty(extension)) {
            extension = FileUtils.getSimpleExtension(extension);
            if(ArrayUtils.containsIgnoreCase(COMPRESS, extension)) {
                return true;
            }
        }
        return false;
    }
}
