package com.chua.common.support.utils;

import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.net.URLDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Consumer;
import java.util.zip.ZipFile;

import static com.chua.common.support.constant.CommonConstant.*;
import static com.chua.common.support.lang.net.URLEncoder.*;
import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * url工具类<br />
 * 部分工具来自于HuTool系列
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/19
 */
@Slf4j
public class UrlUtils {
    /**
     * Data URI Scheme封装，数据格式为Base64。data URI scheme 允许我们使用内联（inline-code）的方式在网页中包含数据，<br>
     * 目的是将一些小的数据，直接嵌入到网页中，从而不用再从外部文件载入。常用于将图片嵌入网页。
     *
     * <p>
     * Data URI的格式规范：
     * <pre>
     *     data:[&lt;mime type&gt;][;charset=&lt;charset&gt;][;&lt;encoding&gt;],&lt;encoded data&gt;
     * </pre>
     *
     * @param mimeType 可选项（null表示无），数据类型（image/png、text/plain等）
     * @param data     编码后的数据
     * @return Data URI字符串
     * @since 5.3.11
     */
    public static String getDataUriBase64(String mimeType, String data) {
        return getDataUri(mimeType, null, "base64", data);
    }

    /**
     * Data URI Scheme封装。data URI scheme 允许我们使用内联（inline-code）的方式在网页中包含数据，<br>
     * 目的是将一些小的数据，直接嵌入到网页中，从而不用再从外部文件载入。常用于将图片嵌入网页。
     *
     * <p>
     * Data URI的格式规范：
     * <pre>
     *     data:[&lt;mime type&gt;][;charset=&lt;charset&gt;][;&lt;encoding&gt;],&lt;encoded data&gt;
     * </pre>
     *
     * @param mimeType 可选项（null表示无），数据类型（image/png、text/plain等）
     * @param encoding 数据编码方式（US-ASCII，BASE64等）
     * @param data     编码后的数据
     * @return Data URI字符串
     * @since 5.3.6
     */
    public static String getDataUri(String mimeType, String encoding, String data) {
        return getDataUri(mimeType, null, encoding, data);
    }

    /**
     * Data URI Scheme封装。data URI scheme 允许我们使用内联（inline-code）的方式在网页中包含数据，<br>
     * 目的是将一些小的数据，直接嵌入到网页中，从而不用再从外部文件载入。常用于将图片嵌入网页。
     *
     * <p>
     * Data URI的格式规范：
     * <pre>
     *     data:[&lt;mime type&gt;][;charset=&lt;charset&gt;][;&lt;encoding&gt;],&lt;encoded data&gt;
     * </pre>
     *
     * @param mimeType 可选项（null表示无），数据类型（image/png、text/plain等）
     * @param charset  可选项（null表示无），源文本的字符集编码方式
     * @param encoding 数据编码方式（US-ASCII，BASE64等）
     * @param data     编码后的数据
     * @return Data URI字符串
     * @since 5.3.6
     */
    public static String getDataUri(String mimeType, Charset charset, String encoding, String data) {
        final StringBuilder builder = new StringBuilder("data:");
        if (!StringUtils.isNullOrEmpty(mimeType)) {
            builder.append(mimeType);
        }

        if (null != charset) {
            builder.append(";charset=").append(charset.name());
        }

        if (!StringUtils.isNullOrEmpty(encoding)) {
            builder.append(';').append(encoding);
        }

        builder.append(',').append(data);

        return builder.toString();
    }


