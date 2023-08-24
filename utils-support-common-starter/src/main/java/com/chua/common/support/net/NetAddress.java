package com.chua.common.support.net;

import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.lang.net.UrlPath;
import com.chua.common.support.lang.net.UrlQuery;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.UrlUtils;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static com.chua.common.support.constant.CommonConstant.*;
import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * 地址
 *
 * <p>
 * foo://example.com:8042/over/there?name=ferret#nose
 * \_/   \______________/\_________/ \_________/ \__/
 * |           |            |            |        |
 * scheme     authority       path        query   fragment
 * |   _____________________|__
 * / \ /                        \
 * urn:example:animal:ferret:nose
 * </p>
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/3/25
 */
@Data
@Accessors(chain = true)
public class NetAddress implements Serializable {

    private static final String PROTOCOL_SYMBOL = "://";
    private static final String HOST_SYMBOL = ":";
    public static final Map<String, Integer> DEFAULT_PORT = new ConcurrentHashMap<>();
    /**
     * 地址
     */
    public static final Pattern ADDRESS = Pattern.compile("([1-9]|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])(\\\\.(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])){3}");


    static {
        DEFAULT_PORT.put("ftp", 21);
        DEFAULT_PORT.put("sftp", 22);
        DEFAULT_PORT.put("telnet", 23);
        DEFAULT_PORT.put("smtp", 25);
        DEFAULT_PORT.put("tftp", 29);
        DEFAULT_PORT.put("http", 80);
        DEFAULT_PORT.put("pop3", 110);
        DEFAULT_PORT.put("nntp", 119);
        DEFAULT_PORT.put("imap4", 143);
        DEFAULT_PORT.put("snmp", 161);
        DEFAULT_PORT.put("https", 443);
        DEFAULT_PORT.put("telnets", 992);
        DEFAULT_PORT.put("imaps", 993);
        DEFAULT_PORT.put("rmi", 1099);
        DEFAULT_PORT.put("sql-server", 1433);
        DEFAULT_PORT.put("oracle", 1521);
        DEFAULT_PORT.put("derby", 1527);
        DEFAULT_PORT.put("zookeeper", 2181);
        DEFAULT_PORT.put("mysql", 3306);
        DEFAULT_PORT.put("sybase", 5000);
        DEFAULT_PORT.put("postgrsql", 5432);
        DEFAULT_PORT.put("redis", 6379);
        DEFAULT_PORT.put("solr", 8983);
        DEFAULT_PORT.put("kafka", 9092);
        DEFAULT_PORT.put("elasticsearch", 9200);
        DEFAULT_PORT.put("dubbo", 20880);
        DEFAULT_PORT.put("mongodb", 27017);
        DEFAULT_PORT.put("db2", 50000);
        DEFAULT_PORT.put("active-mq", 61616);
    }

    /**
     * 原始地址
     */
    private String originalAddress;
    /**
     * 原始Host
     */
    private String originalHost;
    /**
     * 原始Host
     */
    private int originalPort;
    /**
     * hash
     */
    private String fragment;
    /**
     * 权
     */
    private String authority;
    /**
     * 地址
     */
    private String host = "127.0.0.1";
    /**
     * 端口
     */
    private Integer port = -1;
    /**
     * 协议
     */
    private String protocol;
    /**
     * 用户信息
     */
    private String userInfo;
    /**
     * 地址
     */
    private String address;
    /**
     * 用户名
     */
    @Getter
    private String username;
    /**
     * 用户名
     */
    @Getter
    private String password;
    /**
     * 路径
     */
    private String path;
    /**
     * UrlPath
     */
    private UrlPath urlPath;
    /**
     * 查询条件
     */
    private String query;
    @Getter
    private UrlQuery urlQuery = new UrlQuery();
    private String fullProtocol;
    private URI uri;

    /**
     * 初始化
     *
     * @param address 地址
     */
    private NetAddress(String address) {
        this.originalAddress = address;
        this.urlQuery = new UrlQuery();
        this.analysisAuthority();
        this.analysisSchema();
        this.analysisPath();
        this.analysisQuery();
        this.analysisFragment();
    }

