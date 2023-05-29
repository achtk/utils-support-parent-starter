package com.chua.common.support.utils;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.file.xz.XZInputStream;
import com.chua.common.support.function.SafeFunction;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.io.ProgressInputStream;
import com.chua.common.support.lang.process.ProgressBar;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static com.chua.common.support.constant.CommonConstant.EMPTY;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * io工具类
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/1/11
 */
public class IoUtils {
    public static final int DEFAULT_BUFFER_SIZE = 1024 * 10;
    private static final int EOF = -1;
    private static final String SYMBOL_LF = "\n";
    private static final String XZ = "xz";
    private static String GZ = "gz";

    /**
     * 转换为{@link BufferedInputStream}
     *
     * @param in {@link InputStream}
     * @return {@link BufferedInputStream}
     * @since 4.0.10
     */
    public static BufferedInputStream toBuffered(InputStream in) {
        return (in instanceof BufferedInputStream) ? (BufferedInputStream) in : new BufferedInputStream(in);
    }

    /**
     * 转换为{@link BufferedInputStream}
     *
     * @param in         {@link InputStream}
     * @param bufferSize buffer size
     * @return {@link BufferedInputStream}
     * @since 5.6.1
     */
    public static BufferedInputStream toBuffered(InputStream in, int bufferSize) {
        return (in instanceof BufferedInputStream) ? (BufferedInputStream) in : new BufferedInputStream(in, bufferSize);
    }

    /**
     * 转换为{@link BufferedOutputStream}
     *
     * @param out {@link OutputStream}
     * @return {@link BufferedOutputStream}
     * @since 4.0.10
     */
    public static BufferedOutputStream toBuffered(OutputStream out) {
        return (out instanceof BufferedOutputStream) ? (BufferedOutputStream) out : new BufferedOutputStream(out);
    }

    /**
     * 转换为{@link BufferedOutputStream}
     *
     * @param out        {@link OutputStream}
     * @param bufferSize buffer size
     * @return {@link BufferedOutputStream}
     * @since 5.6.1
     */
    public static BufferedOutputStream toBuffered(OutputStream out, int bufferSize) {
        return (out instanceof BufferedOutputStream) ? (BufferedOutputStream) out : new BufferedOutputStream(out, bufferSize);
    }