    /**
     * 编码URL，默认使用UTF-8编码<br>
     * 将需要转换的内容（ASCII码形式之外的内容），用十六进制表示法转换出来，并在之前加上%开头。<br>
     * 此方法用于URL自动编码，类似于浏览器中键入地址自动编码，对于像类似于“/”的字符不再编码
     *
     * @param url URL
     * @return 编码后的URL
     * @since 3.1.2
     */
    public static String encode(String url) {
        return SimpleUrlEncoder.DEFAULT.encode(url);
    }
    /**
     * 编码URL<br>
     * 将需要转换的内容（ASCII码形式之外的内容），用十六进制表示法转换出来，并在之前加上%开头。
     *
     * @param url     URL
     * @param charset 编码，为null表示不编码
     * @return 编码后的URL
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    public static String encodeAll(String url, Charset charset) throws UnsupportedEncodingException {
        return ALL.encode(url, charset);
    }
    /**
     * 单独编码URL中的空白符，空白符编码为%20
     *
     * @param urlStr URL字符串
     * @return 编码后的字符串
     * @since 4.5.14
     */
    public static String encodeBlank(CharSequence urlStr) {
        if (urlStr == null) {
            return null;
        }

        int len = urlStr.length();
        final StringBuilder sb = new StringBuilder(len);
        char c;
        for (int i = 0; i < len; i++) {
            c = urlStr.charAt(i);
            if (Character.isWhitespace(c) || Character.isSpaceChar(c) || c == '\ufeff' || c == '\u202a') {
                sb.append("%20");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    /**
     * 解码URL<br>
     * 将%开头的16进制表示的内容解码。
     *
     * @param url URL
     * @return 解码后的URL
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     * @since 3.1.2
     */
    public static String decode(String url) throws UnsupportedEncodingException {
        return decode(url, UTF_8);
    }

    /**
     * 解码application/x-www-form-urlencoded字符<br>
     * 将%开头的16进制表示的内容解码。
     *
     * @param content 被解码内容
     * @param charset 编码，null表示不解码
     * @return 编码后的字符
     * @since 4.4.1
     */
    public static String decode(String content, Charset charset) {
        if (null == charset) {
            return content;
        }
        return com.chua.common.support.lang.net.URLDecoder.decode(content, charset);
    }

    /**
     * 解码application/x-www-form-urlencoded字符<br>
     * 将%开头的16进制表示的内容解码。
     *
     * @param content       被解码内容
     * @param charset       编码，null表示不解码
     * @param isPlusToSpace 是否+转换为空格
     * @return 编码后的字符
     * @since 5.6.3
     */
    public static String decode(String content, Charset charset, boolean isPlusToSpace) {
        if (null == charset) {
            return content;
        }
        return URLDecoder.decode(content, charset, isPlusToSpace);
    }

    /**
     * 解码application/x-www-form-urlencoded字符<br>
     * 将%开头的16进制表示的内容解码。
     *
     * @param content URL
     * @param charset 编码
     * @return 解码后的URL
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    public static String decode(String content, String charset) throws UnsupportedEncodingException {
        return decode(content, Charset.forName(charset));
    }

    /**
     * 从URL对象中获取不被编码的路径Path<br>
     * 对于本地路径，URL对象的getPath方法对于包含中文或空格时会被编码，导致本读路径读取错误。<br>
     * 此方法将URL转为URI后获取路径用于解决路径被编码的问题
     *
     * @param url {@link URL}
     * @return 路径
     * @since 3.0.8
     */
    public static String getDecodedPath(URL url) {
        if (null == url) {
            return null;
        }

        String path = null;
        // URL对象的getPath方法对于包含中文或空格的问题
        path = Objects.requireNonNull(toUri(url)).getPath();
        return (null != path) ? path : url.getPath();
    }

    /**
     * 获取文件
     *
     * @param resourceLocation 资源路径
     * @return 文件
     * @throws FileNotFoundException 异常
     */
    public static File getFile(String resourceLocation) throws FileNotFoundException {
        if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
            String path = resourceLocation.substring("classpath:".length());
            String description = "class path resource [" + path + CommonConstant.SYMBOL_RIGHT_SQUARE_BRACKET;
            ClassLoader cl = ClassUtils.getDefaultClassLoader();
            URL url = cl != null ? cl.getResource(path) : ClassLoader.getSystemResource(path);
            if (url == null) {
                throw new FileNotFoundException(description + " cannot be resolved to absolute file path because it does not exist");
            } else {
                return getFile(url, description);
            }
        } else {
            try {
                return getFile(new URL(resourceLocation));
            } catch (MalformedURLException var5) {
                return new File(resourceLocation);
            }
        }
    }

    /**
     * 从url获取文件
     *
     * @param resourceUrl url
     * @return File
     * @throws FileNotFoundException FileNotFoundException
     */
    public static File getFile(URL resourceUrl) throws FileNotFoundException {
        return getFile(resourceUrl, "URL");
    }

    /**
     * file://
     *
     * @param resourceUrl url
     * @param description 文件描述
     * @return File
     * @throws FileNotFoundException FileNotFoundException
     */
    public static File getFile(URL resourceUrl, String description) throws FileNotFoundException, NullPointerException {
        if (!FILE.equals(resourceUrl.getProtocol())) {
            throw new FileNotFoundException(description + " cannot be resolved to absolute file path because it does not reside in the file system: " + resourceUrl);
        } else {
            try {
                return new File(toUri(resourceUrl).getSchemeSpecificPart());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取uri中的文件
     *
     * @param resourceUri uri
     * @return 文件
     * @throws FileNotFoundException FileNotFoundException
     */
    public static File getFile(URI resourceUri) throws FileNotFoundException {
        return getFile(resourceUri, "URI");
    }

    /**
     * file://
     *
     * @param resourceUri uri
     * @param description 文件描述
     * @return 文件
     * @throws FileNotFoundException FileNotFoundException
     */
    public static File getFile(URI resourceUri, String description) throws FileNotFoundException {
        if (!FILE.equals(resourceUri.getScheme())) {
            throw new FileNotFoundException(description + " cannot be resolved to absolute file path because it does not reside in the file system: " + resourceUri);
        }
        return new File(resourceUri.getSchemeSpecificPart());
    }

    /**
     * 获取url
     *
     * @param resourceLocation 资源文件地址
     * @return URL
     * @throws FileNotFoundException FileNotFoundException
     */
    public static URL getUrl(String resourceLocation) throws FileNotFoundException {
        if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
            String path = resourceLocation.substring("classpath:".length());
            ClassLoader cl = ClassUtils.getDefaultClassLoader();
            URL url = cl != null ? cl.getResource(path) : ClassLoader.getSystemResource(path);
            if (url == null) {
                String description = "class path resource [" + path + CommonConstant.SYMBOL_RIGHT_SQUARE_BRACKET;
                throw new FileNotFoundException(description + " cannot be resolved to URL because it does not exist");
            }
            return url;
        } else {
            try {
                return new URL(resourceLocation);
            } catch (MalformedURLException var6) {
                try {
                    return (new File(resourceLocation)).toURI().toURL();
                } catch (MalformedURLException var5) {
                    throw new FileNotFoundException("Resource location [" + resourceLocation + "] is neither a URL not a well-formed file path");
                }
            }
        }
    }

    /**
     * 是否是 jar
     *
     * @param url url
     * @return 是jar类压缩包返回true
     */
    public static boolean isAllJar(URL url) {
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_WAR.equals(protocol) ||
                URL_PROTOCOL_ZIP.equals(protocol) || URL_PROTOCOL_VFSZIP.equals(protocol) ||
                URL_PROTOCOL_WSJAR.equals(protocol));
    }

    /**
     * 是否是 jar
     *
     * @param path url
     * @return
     */
    public static boolean isAllJar(String path) {
        String extension = FileUtils.getExtension(path);
        return (URL_PROTOCOL_JAR.equals(extension) || URL_PROTOCOL_WAR.equals(extension) ||
                URL_PROTOCOL_ZIP.equals(extension) || URL_PROTOCOL_VFSZIP.equals(extension) ||
                URL_PROTOCOL_WSJAR.equals(extension));
    }

    /**
     * 是否是file
     *
     * @param url url
     * @return 是否是file
     */
    public static boolean isFile(URL url) {
        return null != url && URL_PROTOCOL_FILE.equals(url.getProtocol());
    }

    /**
     * 是否是file jar
     *
     * @param url url
     * @return
     */
    public static boolean isFileJar(URL url) {
        return isFile(url) && url.toExternalForm().endsWith(JAR_FILE_EXTENSION);
    }

    /**
     * file://
     *
     * @param url url
     * @return boolean
     */
    public static boolean isFileUrl(URL url) {
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_FILE.equals(protocol) || URL_PROTOCOL_VFSFILE.equals(protocol) ||
                URL_PROTOCOL_VFS.equals(protocol));
    }

    /**
     * 是否是file
     *
     * @param url url
     * @return 是否是file
     */
    public static boolean isJar(URL url) {
        return null != url && URL_PROTOCOL_JAR.equals(url.getProtocol());
    }

    /**
     * 是否是jar url
     *
     * @param url url
     * @return boolean
     */
    public static boolean isJarFileUrl(URL url) {
        return (URL_PROTOCOL_FILE.equals(url.getProtocol()) &&
                url.getPath().toLowerCase().endsWith(JAR_FILE_EXTENSION));
    }

    /**
     * 是否是jar://
     *
     * @param url url
     * @return boolean
     */
    public static boolean isJarUrl(URL url) {
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_WAR.equals(protocol) ||
                URL_PROTOCOL_ZIP.equals(protocol) || URL_PROTOCOL_VFSZIP.equals(protocol) ||
                URL_PROTOCOL_WSJAR.equals(protocol));
    }

    /**
     * url格式校验
     *
     * @param url url
     * @return 是url返回true
     */
    public static boolean isUrl(String url) {
        return url != null && url.trim().length() > 0 && url.startsWith("http");
    }

    /**
     * 是否是 war
     *
     * @param url url
     * @return
     */
    public static boolean isWar(URL url) {
        return null != url && URL_PROTOCOL_WAR.equals(url.getProtocol());
    }

    /**
     * 是否是 wsjar
     *
     * @param url url
     * @return
     */
    public static boolean isWsJar(URL url) {
        return null != url && URL_PROTOCOL_WSJAR.equals(url.getProtocol());
    }

    /**
     * 大小
     *
     * @param url url
     * @return 修改时间
     */
    public static long size(URL url) {
        try {
            return url.openConnection().getContentLengthLong();
        } catch (IOException e) {
            return -1L;
        }
    }

    /**
     * 大小
     *
     * @param file url
     * @return 修改时间
     */
    public static long size(String file) {
        try {
            return new URL(file).openConnection().getContentLengthLong();
        } catch (IOException e) {
            return -1L;
        }
    }

    /**
     * 类型
     *
     * @param file url
     * @return 修改时间
     */
    public static String getContentType(String file) {
        try {
            return new URL(file).openConnection().getContentType();
        } catch (IOException e) {
            return UNKNOWN;
        }
    }

    /**
     * 修改时间
     *
     * @param file url
     * @return 修改时间
     */
    public static long lastModified(String file) {
        File file2 = Converter.convertIfNecessary(file, File.class);
        if(null != file2 && file2.exists()) {
            return file2.lastModified();
        }
        try {
            return new URL(file).openConnection().getLastModified();
        } catch (IOException e) {
            return -1L;
        }
    }

    /**
     * 标准化URL字符串，包括：
     *
     * <pre>
     * 1. 多个/替换为一个
     * </pre>
     *
     * @param url          URL字符串
     * @param isEncodePath 是否对URL中path部分的中文和特殊字符做转义（不包括 http:, /和域名部分）
     * @return 标准化后的URL字符串
     * @since 4.4.1
     */
    public static String normalize(String url, boolean isEncodePath) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        final int sepIndex = url.indexOf("://");
        String protocol;
        String body;
        if (sepIndex > 0) {
            protocol = StringUtils.subPre(url, sepIndex + 3);
            body = StringUtils.subSuf(url, sepIndex + 3);
        } else {
            protocol = "http://";
            body = url;
        }

        final int paramsSepIndex = StringUtils.indexOf(body, '?');
        String params = null;
        if (paramsSepIndex > 0) {
            params = StringUtils.subSuf(body, paramsSepIndex);
            body = StringUtils.subPre(body, paramsSepIndex);
        }

        if (!StringUtils.isBlank(body)) {
            // 去除开头的\或者/
            body = body.replaceAll("^[\\\\/]+", SYMBOL_EMPTY);
            // 替换多个\或/为单个/
            body = body.replace("\\", "/").replaceAll("//+", "/");
        }

        final int pathSepIndex = StringUtils.indexOf(body, '/');
        String domain = body;
        String path = null;
        if (pathSepIndex > 0) {
            domain = StringUtils.subPre(body, pathSepIndex);
            path = StringUtils.subSuf(body, pathSepIndex);
        }
        if (isEncodePath) {
            path = encode(path);
        }
        return protocol + domain + StringUtils.nullToEmpty(path) + StringUtils.nullToEmpty(params);
    }