    /**
     * 初始化
     */
    private NetAddress() {
    }

    /**
     * 随机端口
     */
    public static NetAddress createRandom() {
        try {
            ServerSocket s = new ServerSocket(0);
            return NetAddress.of(InetAddress.getLocalHost().getHostAddress() + ":" + s.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 创建NetAddress
     *
     * @param args 参数
     */
    public static NetAddress of(String[] args) throws UnknownHostException {
        if (args.length == 0) {
            return NetAddress.of(InetAddress.getLocalHost().getHostAddress());
        }

        if (args.length == 1) {
            return NetAddress.of("0.0.0.0:" + args[0]);
        }

        return NetAddress.of(args[0] + ":" + args[1]);

    }

    /**
     * http
     * @param url url
     * @return http
     */
    public static NetAddress ofHttp(String url) {
        final int sepIndex = url.indexOf("://");
        if (sepIndex < 0) {
            url = HTTP + "://" + url.trim();
        }
        return NetAddress.of(UrlUtils.normalize(StringUtils.trim(url), false));
    }

    /**
     * 获取地址
     *
     * @return 地址
     */
    public String getAddress() {
        if (-1 != port) {
            return host.concat(":" + port);
        }
        return host;
    }

    public static NetAddress valueOf(String address) {
        return new NetAddress(address);
    }

    /**
     * 获取地址
     *
     * @return 地址
     */
    public String getHost() {
        return getHost(null);
    }

    /**
     * 获取地址
     *
     * @return 地址
     */
    public String getHostIfExcludes(String[] excludes) {
        try {
            return getHostIfExcludes(excludes, InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            return "";
        }
    }

    /**
     * 获取地址
     *
     * @return 地址
     */
    public String getHostIfExcludes(String[] excludes, String defaultHost) {
        return StringUtils.isNullOrEmpty(host) || contains(excludes, host) ? defaultHost : host;
    }

    /**
     * 判断字符串数组是否包含指定的字符串
     *
     * @param array 字符串数组
     * @param str   指定的字符串
     * @return 包含true，否则false
     */
    private static <T> boolean contains(T[] array, T str) {
        if (array == null) {
            return false;
        }

        for (T item : array) {
            if (null == item && null == str) {
                return true;
            }

            if (null == str) {
                return false;
            }

            if (null != item && item.equals(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置地址
     *
     * @param host 地址
     * @return this
     */
    public NetAddress setHost(String host) {
        if (null != host) {
            this.host = host;
        }
        return this;
    }

    /**
     * 构建地址
     *
     * @return 地址
     */
    public InetSocketAddress getInetSocketAddress() {
        if (null != host && null != port) {
            return new InetSocketAddress(host, port);
        }
        return null;
    }

    /**
     * 获取地址
     *
     * @return 地址
     */
    public String getOriginalHost() {
        return originalHost;
    }

    /**
     * 获取端口
     *
     * @return 端口
     */
    public Integer getOriginalPort() {
        return originalPort;
    }

    /**
     * 获取端口
     *
     * @return 端口
     */
    public Integer getPort() {
        return port;
    }

    /**
     * 获取端口
     *
     * @param defaultValue 默认值
     * @return 端口
     */
    public int getPort(int defaultValue) {
        return null == port || -1 == port ? defaultValue : port;
    }

    /**
     * 设置端口
     *
     * @param port 端口
     * @return this
     */
    public NetAddress setPort(Integer port) {
        if (null != port) {
            this.port = port;
        }
        return this;
    }

    /**
     * 获取协议
     *
     * @return 协议
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * 获取协议
     *
     * @param defaultValue 默认值
     * @return 协议
     */
    public String getProtocol(String defaultValue) {
        return StringUtils.isNullOrEmpty(protocol) ? defaultValue : protocol;
    }

    /**
     * 设置协议
     *
     * @param protocol 协议
     * @return this
     */
    public NetAddress setProtocol(String protocol) {
        if (null != protocol) {
            this.protocol = protocol;
        }
        return this;
    }

    /**
     * query
     *
     * @return query
     */
    public String getQuery() {
        return this.query;
    }

    /**
     * 格式化参数
     *
     * @return 参数
     */
    public Map<CharSequence, CharSequence> parametric() {
        return Optional.ofNullable(urlQuery.getQueryMap()).orElse(Collections.emptyMap());
    }

    /**
     * 获取地址
     *
     * @return 地址
     */
    public String getHost(String defaultHost) {
        return StringUtils.isNullOrEmpty(host) || "127.0.0.1".equals(host) ? (null == defaultHost ? "127.0.0.1" : defaultHost) : host;
    }

    /**
     * 获取参数
     *
     * @param name         参数
     * @param defaultValue 默认值
     * @param <T>          类型
     * @return 结果
     */
    public <T> T getParameter(String name, T defaultValue) {
        return (T) Optional.ofNullable(urlQuery.get(name)).orElse((CharSequence) defaultValue);
    }

    /**
     * 设置地址
     *
     * @param host         地址
     * @param defaultValue 默认地址
     * @return this
     */
    public NetAddress setHost(String host, String defaultValue) {
        if (null != host) {
            setHost(host);
        } else {
            setHost(defaultValue);
        }
        return this;
    }

    /**
     * 设置端口
     *
     * @param port         端口
     * @param defaultValue 默认端口
     * @return this
     */
    public NetAddress setPort(Integer port, Integer defaultValue) {
        if (null != port) {
            setPort(port);
        } else {
            setPort(defaultValue);
        }
        return this;
    }

    /**
     * 设置协议
     *
     * @param protocol     协议
     * @param defaultValue 默认协议
     * @return this
     */
    public NetAddress setProtocol(String protocol, String defaultValue) {
        if (null != protocol) {
            setProtocol(protocol);
        } else {
            setProtocol(defaultValue);
        }
        return this;
    }

    /**
     * 概括
     *
     * @return 概括
     */
    public String summary() {
        return "NetAddress{" +
                " \n\t original     = '" + originalAddress + '\'' +
                ",\n\t fragment     = '" + fragment + '\'' +
                ",\n\t authority    = '" + authority + '\'' +
                ",\n\t host         = '" + host + '\'' +
                ",\n\t port         = " + port +
                ",\n\t originalHost = '" + originalHost + '\'' +
                ",\n\t originalPort = " + originalPort +
                ",\n\t protocol     = '" + protocol + '\'' +
                ",\n\t userInfo     = '" + userInfo + '\'' +
                ",\n\t address      = '" + address + '\'' +
                ",\n\t username     = '" + username + '\'' +
                ",\n\t password     = '" + password + '\'' +
                ",\n\t path         = '" + path + '\'' +
                ",\n\t query        = '" + query + '\'' +
                ",\n\t parametric   = " + urlQuery.toString() +
                '}';
    }

    /**
     * url
     *
     * @param scheme 协议
     * @param host   host
     * @param port   端口
     * @return url
     */
    public static URL createUrl(String scheme, String host, Integer port) {
        return createUrl(scheme, host, port, "");
    }

    public static NetAddress of(URL url) {
        return new NetAddress(url.toExternalForm());
    }

    public static NetAddress empty() {
        return new NetAddress();
    }

    public static NetAddress of(String scheme, String host, Integer port, Map<String, Object> spec) {
        return of(createUrl(scheme, host, port, spec));
    }

    public static NetAddress of(String address) {
        return new NetAddress(address);
    }

    /**
     * 格式化参数
     *
     * @param query 请求查询参数
     * @return 参数
     */
    public Map<String, Object> parametric(String query) {
        if (null == query) {
            return Collections.emptyMap();
        }
        Map<String, String> split = new HashMap<>(16);
        if (query.contains(SYMBOL_EQUALS)) {
            try {
                split = Splitter.on(SYMBOL_AND).omitEmptyStrings().trimResults().withKeyValueSeparator(SYMBOL_EQUALS).split(query);
            } catch (Exception ignored) {
                split.putAll(MapUtils.asMap(query, SYMBOL_AND, SYMBOL_EQUALS));
            }
        }
        Map<String, Object> result = new HashMap<>(split.size());
        split.forEach((k, v) -> {
            if (v != null) {
                try {
                    result.put(k, URLDecoder.decode(v, "UTF-8"));
                } catch (UnsupportedEncodingException ignored) {
                }
                return;
            }
            result.put(k, null);
        });
        return result;
    }

    /**
     * 初始化
     *
     * @param socketAddress 地址
     * @return this
     */
    public static NetAddress of(SocketAddress socketAddress) {
        if (null != socketAddress) {
            if (socketAddress instanceof InetSocketAddress) {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
                return of(inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort());
            }
        }
        return of("");
    }

    /**
     * url
     *
     * @param scheme 协议
     * @param host   host
     * @param port   端口
     * @param file   文件
     * @return url
     */
    private static URL createUrl(String scheme, String host, Integer port, String file) {
        try {
            return new URL(scheme, host, port, null == file ? "" : file);
        } catch (MalformedURLException e) {
            try {
                return new URL(scheme + PROTOCOL_SYMBOL + host + HOST_SYMBOL + port + CommonConstant.SYMBOL_QUESTION + file);
            } catch (MalformedURLException e1) {
                return null;
            }
        }
    }

    /**
     * url
     *
     * @param scheme 协议
     * @param host   host
     * @param port   端口
     * @param spec   参数
     * @return url
     */
    private static URL createUrl(String scheme, String host, Integer port, Map<String, Object> spec) {
        String join = Joiner.on(SYMBOL_AND).withKeyValueSeparator(SYMBOL_EQUALS).join(spec);
        try {
            return new URL(scheme, host, port, join);
        } catch (MalformedURLException e) {
            try {
                return new URL(scheme + PROTOCOL_SYMBOL + host + HOST_SYMBOL + port + CommonConstant.SYMBOL_QUESTION + join);
            } catch (MalformedURLException e1) {
                return null;
            }
        }
    }

    /**
     * 解析 hash
     */
    private void analysisFragment() {
        if (StringUtils.isNullOrEmpty(address)) {
            return;
        }

        int index = originalAddress.indexOf("#");
        if (index != -1) {
            this.fragment = originalAddress.substring(index + 1);
        }

        if (null == authority) {
            return;
        }

        if (authority.contains(SYMBOL_HASH)) {
            this.authority = authority.substring(0, authority.indexOf(SYMBOL_HASH));
        }

        if (null != host && host.contains(SYMBOL_HASH)) {
            this.host = host.substring(0, host.indexOf(SYMBOL_HASH));
        }

        if (null != address && address.contains(SYMBOL_HASH)) {
            this.address = address.substring(0, address.indexOf(SYMBOL_HASH));
        }
    }

    /**
     * 解析查询条件
     */
    private void analysisQuery() {
        if (StringUtils.isNullOrEmpty(address)) {
            return;
        }

        int index = originalAddress.indexOf(CommonConstant.SYMBOL_QUESTION);
        if (index != -1) {
            String queryAndFragment = originalAddress.substring(index + 1);
            index = queryAndFragment.indexOf("#");
            if (index == -1) {
                this.query = queryAndFragment;
            } else {
                this.query = queryAndFragment.substring(0, index);
            }
        }
        analysisQueryParams();
    }

    /**
     * 解析参数
     */
    private void analysisQueryParams() {
        if (null == query) {
            return;
        }
        Map<String, Object> parametric = this.parametric(query);
        parametric.forEach(urlQuery::add);
    }

    /**
     * 解析路径
     */
    private void analysisPath() {
        if (null == authority) {
            return;
        }
        String path = uri.getPath();
        urlPath = UrlPath.of(path, UTF_8);
        int index = path.indexOf(CommonConstant.SYMBOL_QUESTION);
        if (index != -1) {
            this.path = path.substring(0, index);
            return;
        }
        this.path = path.startsWith("/") ? path.substring(1) : path;
    }

    /**
     * 用户信息
     */
    private void analysisUserInfo() {
        if (null == authority) {
            return;
        }
        int index = authority.indexOf("@");
        if (index != -1) {
            this.userInfo = authority.substring(0, index);
        }
        analysisUsername();
        analysisPassword();
    }

    /**
     * 解析密码
     */
    private void analysisPassword() {
        if (null == userInfo) {
            return;
        }
        int index = userInfo.indexOf(":");
        if (-1 != index) {
            this.password = userInfo.substring(index + 1);
        }
    }

    /**
     * 解析用户名
     */
    private void analysisUsername() {
        if (null == userInfo) {
            return;
        }
        int index = userInfo.indexOf(":");
        if (-1 != index) {
            this.username = userInfo.substring(0, index);
        }
    }

    /**
     * 获取地址
     */
    private void analysisAddress() {
        if (null == userInfo) {
            if (null != authority) {
                this.address = authority;
            } else {
                this.address = originalAddress;
            }
        } else {
            this.address = authority.substring(userInfo.length() + 1);
        }

        if (null != address) {
            int index = address.indexOf(CommonConstant.SYMBOL_QUESTION);
            if (index != -1) {
                this.address = address.substring(0, index);
            }

            index = address.indexOf("#");
            if (index != -1) {
                this.address = address.substring(0, index);
            }
        }


        this.analysisHost();
        this.analysisPort();
    }

    /**
     * 分析 authority
     */
    @SneakyThrows
    private void analysisAuthority() {
        try {
            this.authority = (uri == null ? (uri = new URI(originalAddress)) : uri).getAuthority();
        } catch (URISyntaxException ignored) {
        }
        this.analysisUserInfo();
        this.analysisAddress();
        this.analysisOrigin();

    }

    /**
     * 分析地址
     */
    private void analysisHost() {
        if (null == address) {
            return;
        }
        int index = address.indexOf(":");
        if (index != -1) {
            this.host = address.substring(0, index);
            return;
        }
        this.host = address;
    }

    /**
     * 分析端口
     */
    private void analysisPort() {
        if (null == host || null == address) {
            return;
        }

        if (host.length() < address.length()) {
            String substring = address.substring(host.length() + 1);
            try {
                this.port = Integer.parseInt(substring);
            } catch (NumberFormatException e) {
                this.port = -1;
            }
        }

    }

    /**
     * 解析原始信息
     */
    private void analysisOrigin() {
        if (StringUtils.isNullOrEmpty(address)) {
            return;
        }

        if (-1 == port && !Pattern.matches(ADDRESS.pattern(), address)) {
            try {
                InetAddress inetAddress = InetAddress.getByName(address);
                this.originalHost = inetAddress.getHostAddress();
                this.originalPort = createDefaultPort();
            } catch (UnknownHostException ignored) {
            }
        } else {
            originalHost = host;
            originalPort = port;
        }
    }

    /**
     * 默认端口
     *
     * @return 默认端口
     */
    private int createDefaultPort() {
        return DEFAULT_PORT.getOrDefault(protocol, -1);
    }

    /**
     * 分析协议
     */
    private void analysisSchema() {
        if (null == originalAddress) {
            return;
        }

        int index = originalAddress.indexOf("://");
        if (index != -1) {
            String proto = null;
            try {
                this.uri = new URI(originalAddress);
                proto = uri.getScheme();
                String tpl = proto;
                while (!StringUtils.isEmpty(tpl)) {
                    uri = new URI(uri.getSchemeSpecificPart());
                    tpl = uri.getScheme();
                    if(null != tpl) {
                        proto = tpl;
                    }
                }
            } catch (URISyntaxException ignored) {
            }

            this.protocol = proto;
            this.fullProtocol = originalAddress.substring(0, index);
        }
    }


    private String print(String source, int length) {
        if (StringUtils.isNullOrEmpty(source)) {
            return source;
        }

        if (source.length() > length) {
            return source.substring(0, length) + "...";
        }

        return source;
    }

    @Override
    public String toString() {
        return
                " originalAddress: '" + print(originalAddress, 15) + '\'' +
                        "\r\n originalHost: '" + originalHost + '\'' +
                        "\r\n originalPort: " + originalPort +
                        "\r\n fragment: '" + fragment + '\'' +
                        "\r\n authority: '" + authority + '\'' +
                        "\r\n host: '" + host + '\'' +
                        "\r\n port: " + port +
                        "\r\n protocol: '" + protocol + '\'' +
                        "\r\n userInfo: '" + userInfo + '\'' +
                        "\r\n address: '" + address + '\'' +
                        "\r\n username: '" + username + '\'' +
                        "\r\n password: '******' " +
                        "\r\n path: '" + path + '\'' +
                        "\r\n query: '" + print(query, 15) + '\'' +
                        "\r\n parametric: " + urlQuery.toString();
    }

    private String analysisMap(Map<String, Object> parametric) {
        if (null == parametric) {
            return "";
        }

        StringBuilder sb = new StringBuilder(CommonConstant.SYMBOL_LEFT_BIG_PARENTHESES);
        for (Map.Entry<String, Object> entry : parametric.entrySet()) {
            sb.append("\r\n\t\t").append(entry.getKey()).append(": ").append(entry.getValue());
        }

        sb.append("\r\n}");

        return sb.toString();
    }

    /**
     * 获取 InetSocketAddress
     *
     * @return InetSocketAddress
     */
    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(getHost(), getPort());
    }

    public String toFullString() {
        return buildString(true, true);
    }

    public String toFullString(String... parameters) {
        return buildString(true, true, parameters);
    }

    private String buildString(boolean appendUser, boolean appendParameter, String... parameters) {
        return buildString(appendUser, appendParameter, false, false, parameters);
    }

    private String buildString(boolean appendUser, boolean appendParameter, boolean useIP, boolean useService, String... parameters) {
        StringBuilder buf = new StringBuilder();
        if (!StringUtils.isEmpty(getProtocol())) {
            buf.append(getProtocol());
            buf.append("://");
        }
        if (appendUser && !StringUtils.isEmpty(getUsername())) {
            buf.append(getUsername());
            if (!StringUtils.isEmpty(getPassword())) {
                buf.append(':');
                buf.append(getPassword());
            }
            buf.append('@');
        }
        String host;
        if (useIP) {
            host = getIp();
        } else {
            host = getHost();
        }
        if (!StringUtils.isEmpty(host)) {
            buf.append(host);
            if (getPort() > 0) {
                buf.append(':');
                buf.append(getPort());
            }
        }
        String path = getPath();
        if (!StringUtils.isEmpty(path)) {
            buf.append('/');
            buf.append(path);
        }

        if (appendParameter) {
            buildParameters(buf, true, parameters);
        }
        return buf.toString();
    }

    protected void buildParameters(StringBuilder buf, boolean concat, String[] parameters) {
        if (!MapUtils.isEmpty(urlQuery.getQueryMap())) {
            List<String> includes = (ArrayUtils.isEmpty(parameters) ? null : Arrays.asList(parameters));
            boolean first = true;
            for (Map.Entry<CharSequence, CharSequence> entry : new TreeMap<>(urlQuery.getQueryMap()).entrySet()) {
                boolean b = !StringUtils.isEmpty(entry.getKey())
                        && (includes == null || includes.contains(entry.getKey()));
                if (b) {
                    if (first) {
                        if (concat) {
                            buf.append('?');
                        }
                        first = false;
                    } else {
                        buf.append('&');
                    }
                    buf.append(entry.getKey());
                    buf.append('=');
                    buf.append(entry.getValue() == null ? "" : entry.getValue());
                }
            }
        }
    }

    public String getIp() {
        return NetUtils.getIpByHost(getHost());
    }

    /**
     * 地址
     *
     * @return 地址
     */
    public java.net.URL toURL() {
        try {
            return new URL(toFullString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加参数
     *
     * @param map 参数
     */
    public void addParameter(Map<String, Object> map) {
        map.forEach(urlQuery::add);
    }

    /**
     * 添加参数
     *
     * @param key   key
     * @param value 值
     */
    public void addParameter(String key, Object value) {
        urlQuery.add(key, value);
    }

    public String getPath() {
        return path;
    }

}