    /**
     * 转换为{@link BufferedReader}
     *
     * @param reader {@link Reader}
     * @return {@link BufferedReader}
     * @since 5.6.1
     */
    public static BufferedReader toBuffered(Reader reader) {
        return (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
    }

    /**
     * 转换为{@link BufferedReader}
     *
     * @param reader     {@link Reader}
     * @param bufferSize buffer size
     * @return {@link BufferedReader}
     * @since 5.6.1
     */
    public static BufferedReader toBuffered(Reader reader, int bufferSize) {
        return (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader, bufferSize);
    }

    /**
     * 转换为{@link BufferedWriter}
     *
     * @param writer {@link Writer}
     * @return {@link BufferedWriter}
     * @since 5.6.1
     */
    public static BufferedWriter toBuffered(Writer writer) {
        return (writer instanceof BufferedWriter) ? (BufferedWriter) writer : new BufferedWriter(writer);
    }

    /**
     * 转换为{@link BufferedWriter}
     *
     * @param writer     {@link Writer}
     * @param bufferSize buffer size
     * @return {@link BufferedWriter}
     * @since 5.6.1
     */
    public static BufferedWriter toBuffered(Writer writer, int bufferSize) {
        return (writer instanceof BufferedWriter) ? (BufferedWriter) writer : new BufferedWriter(writer, bufferSize);
    }

    /**
     * 关闭URLConnection.
     *
     * @param conn the connection to close.
     * @since 2.4
     */
    public static void close(final URLConnection conn) {
        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).disconnect();
        }
    }

    /**
     * 关闭
     *
     * @param graphics2d graphics2d
     */
    public static void closeQuietly(Graphics2D graphics2d) {
        if (null == graphics2d) {
            return;
        }
        graphics2d.dispose();
    }

    /**
     * 关闭
     *
     * @param closeable 可关闭
     */
    public static void closeQuietly(final AutoCloseable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final Throwable ignore) {
            // ignore
        }
    }

    /**
     * 关闭
     *
     * @param connection 可关闭
     */
    public static void closeQuietly(final HttpURLConnection connection) {
        try {
            if (connection != null) {
                connection.disconnect();
            }
        } catch (final Throwable ignore) {
            // ignore
        }
    }
    /**
     * 拷贝流，拷贝后不关闭流
     *
     * @param in             输入流
     * @param out            输出流
     * @param bufferSize     缓存大小
     * @param progressBar 进度条
     * @return 传输的byte数
     * @throws RuntimeException IO异常
     * @since 5.7.8
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize, ProgressBar progressBar) throws IOException {
        ProgressInputStream stream = new ProgressInputStream(in, progressBar);
        return copy(stream, out, bufferSize);
    }
    /**
     * 输入流拷贝
     *
     * @param input 输入流
     * @return 输入流
     * @throws IOException IOException
     */
    public static InputStream copy(final InputStream input) throws IOException {
        try (ByteArrayOutputStream baas = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baas.write(buffer, 0, len);
            }
            baas.flush();
            return new ByteArrayInputStream(baas.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 输入流拷贝到输出流
     *
     * @param input   输入流
     * @param output  输出流
     * @param charset 编码
     * @throws IOException IOException
     */
    public static void copy(final InputStream input, final Writer output, final Charset charset) throws IOException {
        final InputStreamReader in = new InputStreamReader(input, toCharset(charset));
        copy(in, output);
    }

    /**
     * 输入流拷贝到输出流
     *
     * @param input  输入流
     * @param output 输出流
     * @return int
     * @throws IOException IOException
     */
    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * 拷贝文件
     *
     * @param input   输入流
     * @param output  输出流
     * @param charset 编码
     * @throws IOException IOException
     */
    public static void copy(final Reader input, final OutputStream output, final Charset charset)
            throws IOException {
        final OutputStreamWriter out = new OutputStreamWriter(output, toCharset(charset));
        copy(input, out);
        out.flush();
    }

    /**
     * 拷贝文件
     *
     * @param input  输入流
     * @param output 输出流
     * @return int
     * @throws IOException IOException
     */
    public static int copy(final Reader input, final Writer output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * 拷贝文件
     *
     * @param input  输入流
     * @param output 输出流
     * @return long
     * @throws IOException IOException
     */
    public static long copy(final InputStream input, final OutputStream output, final int bufferSize) throws IOException {
        return copyLarge(input, output, new byte[bufferSize]);
    }

    /**
     * 流拷贝
     *
     * @param inputStream 流
     * @return 流
     */
    public static ByteBuffer copyInputStream(InputStream inputStream) throws IOException {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // 定义一个缓存数组，临时存放读取的数组
            //经过测试，4*1024是一个非常不错的数字，过大过小都会比较影响性能
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > -1) {
                stream.write(buffer, 0, length);
            }
            stream.flush();
            return ByteBuffer.wrap(stream.toByteArray());
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * 拷贝大文件
     *
     * @param input  输入流
     * @param output 输出流
     * @return long
     * @throws IOException IOException
     */
    public static long copyLarge(final Reader input, final Writer output) throws IOException {
        return copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * 拷贝大文件
     *
     * @param input  输入流
     * @param output 输出流
     * @param buffer 数组
     * @return long
     * @throws IOException IOException
     */
    public static long copyLarge(final Reader input, final Writer output, final char[] buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * 拷贝大文件
     *
     * @param input  输入流
     * @param output 输出流
     * @param buffer 数组
     * @return long
     * @throws IOException IOException
     */
    public static long copyLarge(final InputStream input, final OutputStream output, final byte[] buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * 拷贝大文件
     *
     * @param input  输入流
     * @param output 输出流
     * @return long
     * @throws IOException IOException
     */
    public static long copyLarge(final InputStream input, final OutputStream output) throws IOException {
        return copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 获得一个输出流对象
     *
     * @param file 文件
     * @return 输出流对象
     * @throws IOException IO异常
     */
    public static BufferedOutputStream getOutputStream(File file) throws IOException {
        try {
            return new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * 获得一个输出流对象
     *
     * @param path 输出到的文件路径，绝对路径
     * @return 输出流对象
     * @throws IOException IO异常
     */
    public static BufferedOutputStream getOutputStream(String path) throws IOException {
        return getOutputStream(new File(path));
    }

    /**
     * 获得一个带缓存的写入对象
     *
     * @param path        输出路径，绝对路径
     * @param charsetName 字符集
     * @param isAppend    是否追加
     * @return BufferedReader对象
     * @throws IOException IO异常
     */
    public static BufferedWriter getWriter(String path, String charsetName, boolean isAppend) throws IOException {
        return getWriter(new File(path), Charset.forName(charsetName), isAppend);
    }
    /**
     * 获得一个Writer
     *
     * @param out     输入流
     * @param charset 字符集
     * @return OutputStreamWriter对象
     */
    public static OutputStreamWriter getWriter(OutputStream out, Charset charset) {
        if (null == out) {
            return null;
        }

        if (null == charset) {
            return new OutputStreamWriter(out);
        } else {
            return new OutputStreamWriter(out, charset);
        }
    }
    /**
     * 获得一个带缓存的写入对象
     *
     * @param path     输出路径，绝对路径
     * @param charset  字符集
     * @param isAppend 是否追加
     * @return BufferedReader对象
     * @throws IOException IO异常
     */
    public static BufferedWriter getWriter(String path, Charset charset, boolean isAppend) throws IOException {
        return getWriter(new File(path), charset, isAppend);
    }

    /**
     * 获得一个带缓存的写入对象
     *
     * @param file        输出文件
     * @param charsetName 字符集
     * @param isAppend    是否追加
     * @return BufferedReader对象
     * @throws IOException IO异常
     */
    public static BufferedWriter getWriter(File file, String charsetName, boolean isAppend) throws IOException {
        return getWriter(file, Charset.forName(charsetName), isAppend);
    }

    /**
     * 获得一个带缓存的写入对象
     *
     * @param file     输出文件
     * @param charset  字符集
     * @param isAppend 是否追加
     * @return BufferedReader对象
     * @throws IOException IO异常
     */
    public static BufferedWriter getWriter(File file, Charset charset, boolean isAppend) throws IOException {
        try {
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, isAppend), charset));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 逐行读取
     *
     * @param input    输入流
     * @param encoding 编码
     * @return 迭代器
     * @throws IOException IOException
     */
    public static LineIterator lineIterator(InputStream input, Charset encoding) throws IOException {
        return new LineIterator(new InputStreamReader(input, encoding));
    }

    /**
     * 获取文件流
     *
     * @param file 文件
     * @return FileInputStream
     * @throws IOException IOException
     */
    public static FileInputStream openStream(final File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }

    /**
     * 获取流
     *
     * @param source 字符串
     * @return StringReader
     */
    public static StringReader openStream(final String source) {
        return new StringReader(source);
    }

    /**
     * 获取流
     *
     * @param url url
     * @return FileInputStream
     * @throws IOException IOException
     */
    public static InputStream openStream(final URL url) throws IOException {
        return null != url ? url.openStream() : null;
    }

    /**
     * 获取流
     *
     * @param uri url
     * @return FileInputStream
     * @throws IOException IOException
     */
    public static InputStream openStream(final URI uri) throws IOException {
        return null != uri ? uri.toURL().openStream() : null;
    }


    /**
     * 读取流
     *
     * @param input  流
     * @param buffer 缓冲
     * @return int
     * @throws IOException IOException
     */
    public static int read(final ReadableByteChannel input, final ByteBuffer buffer) throws IOException {
        final int length = buffer.remaining();
        while (buffer.remaining() > 0) {
            final int count = input.read(buffer);
            if (EOF == count) {
                break;
            }
        }
        return length - buffer.remaining();
    }

    /**
     * 读取流
     *
     * @param input  流
     * @param buffer 字节
     * @return int
     * @throws IOException IOException
     */
    public static int read(final Reader input, final char[] buffer) throws IOException {
        return read(input, buffer, 0, buffer.length);
    }

    /**
     * 读取流
     *
     * @param input  流
     * @param buffer 字节
     * @param offset 位置
     * @param length 长度
     * @return int
     * @throws IOException IOException
     */
    public static int read(final Reader input, final char[] buffer, final int offset, final int length)
            throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("Length must not be negative: " + length);
        }
        int remaining = length;
        while (remaining > 0) {
            final int location = length - remaining;
            final int count = input.read(buffer, offset + location, remaining);
            if (EOF == count) {
                break;
            }
            remaining -= count;
        }
        return length - remaining;
    }

    /**
     * 读取所有字节
     *
     * @param input  流
     * @param buffer 字节
     * @param offset 位置
     * @param length 长度
     * @throws IOException IOException
     */
    public static void readFully(final Reader input, final char[] buffer, final int offset, final int length)
            throws IOException {
        final int actual = read(input, buffer, offset, length);
        if (actual != length) {
            throw new EOFException("Length to read: " + length + " actual: " + actual);
        }
    }

    /**
     * 读取所有字节
     *
     * @param input  流
     * @param buffer 字节
     * @throws IOException IOException
     */
    public static void readFully(final InputStreamReader input, final char[] buffer) throws IOException {
        readFully(input, buffer, 0, buffer.length);
    }

    /**
     * 读取所有字节
     *
     * @param input  流
     * @param buffer 字节
     * @param offset 位置
     * @param length 长度
     * @throws IOException IOException
     */
    public static void readFully(final InputStreamReader input, final char[] buffer, final int offset, final int length)
            throws IOException {
        final int actual = read(input, buffer, offset, length);
        if (actual != length) {
            throw new EOFException("Length to read: " + length + " actual: " + actual);
        }
    }

    /**
     * 读取所有字节
     *
     * @param input    流
     * @param encoding 编码
     * @throws IOException IOException
     */
    public static List<String> readLines(final InputStream input, final Charset encoding) throws IOException {
        try (final InputStreamReader reader = new InputStreamReader(input, toCharset(encoding))) {
            return readLines(reader);
        }
    }

    /**
     * 读取所有字节
     *
     * @param input    流
     * @param encoding 编码
     * @throws IOException IOException
     */
    public static List<String> readLines(final File input, final Charset encoding) throws IOException {
        final InputStreamReader reader = new InputStreamReader(new FileInputStream(input), toCharset(encoding));
        return readLines(reader);
    }

    /**
     * 读取所有字节
     *
     * @param input     流
     * @param separator 风分隔符
     * @throws IOException IOException
     */
    public static List<String[]> readLines(final Reader input, final String separator) throws IOException {
        final List<String[]> list;
        try (BufferedReader reader = toBufferedReader(input)) {
            list = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                String[] split = line.split(separator);
                list.add(ArrayUtils.trimOrSeparator(split, "\""));
                line = reader.readLine();
            }
        }
        return list;
    }

    /**
     * 读取所有字节
     *
     * @param input 流
     * @throws IOException IOException
     */
    public static List<String> readLines(final Reader input) throws IOException {
        final List<String> list;
        try (BufferedReader reader = toBufferedReader(input)) {
            list = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
        }
        return list;
    }

    /**
     * 重置流
     *
     * @param inputStream 流
     */
    public static void reset(InputStream inputStream) {
        if (null == inputStream) {
            return;
        }
        inputStream.mark(0);
        if (inputStream.markSupported()) {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reader 转 BufferedReader
     *
     * @param reader Reader
     * @return BufferedReader
     */
    public static BufferedReader toBufferedReader(final Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    /**
     * inputStream 转 BufferedReader
     *
     * @param inputStream Reader
     * @return BufferedReader
     */
    public static BufferedReader toBufferedReader(final InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, UTF_8));
    }

    /**
     * 流转字节
     *
     * @param input 输入流
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] toByteArray(final InputStream input) throws IOException {
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            copy(input, output);
            return output.toByteArray();
        }
    }

    /**
     * 流转字节
     *
     * @param input 输入流
     * @param size  数组长度
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] toByteArray(final InputStream input, final long size) throws IOException {

        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Size cannot be greater than Integer max value: " + size);
        }
        return toByteArray(input, (int) size);
    }

    /**
     * 流转字节
     *
     * @param input 输入流
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] toByteArrayKeepOpen(final InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /**
     * 流转字节
     *
     * @param input 输入流
     * @return byte[]
     * @throws IOException IOException
     */
    public static ByteArrayOutputStream toByteArrayOutputStream(final InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output;
    }

    /**
     * 流转字节
     *
     * @param input 输入流
     * @param size  数组长度
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] toByteArray(final InputStream input, final int size) throws IOException {
        if (size < 0) {
            throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
        }

        if (size == 0) {
            return new byte[0];
        }

        final byte[] data = new byte[size];
        int offset = 0;
        int read;

        while (offset < size && (read = input.read(data, offset, size - offset)) != EOF) {
            offset += read;
        }

        if (offset != size) {
            throw new IOException("Unexpected read size. current: " + offset + ", expected: " + size);
        }

        return data;
    }

    /**
     * 流转数组
     *
     * @param input 输入流
     * @return byte
     * @throws IOException IOException
     */
    public static byte[] toByteArray(final Reader input) throws IOException {
        return toByteArray(input, Charset.defaultCharset());
    }

    /**
     * URL转字节
     *
     * @param url url
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] toByteArray(final URL url) throws IOException {
        final URLConnection conn = url.openConnection();
        try {
            return toByteArray(conn);
        } finally {
            close(conn);
        }
    }

    /**
     * URI转字节
     *
     * @param uri uri
     * @return byte
     * @throws IOException IOException
     */
    public static byte[] toByteArray(final URI uri) throws IOException {
        return toByteArray(uri.toURL());
    }

    /**
     * URLConnection转字节
     *
     * @param urlConnection urlConnection
     * @return byte
     * @throws IOException IOException
     */
    public static byte[] toByteArray(final URLConnection urlConnection) throws IOException {
        try (InputStream inputStream = urlConnection.getInputStream()) {
            return toByteArray(inputStream);
        }
    }

    /**
     * 流转数组
     *
     * @param input    输入流
     * @param encoding 编码
     * @return byte
     * @throws IOException IOException
     */
    public static byte[] toByteArray(final Reader input, final Charset encoding) throws IOException {
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            copy(input, output, encoding);
            return output.toByteArray();
        }
    }

    /**
     * 流转字节
     *
     * @param openInputStream 输入流
     * @return ByteBuffer
     * @throws IOException IOException
     */
    public static ByteBuffer toByteBuffer(InputStream openInputStream) throws IOException {
        return ByteBuffer.wrap(toByteArray(openInputStream));
    }

    /**
     * 编码设置
     *
     * @param charset 编码
     * @return Charset
     */
    public static Charset toCharset(final Charset charset) {
        return charset == null ? Charset.defaultCharset() : charset;
    }

    /**
     * 编码设置
     *
     * @param charset 编码
     * @return Charset
     */
    public static Charset toCharset(final String charset) {
        return charset == null ? Charset.defaultCharset() : Charset.forName(charset);
    }

    /**
     * 字符串转流
     *
     * @param input    输入
     * @param encoding 编码
     * @return InputStream
     */
    public static InputStream toInputStream(final String input, final Charset encoding) {
        return new ByteArrayInputStream(input.getBytes(encoding));
    }

    /**
     * 字节数组转流
     *
     * @param bufferedImage 输入
     * @return InputStream
     */
    public static InputStream toInputStream(final BufferedImage bufferedImage) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "jpg", os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字节数组转流
     *
     * @param input 输入
     * @return InputStream
     */
    public static InputStream toInputStream(final byte[] input) {
        return new ByteArrayInputStream(input);
    }

    /**
     * 字节数组转流
     *
     * @param input 输入
     * @return InputStream
     */
    public static InputStream toInputStream(final File input) throws FileNotFoundException {
        return new FileInputStream(input);
    }

    /**
     * 字符串转流
     *
     * @param inputStream 参数
     * @param encoding    编码
     * @return InputStream
     * @throws IOException IOException
     */
    public static InputStreamReader toInputStreamReader(final InputStream inputStream, final Charset encoding) throws IOException {
        return new InputStreamReader(inputStream, encoding);
    }

    /**
     * 字符串转流
     *
     * @param url      参数
     * @param encoding 编码
     * @return InputStream
     * @throws IOException IOException
     */
    public static InputStreamReader toInputStreamReader(final URL url, final Charset encoding) throws IOException {
        return toInputStreamReader(url.openStream(), encoding);
    }

    /**
     * 字符串转流
     *
     * @param file     参数
     * @param encoding 编码
     * @return InputStream
     * @throws IOException IOException
     */
    public static InputStreamReader toInputStreamReader(final File file, final Charset encoding) throws IOException {
        return toInputStreamReader(new FileInputStream(file), encoding);
    }

    /**
     * 字符串转流
     *
     * @param inputStream 参数
     * @return InputStream
     * @throws IOException IOException
     */
    public static InputStreamReader toInputStreamReader(final InputStream inputStream) throws IOException {
        return toInputStreamReader(inputStream, UTF_8);
    }

    /**
     * 字符串转流
     *
     * @param url 参数
     * @return InputStream
     * @throws IOException IOException
     */
    public static InputStreamReader toInputStreamReader(final URL url) throws IOException {
        return toInputStreamReader(url.openStream(), UTF_8);
    }

    /**
     * 字符串转流
     *
     * @param file 参数
     * @return InputStream
     * @throws IOException IOException
     */
    public static InputStreamReader toInputStreamReader(final File file) throws IOException {
        return toInputStreamReader(new FileInputStream(file), UTF_8);
    }

    /**
     * url 转 list
     *
     * @param url 类
     * @return List
     */
    public static List<String> toList(URL url) throws IOException {
        return Splitter.on("\r\n").omitEmptyStrings().trimResults().splitToList(toString(url, UTF_8));
    }

    /**
     * url 转 list
     *
     * @param url     类
     * @param charset 编码
     * @return List
     */
    public static List<String> toList(URL url, String charset) throws IOException {
        String s = toString(url, charset);
        return Splitter.on("\r\n").omitEmptyStrings().trimResults().splitToList(s);
    }


    /**
     * 字节数组转字符串
     *
     * @param input 字节数组
     * @return String
     */
    public static String toString(final byte[] input) {
        return toString(input, UTF_8);
    }

    /**
     * 字节数组转字符串
     *
     * @param input   字节数组
     * @param charset 编码
     * @return String
     */
    public static String toString(final byte[] input, final Charset charset) {
        return new String(input, charset);
    }

    /**
     * url转字符串
     *
     * @param url      字节数组
     * @param encoding 编码
     * @return String
     * @throws IOException IOException
     */
    public static String toString(final URL url, final String encoding) throws IOException {
        return toString(url, toCharset(encoding));
    }

    /**
     * 字节数组转字符串
     *
     * @param input    字节数组
     * @param encoding 编码
     * @return String
     */
    public static String toString(final byte[] input, final String encoding) {
        return new String(input, toCharset(encoding));
    }

    /**
     * 流转字符串
     *
     * @param input    流
     * @param encoding 编码
     * @return String
     * @throws IOException IOException
     */
    public static String toString(final InputStream input, final Charset encoding) throws IOException {
        try (final Writer sw = new StringWriter()) {
            copy(input, sw, encoding);
            return sw.toString();
        }
    }

    /**
     * 流转字符串
     *
     * @param input    流
     * @param encoding 编码
     * @return String
     * @throws IOException IOException
     */
    public static String toString(final InputStream input, final String encoding) throws IOException {
        return toString(input, toCharset(encoding));
    }

    /**
     * 流转字符串
     *
     * @param input 流
     * @return String
     * @throws IOException IOException
     */
    public static String toString(final Reader input) throws IOException {
        try (final Writer sw = new StringWriter()) {
            copy(input, sw);
            return sw.toString();
        }
    }

    /**
     * 流转字符串
     *
     * @param file 流
     * @return String
     * @throws IOException IOException
     */
    public static String toString(final File file) throws IOException {
        return toString(file, UTF_8);
    }

    /**
     * 流转字符串
     *
     * @param file    流
     * @param charset 编码
     * @return String
     * @throws IOException IOException
     */
    public static String toString(final File file, Charset charset) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), charset);
             StringWriter sw = new StringWriter()
        ) {
            char[] chars = new char[4096];
            int line = 0;

            while ((line = reader.read(chars)) != -1) {
                sw.write(chars, 0, line);
            }
            sw.flush();
            return sw.toString();
        }
    }

    /**
     * 流转字符串
     *
     * @param path 流
     * @return String
     * @throws IOException IOException
     */
    public static String toString(final Path path) throws IOException {
        return toString(path.toFile(), UTF_8);
    }

    /**
     * URI 转字符串
     *
     * @param uri URI
     * @return String
     * @throws IOException IOException
     */
    public static String toString(final URI uri) throws IOException {
        return toString(uri, Charset.defaultCharset());
    }

    /**
     * URI 转字符串
     *
     * @param uri      URI
     * @param encoding 编码
     * @return String
     * @throws IOException IOException
     */
    public static String toString(final URI uri, final Charset encoding) throws IOException {
        return toString(uri.toURL(), toCharset(encoding));
    }

    /**
     * URL 转字符串
     *
     * @param url      URL
     * @param encoding 编码
     * @return String
     * @throws IOException IOException
     */
    public static String toString(final URL url, final Charset encoding) throws IOException {
        if (null == url) {
            return null;
        }

        try (InputStream inputStream = url.openStream()) {
            return toString(inputStream, encoding);
        }
    }

    /**
     * is转 reader
     *
     * @param inputStream 流
     * @return InputStreamReader
     */
    public static InputStreamReader toUtf8InputStreamReader(InputStream inputStream) throws IOException {
        return new InputStreamReader(inputStream, UTF_8);
    }

    /**
     * URL 转 reader
     *
     * @param url URL
     * @return InputStreamReader
     */
    public static InputStreamReader toUtf8InputStreamReader(URL url) throws IOException {
        return new InputStreamReader(url.openStream(), UTF_8);
    }

    /**
     * 写数据
     *
     * @param outputStream 数据
     * @param byteLength   流
     * @param function     回调
     * @throws IOException IOException
     */
    public static void write(final OutputStream outputStream, final int byteLength, final SafeFunction<byte[], Integer> function) throws IOException {
        int count;
        byte[] data = new byte[byteLength];
        while ((count = function.apply(data)) != -1) {
            outputStream.write(data, 0, count);
        }
    }

    /**
     * 写数据
     *
     * @param data     数据
     * @param output   流
     * @param encoding 编码
     * @param append   是否追加
     * @throws IOException IOException
     */
    public static void write(final String data, final File output, final Charset encoding, boolean append) throws IOException {
        FileUtils.forceMkdirParent(output);

        StandardOpenOption[] standardOpenOptions = new StandardOpenOption[0];
        if (append) {
            standardOpenOptions = new StandardOpenOption[]{StandardOpenOption.APPEND};
        }
        Files.write(output.toPath(), data.getBytes(encoding), standardOpenOptions);
    }

    /**
     * 写数据
     *
     * @param data     数据
     * @param output   流
     * @param encoding 编码
     * @throws IOException IOException
     */
    public static void write(final String data, final OutputStream output, final Charset encoding) throws IOException {
        if (data != null) {
            output.write(data.getBytes(toCharset(encoding)));
        }
    }

    /**
     * 将多部分内容写到流中，自动转换为字符串
     *
     * @param out        输出流
     * @param charset    写出的内容的字符集
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param contents   写入的内容，调用toString()方法，不包括不会自动换行
     * @throws RuntimeException IO异常
     * @since 3.0.9
     */
    public static void write(OutputStream out, Charset charset, boolean isCloseOut, Object... contents) throws RuntimeException {
        OutputStreamWriter osw = null;
        try {
            osw = getWriter(out, charset);
            for (Object content : contents) {
                if (content != null) {
                    osw.write(StringUtils.defaultString(Converter.convertIfNecessary(content, String.class), EMPTY));
                }
            }
            osw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (isCloseOut) {
                IoUtils.closeQuietly(osw);
            }
        }
    }
    /**
     * 将byte[]写到流中
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param content    写入的内容
     * @throws IOException IO异常
     */
    public static void write(OutputStream out, boolean isCloseOut, byte[] content) throws IOException {
        try {
            out.write(content);
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (isCloseOut) {
                closeQuietly(out);
            }
        }
    }
    // -------------------------------------------------------------------------------------------- out start

    /**
     * 写数据
     *
     * @param data   数据
     * @param output 流
     * @throws IOException IOException
     */
    public static void write(final byte[] data, final OutputStream output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    /**
     * 写数据
     *
     * @param inputStream 数据流
     * @param output      流
     * @throws IOException IOException
     */
    public static void write(final InputStream inputStream, final OutputStream output) throws IOException {
        if (inputStream != null) {
            int line = 0;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((line = inputStream.read(bytes)) > -1) {
                output.write(bytes, 0, line);
            }
        }
    }

    /**
     * 写数据
     *
     * @param data     数据
     * @param output   流
     * @param encoding 编码
     * @throws IOException IOException
     */
    public static void write(final String data, final OutputStream output, final String encoding) throws IOException {
        write(data, output, toCharset(encoding));
    }

    /**
     * 写数据
     *
     * @param data   数据
     * @param output 流
     * @throws IOException IOException
     */
    public static void write(final StringBuffer data, final Writer output) throws IOException {
        if (data != null) {
            output.write(data.toString());
        }
    }

    /**
     * 写数据
     *
     * @param data   数据
     * @param output 流
     * @throws IOException IOException
     */
    public static void write(final StringBuffer data, final OutputStream output) throws IOException {
        write(data, output, null);
    }

    /**
     * 写数据
     *
     * @param data     数据
     * @param output   流
     * @param encoding 编码
     * @throws IOException IOException
     */
    public static void write(final StringBuffer data, final OutputStream output, final String encoding) throws IOException {
        if (data != null) {
            output.write(data.toString().getBytes(toCharset(encoding)));
        }
    }

    /**
     * 写数据
     *
     * @param lines      数据
     * @param output     流
     * @param lineEnding 结尾
     * @throws IOException IOException
     */
    public static void writeLines(final Collection<?> lines, final String lineEnding, final OutputStream output) throws IOException {
        writeLines(lines, lineEnding, output, Charset.defaultCharset());
    }

    /**
     * 写数据
     *
     * @param lines      数据
     * @param output     流
     * @param lineEnding 结尾
     * @throws IOException IOException
     */
    public static void writeLines(final Collection<?> lines, String lineEnding, final OutputStream output,
                                  final Charset encoding) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = SYMBOL_LF;
        }
        final Charset cs = toCharset(encoding);
        for (final Object line : lines) {
            if (line != null) {
                output.write(line.toString().getBytes(cs));
            }
            output.write(lineEnding.getBytes(cs));
        }
    }

    /**
     * 获取文件类型
     *
     * @param stream 流
     * @return 类型
     */
    public static String getMimeType(InputStream stream) {
        String type = null;
        try {
            type = URLConnection.guessContentTypeFromStream(stream);
        } catch (IOException ignored) {
        }
        return type;
    }

    /**
     * 流转流
     *
     * @param inputStream 流
     * @param buffer      读取大小
     * @return 流
     */
    public static InputStream toInputStream(InputStream inputStream, int buffer) {
        int count;
        byte[] data = new byte[buffer];
        try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
            while ((count = inputStream.read(data)) != -1) {
                fos.write(data, 0, count);
            }
            fos.flush();
            return new ByteArrayInputStream(fos.toByteArray());
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * 流转流
     *
     * @param inputStream 流
     * @return 流
     */
    public static InputStream toInputStream(InputStream inputStream) {
        return toInputStream(inputStream, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 读取第一行
     *
     * @param reader 字符
     * @return 第一行
     */
    public static String readFirstLine(Reader reader) {
        try (BufferedReader stringReader = new BufferedReader(reader)) {
            return stringReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 写入字符串
     *
     * @param data   数据
     * @param writer 输出
     */
    public static void write(String data, Writer writer) {
        try (Writer writer1 = writer) {
            writer1.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析文件
     *
     * @param filepath 文件路径
     * @return 文件
     */
    public static InputStreamReader newFileReader(String filepath) throws IOException {
        return newFileReader(filepath, UTF_8);
    }

    /**
     * 解析文件
     *
     * @param filepath 文件路径
     * @param charset  编码
     * @return 文件
     */
    public static InputStreamReader newFileReader(String filepath, Charset charset) throws IOException {
        InputStream inputStream = newFileStream(filepath);
        if (null == inputStream) {
            return null;
        }
        return new InputStreamReader(inputStream, charset);
    }

    /**
     * 解析文件
     *
     * @param filepath 文件路径
     * @return 文件
     */
    public static InputStream newFileStream(String filepath) throws IOException {
        File file = new File(filepath);
        if (!file.exists()) {
            return null;
        }

        try {
            return java.nio.file.Files.newInputStream(file.toPath());
        } catch (IOException ignored) {
        }

        String extension = FileUtils.getExtension(file);
        if (XZ.equals(extension)) {
            return new XZInputStream(java.nio.file.Files.newInputStream(file.toPath()));
        }

        if (GZ.equals(extension)) {
            return new GZIPInputStream(java.nio.file.Files.newInputStream(file.toPath()));
        }

        return null;
    }

    /**
     * 解析文件
     *
     * @param classpath 文件路径
     * @return 文件
     */
    public static InputStreamReader newClassPathReader(String classpath) throws IOException {
        return newClassPathReader(classpath, UTF_8);
    }

    /**
     * 解析文件
     *
     * @param classpath 文件路径
     * @param charset   编码
     * @return 文件
     */
    public static InputStreamReader newClassPathReader(String classpath, Charset charset) throws IOException {
        InputStream inputStream = newClassPathStream(classpath);
        return null == inputStream ? null : new InputStreamReader(inputStream, charset);
    }

    /**
     * 解析文件
     *
     * @param classpath 文件路径
     * @return 文件
     */
    public static InputStream newClassPathStream(String classpath) throws IOException {
        InputStream is = IoUtils.class.getResourceAsStream(classpath);
        if (null != is) {
            return is;
        }

        is = IoUtils.class.getResourceAsStream(classpath + ".xz");

        if (null != is) {
            return new XZInputStream(is);
        }
        is = IoUtils.class.getResourceAsStream(classpath + ".gz");
        if (null != is) {
            return new GZIPInputStream(is);
        }

        return null;
    }

    /**
     * 解析文件
     *
     * @param classpath 文件路径
     * @return 文件
     */
    public static URL newUrl(String classpath) throws IOException {
        URL resource = IoUtils.class.getResource(classpath);
        if (null != resource) {
            return resource;
        }

        resource = IoUtils.class.getResource(classpath + ".xz");

        if (null != resource) {
            return resource;
        }
        resource = IoUtils.class.getResource(classpath + ".gz");
        if (null != resource) {
            return resource;
        }

        return null;
    }


    /**
     * 迭代器
     *
     * @author CH
     * @version 1.0.0
     * @since 2021/5/29
     */
    public static class LineIterator implements Iterator<String> {

        /**
         * 读取器。
         */
        private final BufferedReader bufferedReader;
        /**
         * 当前行.
         */
        private String cachedLine;
        /**
         * 迭代器是否结束.
         */
        private boolean finished = false;


        /**
         * 初始化
         *
         * @param reader 输入流
         * @throws IllegalArgumentException 参数异常
         */
        public LineIterator(final Reader reader) throws IllegalArgumentException {
            if (reader == null) {
                throw new IllegalArgumentException("Reader must not be null");
            }
            if (reader instanceof BufferedReader) {
                bufferedReader = (BufferedReader) reader;
            } else {
                bufferedReader = new BufferedReader(reader);
            }
        }

        @Override
        public boolean hasNext() {
            if (cachedLine != null) {
                return true;
            } else if (finished) {
                return false;
            } else {
                try {
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            finished = true;
                            return false;
                        } else if (isValidLine(line)) {
                            cachedLine = line;
                            return true;
                        }
                    }
                } catch (IOException ioe) {
                    close();
                    throw new IllegalStateException(ioe);
                }
            }
        }

        @Override
        public String next() {
            return nextLine();
        }

        /**
         * 下一行
         *
         * @return 行
         */
        public String nextLine() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more lines");
            }
            String currentLine = cachedLine;
            cachedLine = null;
            return currentLine;
        }

        /**
         * 有效行
         *
         * @param line 行
         * @return 是否有效
         */
        protected boolean isValidLine(String line) {
            return true;
        }

        /**
         * 关闭
         */
        public void close() {
            finished = true;
            IoUtils.closeQuietly(bufferedReader);
            cachedLine = null;
        }
    }

    /**
     * byte[] 转为{@link ByteArrayInputStream}
     *
     * @param file 内容bytes
     * @return 字节流
     * @since 4.1.8
     */
    public static FileInputStream toStream(File file) throws IOException {
        if (file == null) {
            return null;
        }
        return new FileInputStream(file);
    }

    /**
     * byte[] 转为{@link ByteArrayInputStream}
     *
     * @param content 内容bytes
     * @return 字节流
     * @since 4.1.8
     */
    public static ByteArrayInputStream toStream(byte[] content) {
        if (content == null) {
            return null;
        }
        return new ByteArrayInputStream(content);
    }

    /**
     * {@link ByteArrayOutputStream}转为{@link ByteArrayInputStream}
     *
     * @param out {@link ByteArrayOutputStream}
     * @return 字节流
     * @since 5.3.6
     */
    public static ByteArrayInputStream toStream(ByteArrayOutputStream out) {
        if (out == null) {
            return null;
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * 从流中读取前28个byte并转换为16进制，字母部分使用大写
     *
     * @param in {@link InputStream}
     * @return 16进制字符串
     * @throws IOException IO异常
     */
    public static String readHex28Upper(InputStream in) throws IOException {
        return readHex(in, 28, false);
    }

    /**
     * 从流中读取前28个byte并转换为16进制，字母部分使用小写
     *
     * @param in {@link InputStream}
     * @return 16进制字符串
     * @throws IOException IO异常
     */
    public static String readHex28Lower(InputStream in) throws IOException {
        return readHex(in, 28, true);
    }

    /**
     * 读取16进制字符串
     *
     * @param in          {@link InputStream}
     * @param length      长度
     * @param toLowerCase true 传换成小写格式 ， false 传换成大写格式
     * @return 16进制字符串
     * @throws IOException IO异常
     */
    public static String readHex(InputStream in, int length, boolean toLowerCase) throws IOException {
        String hexString = Hex.encodeHexString(readBytes(in, length));
        if (toLowerCase) {
            return hexString.toLowerCase();
        }
        return hexString.toUpperCase();
    }

    /**
     * 读取指定长度的byte数组，不关闭流
     *
     * @param in     {@link InputStream}，为null返回null
     * @param length 长度，小于等于0返回空byte数组
     * @return bytes
     * @throws IOException IO异常
     */
    public static byte[] readBytes(InputStream in, int length) throws IOException {
        if (null == in) {
            return null;
        }
        if (length <= 0) {
            return new byte[0];
        }

        byte[] b = new byte[length];
        int readLength;
        try {
            readLength = in.read(b);
        } catch (IOException e) {
            throw new IOException(e);
        }
        if (readLength > 0 && readLength < length) {
            byte[] b2 = new byte[readLength];
            System.arraycopy(b, 0, b2, 0, readLength);
            return b2;
        } else {
            return b;
        }
    }

}