    /**
     * 获得URL
     *
     * @param path 相对给定 class所在的路径
     * @return URL
     */
    public static URL parseFileUrl(final String path) {
        URL url = null;
        File file = new File(path);
        if (file.exists()) {
            try {
                url = file.toURI().toURL();
            } catch (MalformedURLException ignore) {
            }
        }

        if (null == url) {
            ClassLoader classLoader = Optional.ofNullable(Thread.currentThread().getContextClassLoader()).orElse(UrlUtils.class.getClassLoader());
            return classLoader.getResource(path);
        }
        return url;
    }

    /**
     * 获得URL，常用于使用绝对路径时的情况
     *
     * @param file URL对应的文件对象
     * @return URL
     * @throws MalformedURLException MalformedURLException
     */
    public static URL parseFileUrl(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将URL字符串转换为URL对象，并做必要验证
     *
     * @param urlStr URL字符串
     * @return URL
     * @since 4.1.9
     */
    public static URL parseUrl(final String urlStr) {
        // 编码空白符，防止空格引起的请求异常
        try {
            return new URL(null, encodeBlank(urlStr));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得URL
     *
     * @param path    url路径
     * @param subPath 相对路径
     * @return URL
     */
    public static URL parseUrl(final String path, final String... subPath) {
        if (null == path) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(path);
        if (!path.endsWith(SYMBOL_LEFT_SLASH)) {
            stringBuilder.append(SYMBOL_LEFT_SLASH);
        }
        for (String s : subPath) {
            stringBuilder.append(s);
            if (!s.endsWith(SYMBOL_LEFT_SLASH)) {
                stringBuilder.append(SYMBOL_LEFT_SLASH);
            }
        }
        String preUrl = stringBuilder.toString();
        if (preUrl.endsWith(SYMBOL_LEFT_SLASH)) {
            preUrl = preUrl.substring(0, preUrl.length() - 1);
        }
        return parseUrl(preUrl);
    }

    /**
     * 拼接url
     *
     * @param url url
     * @return URL
     */
    public static URL toFile(String url) {
        try {
            return null == url ? null : new URL(FILE_URL_PREFIX + "/" + url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * 拼接url
     *
     * @param url url
     * @return URL
     */
    public static URL toJar(String url) {
        try {
            return null == url ? null : new URL(JAR_URL_PREFIX + "/" + url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * 获取 jar url
     *
     * @param url
     * @return
     */
    public static URL toJarFile(URL url) {
        if (isFileJar(url)) {
            try {
                return new URL(JAR_URL_PREFIX + url.toExternalForm() + JAR_URL_SEPARATOR);
            } catch (MalformedURLException e) {
                return null;
            }
        } else if (isJar(url)) {
            return url;
        } else if (isWar(url)) {
            return url;
        }
        return url;
    }

    /**
     * List<URL> -> List<String>
     *
     * @param urlList url集合
     * @return 字符串集合
     */
    public static List<String> toStrings(List<URL> urlList) {
        if (urlList.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>(urlList.size());
        for (URL url : urlList) {
            result.add(url.getFile());
        }
        return result;
    }

    /**
     * url转uri
     *
     * @param url url
     * @return URI
     * @throws URISyntaxException 异常
     */
    public static URI toUri(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转uri
     *
     * @param location 字符串
     * @return URI URI
     * @see URISyntaxException 异常
     */
    public static URI toUri(String location) {
        try {
            return new URI(StringUtils.replace(location, " ", "%20"));
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * 转URL为URI
     *
     * @param url      URL
     * @param isEncode 是否编码参数中的特殊字符（默认UTF-8编码）
     * @return URI
     * @throws IOException 包装URISyntaxException
     * @since 4.6.9
     */
    public static URI toUri(URL url, boolean isEncode) throws IOException {
        if (null == url) {
            return null;
        }

        return toUri(url.toString(), isEncode);
    }

    /**
     * 转字符串为URI
     *
     * @param location 字符串路径
     * @param isEncode 是否编码参数中的特殊字符（默认UTF-8编码）
     * @return URI
     * @throws IOException 包装URISyntaxException
     * @since 4.6.9
     */
    public static URI toUri(String location, boolean isEncode) throws IOException {
        if (isEncode) {
            location = encode(location);
        }
        try {
            return new URI(location);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    /**
     * 拼接url
     *
     * @param url url
     * @return url
     */
    public static URL toUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            try {
                return new File(url).toURI().toURL();
            } catch (MalformedURLException e1) {
                return null;
            }
        }
    }

    /**
     * 拼接url
     *
     * @param protocol 协议
     * @param host     地址
     * @param port     端口
     * @param path     地址
     * @return String
     */
    public static String toUrl(String protocol, String host, int port, String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append("://");
        sb.append(host).append(':').append(port);
        if (path.charAt(0) != SYMBOL_LEFT_SLASH_CHAR) {
            sb.append(SYMBOL_LEFT_SLASH_CHAR);
        }
        sb.append(path);
        return sb.toString();
    }

    /**
     * 字符串转 url
     *
     * @param strings 字符串
     * @return List<URL> URL集合
     */
    public static List<URL> toUrl(String[] strings) {
        if (null == strings) {
            return Collections.emptyList();
        }
        return toUrl(Arrays.asList(strings));
    }

    /**
     * 字符串转 url
     *
     * @param strings 字符串
     * @return List<URL> URL集合
     */
    public static List<URL> toUrl(List<String> strings) {
        if (strings.isEmpty()) {
            return Collections.emptyList();
        }
        List<URL> urls = new ArrayList<>(strings.size());
        for (String s : strings) {
            try {
                urls.add(new URL(s));
            } catch (MalformedURLException e) {
                try {
                    urls.add(new File(s).toURI().toURL());
                } catch (MalformedURLException ignore) {
                }
            }
        }
        return urls;
    }

    /**
     * 批量转化字符串为URL
     *
     * @param strings 批量字符串
     * @return URL[]
     */
    public static URL[] toUrls(String[] strings) {
        List<URL> urls = new ArrayList<>();
        for (String s : strings) {
            try {
                urls.add(new File(s).toURI().toURL());
            } catch (MalformedURLException ignored) {
            }
        }
        return urls.toArray(new URL[0]);
    }

    /**
     * file转url
     *
     * @param file 文件
     * @return url
     */
    public static URL[] toUrls(File[] file) {
        List<URL> urls = new ArrayList<>();
        for (File file1 : file) {
            try {
                urls.add(file1.toURI().toURL());
            } catch (MalformedURLException ignored) {
            }
        }
        return urls.toArray(new URL[0]);
    }

    /**
     * 拼接url
     *
     * @param url url
     * @return URL
     */
    public static URL toWar(String url) {
        try {
            return null == url ? null : new URL(WAR_URL_PREFIX + "/" + url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * 遍历
     *
     * @param url  url
     * @param name 名称
     */
    public static void forEach(URL url, Consumer<String> name) {
        if (UrlUtils.isAllJar(url)) {
            doFindPathMatchingJarResources(url, name);
        } else {
            doFindPathMatchingResources(url, name);
        }
    }

    /**
     * 查找Jar中的文件
     *
     * @param url  url
     * @param name 名称
     */
    private static void doFindPathMatchingJarResources(URL url, Consumer<String> name) {
        ZipFile jarFile = null;
        try {
            URLConnection urlConnection = url.openConnection();
            if (urlConnection instanceof JarURLConnection) {
                urlConnection.setUseCaches(false);
                jarFile = ((JarURLConnection) urlConnection).getJarFile();
            }
        } catch (Throwable ignore) {
        }

        if (null == jarFile) {
            return;
        }

        try (ZipFile closeJarFile = jarFile) {
            closeJarFile.stream().forEach(jarEntry -> {
                name.accept(jarEntry.getName());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析文件夹下的文件
     *
     * @param url  url
     * @param name 名称
     */
    private static void doFindPathMatchingResources(URL url, Consumer<String> name) {
        File file = new File(url.getFile());
        if (file.isFile()) {
            return;
        }

        Path path1 = Paths.get(file.getAbsolutePath());
        try {
            Files.walkFileTree(path1, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toFile().isFile()) {
                        String absolutePath = file.getFileName().toString();
                        name.accept(absolutePath);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ignored) {
        }
    }

    /**
     * 创建url
     *
     * @param resourceUrl 资源
     * @return url
     * @throws IOException ex
     */
    public static URL createUrl(String resourceUrl) throws IOException {
        File file = new File(resourceUrl);
        if (file.exists()) {
            return file.toURI().toURL();
        }

        try {
            return new URL(resourceUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 文件名称
     * @param connection 链接
     * @return 名称
     */
    public static String getFileName(URLConnection connection) {
        String headerField = connection.getHeaderField("Content-Disposition");
        if (StringUtils.isNullOrEmpty(headerField)) {
            String form = connection.getURL().toExternalForm();
            int index = form.indexOf("?");
            form = -1 == index ? form : form.substring(0, index);
            return FileUtils.getName(form);
        }

        return headerField.substring("attachment; filename=".length());
    }


    static class SimpleUrlEncoder {
        /**
         * 默认{@link SimpleUrlEncoder}<br>
         * 默认的编码器针对URI路径编码，定义如下：
         *
         * <pre>
         * pchar = unreserved（不处理） / pct-encoded / sub-delims（子分隔符） / ":" / "@"
         * unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"
         * sub-delims = "!" / "$" / "&amp;" / "'" / "(" / ")" / CommonConstant.SYMBOL_ASTERISK / "+" / "," / ";" / "="
         * </pre>
         */
        public static final SimpleUrlEncoder DEFAULT = createDefault();
        /**
         * 存放安全编码
         */
        private final BitSet safeCharacters;

        /**
         * 构造<br>
         * <p>
         * [a-zA-Z0-9]默认不被编码
         */
        public SimpleUrlEncoder() {
            this(new BitSet(256));

            for (char i = LETTER_LOWERCASE_A; i <= LETTER_LOWERCASE_Z; i++) {
                addCharacter(i);
            }
            for (char i = LETTER_UPPERCASE_A; i <= LETTER_UPPERCASE_Z; i++) {
                addCharacter(i);
            }
            for (char i = LETTER_ZERO; i <= LETTER_NIGHT; i++) {
                addCharacter(i);
            }
        }

        /**
         * 构造
         *
         * @param safeCharacters 安全字符，安全字符不被编码
         */
        private SimpleUrlEncoder(BitSet safeCharacters) {
            this.safeCharacters = safeCharacters;
        }

        /**
         * 创建默认{@link SimpleUrlEncoder}<br>
         * 默认的编码器针对URI路径编码，定义如下：
         *
         * <pre>
         * pchar = unreserved（不处理） / pct-encoded / sub-delims（子分隔符） / ":" / "@"
         * unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"
         * sub-delims = "!" / "$" / "&amp;" / "'" / "(" / ")" / CommonConstant.SYMBOL_ASTERISK / "+" / "," / ";" / "="
         * </pre>
         *
         * @return {@link SimpleUrlEncoder}
         */
        public static SimpleUrlEncoder createDefault() {
            final SimpleUrlEncoder encoder = new SimpleUrlEncoder();
            encoder.addCharacter('-');
            encoder.addCharacter('.');
            encoder.addCharacter('_');
            encoder.addCharacter('~');
            // Add the sub-delims
            encoder.addCharacter('!');
            encoder.addCharacter('$');
            encoder.addCharacter('&');
            encoder.addCharacter('\'');
            encoder.addCharacter('(');
            encoder.addCharacter(')');
            encoder.addCharacter('*');
            encoder.addCharacter('+');
            encoder.addCharacter(',');
            encoder.addCharacter(';');
            encoder.addCharacter('=');
            // Add the remaining literals
            encoder.addCharacter(':');
            encoder.addCharacter('@');
            // Add '/' so it isn't encoded when we encode a path
            encoder.addCharacter('/');

            return encoder;
        }

        /**
         * 增加安全字符<br>
         * 安全字符不被编码
         *
         * @param c 字符
         */
        public void addCharacter(char c) {
            safeCharacters.set(c);
        }

        public String encode(String source) {
            final StringBuilder rewrittenPath = new StringBuilder(source.length());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(buf, UTF_8);

            int c;
            for (int i = 0; i < source.length(); i++) {
                c = source.charAt(i);
                //是否编码空格为+
                boolean encodeSpaceAsPlus = false;
                if (safeCharacters.get(c)) {
                    rewrittenPath.append((char) c);
                } else if (encodeSpaceAsPlus && c == ' ') {
                    // 对于空格单独处理
                    rewrittenPath.append('+');
                } else {
                    // convert to external encoding before hex conversion
                    try {
                        writer.write((char) c);
                        writer.flush();
                    } catch (IOException e) {
                        buf.reset();
                        continue;
                    }

                    byte[] ba = buf.toByteArray();
                    for (byte toEncode : ba) {
                        // Converting each byte in the buffer
                        rewrittenPath.append('%');
                        Hex.appendHex(rewrittenPath, toEncode, false);
                    }
                    buf.reset();
                }
            }
            return rewrittenPath.toString();
        }
    }



    /**
     * 将URL字符串转换为URL对象，并做必要验证
     *
     * @param urlStr URL字符串
     * @return URL
     * @since 4.1.9
     */
    public static URL toUrlForHttp(String urlStr) {
        return toUrlForHttp(urlStr, null);
    }

    /**
     * 将URL字符串转换为URL对象，并做必要验证
     *
     * @param urlStr  URL字符串
     * @param handler {@link URLStreamHandler}
     * @return URL
     * @since 4.1.9
     */
    public static URL toUrlForHttp(String urlStr, URLStreamHandler handler) {
        // 编码空白符，防止空格引起的请求异常
        urlStr = encodeBlank(urlStr);
        try {
            return new URL(null, urlStr, handler);
        } catch (MalformedURLException e) {
            try {
                throw new UnsupportedEncodingException();
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * 编码字符为URL中查询语句<br>
     * 将需要转换的内容（ASCII码形式之外的内容），用十六进制表示法转换出来，并在之前加上%开头。<br>
     * 此方法用于POST请求中的请求体自动编码，转义大部分特殊字符
     *
     * @param url     被编码内容
     * @param charset 编码
     * @return 编码后的字符
     * @since 4.4.1
     */
    public static String encodeQuery(String url, Charset charset) {
        return QUERY.encode(url, charset);
    }

}
