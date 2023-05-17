package com.chua.common.support.http.invoke;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.http.*;
import com.chua.common.support.json.Json;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.chua.common.support.constant.NumberConstant.DEFAULT_INITIAL_CAPACITY;
import static com.chua.common.support.http.HttpConstant.*;
import static com.chua.common.support.http.HttpMethod.*;

/**
 * 执行器
 *
 * @author CH
 */
@Slf4j
@Spi("httpclient")
public class UrlHttpClientInvoker extends AbstractHttpClientInvoker {


    public UrlHttpClientInvoker(HttpRequest request, HttpMethod httpMethod) {
        super(request, httpMethod);
    }

    @Override
    protected HttpResponse executeDelete() {
        return executeMethod(DELETE, 1);
    }

    @Override
    protected HttpResponse executePut() {
        return executeMethod(PUT, 1);
    }

    @Override
    protected HttpResponse executePost() {
        return executeMethod(POST, 1);
    }

    @Override
    protected HttpResponse executeGet() {
        return executeMethod(GET, 1);
    }

    @Override
    protected HttpResponse executePatch() {
        return executeMethod(PATCH, 1);
    }

    @Override
    protected HttpResponse executeOption() {
        return executeMethod(OPTION, 1);
    }

    @Override
    protected HttpResponse executeHead() {
        return executeMethod(HEAD, 1);
    }

    /**
     * method请求
     *
     * @param retry 重试次数
     * @return ResponseEntity
     * @see HttpResponse
     */
    public HttpResponse executeMethod(final HttpMethod method, int retry) {
        HttpURLConnection connection = null;
        try {
            doAnalysisUrl(method);
            connection = urlConnection();
            // 设定请求的方法，默认是GET
            doAnalysisRequestMethod(connection, method);
            //设置消息头
            doAnalysisHeader(connection);
            //设置认证
            doAnalysisBasicAuth(connection);
            //设置缓存
            doAnalysisCache(connection, method);
            //设置消息体
            doAlisander(connection);
            //设置配置
            doAnalysisRequestConfig(connection);
            // 建立实际的连接
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）
            // 如果在已打开连接（此时 connected 字段的值为 true）的情况下调用 connect 方法，则忽略该调用
            if (log.isDebugEnabled()) {
                log.debug("==================================================");
            }
            log.info("发送的URL: {}", url);
            if (log.isDebugEnabled()) {
                log.debug("消息体: {}", Json.prettyFormat(request.getBody()));
                log.debug("==================================================");
            }
            connection.connect();
            //获取结果
            int code = connection.getResponseCode();
            Map<String, List<String>> fields = connection.getHeaderFields();
            HttpHeader header = new HttpHeader();
            for (Map.Entry<String, List<String>> entry : fields.entrySet()) {
                header.put(entry.getKey(), CollectionUtils.findFirst(entry.getValue()));
            }

            Object content;
            byte[] bytes;
            try (InputStream stream = connection.getInputStream()) {
                bytes = IoUtils.toByteArray(stream);
                content = bytes;
            }
            log.info("接收到URL: {}, 响应码: {}", url, code);
            return HttpResponse.builder().code(code).content(content).httpHeader(header).build();
        } catch (Throwable e) {
            e.printStackTrace();
            if (retry < request.getRetry()) {
                return executeMethod(method, retry + 1);
            }
            return HttpResponse.builder().code(500).message(e.getMessage()).build();
        } finally {
            IoUtils.closeQuietly(connection);
        }
    }

    /**
     * 处理地址
     */
    private void doAnalysisUrl(HttpMethod method) {
        if (Objects.equals(GET, method) && !url.contains("?")) {
            this.url = HttpClientUtils.createUrlWithParameters(url, request.getBody());
        }
    }

    /**
     * 获取链接
     *
     * @return HttpURLConnection
     * @throws IOException IOException
     */
    private HttpURLConnection urlConnection() throws IOException {
        HttpURLConnection connection;
        URL realUrl = new URL(url);
        if (!isHttps) {
            // 打开和URL之间的连接
            connection = (HttpURLConnection) realUrl.openConnection();
        } else {
            HttpsURLConnection urlConnection = (HttpsURLConnection) realUrl.openConnection();
            Object sslSocketFactory = request.getSslSocketFactory();
            if (sslSocketFactory instanceof SSLSocketFactory) {
                urlConnection.setSSLSocketFactory((SSLSocketFactory) sslSocketFactory);
            } else {
                HttpsURLConnection.setDefaultSSLSocketFactory(HttpClientUtils.createSslSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(HttpClientUtils.createDefaultHostnameVerifier());
            }
            connection = urlConnection;
        }
        return connection;
    }

    /**
     * 设置方法类型
     *
     * @param connection 链接
     * @param method     方法
     */
    private void doAnalysisRequestMethod(HttpURLConnection connection, HttpMethod method) throws ProtocolException {
        // 设置是否从httpUrlConnection读入，默认情况下是true;
        connection.setDoInput(true);
        connection.setRequestMethod(method.name());
    }

    /**
     * 设置消息头
     *
     * @param connection connection
     */
    private void doAnalysisHeader(URLConnection connection) {
        if(isHttps && connection instanceof HttpURLConnection) {
            ((HttpURLConnection) connection).setInstanceFollowRedirects(false);
        }

        HttpHeader header = request.getHeader();
        if (null == header) {
            header = new HttpHeader();
        }

        if(!header.isEmpty()) {
            header.forEach(connection::setRequestProperty);
        }

        // 设置通用的请求属性
        if (StringUtils.isNullOrEmpty(connection.getRequestProperty(ACCEPT))) {
            connection.setRequestProperty(ACCEPT, ANY);
        }

        // 设置通用的请求属性
        if (StringUtils.isNullOrEmpty(connection.getRequestProperty(CONNECTION))) {
            connection.setRequestProperty(CONNECTION, KEEP_ALIVE);
        }

        // 设置通用的请求属性
        if (StringUtils.isNullOrEmpty(connection.getRequestProperty(USER_AGENT))) {
            connection.setRequestProperty(USER_AGENT, USER_AGENT_VALUE);
        }
        if (log.isDebugEnabled()) {
            if (log.isDebugEnabled()) {
                log.debug("消息头设置完成, header!!");
                log.debug("=======================================================");
                for (String key : header.keySet()) {
                    log.debug("{}: {}", key, header.get(key));
                }
                log.debug("=======================================================");
            }
        }
    }


    /**
     * 设置认证
     *
     * @param connection 连接
     */
    private void doAnalysisBasicAuth(HttpURLConnection connection) {
        if (request.getBasicAuth().isEmpty()) {
            return;
        }
        String encoding = "";
        for (Map.Entry<String, String> entry : request.getBasicAuth().entrySet()) {
            encoding = Base64.getEncoder().encodeToString((entry.getKey() + ":" + entry.getValue()).getBytes(StandardCharsets.UTF_8));
        }
        connection.setRequestProperty("Authorization", String.format("Basic %s", encoding));
    }


    /**
     * Post 请求不能使用缓存
     *
     * @param connection 链接
     * @param method     方法
     */
    private void doAnalysisCache(HttpURLConnection connection, HttpMethod method) {
        // Post 请求不能使用缓存
        if (Objects.equals(GET, method)) {
            connection.setUseCaches(true);
            return;
        }
        connection.setUseCaches(false);
    }

    /**
     * 设置消息体
     *
     * @param connection 链接
     */
    private void doAlisander(HttpURLConnection connection) {

        try {
            connection.setDoOutput(true);
        } catch (Exception ignored) {
        }

        Map<String, Object> body = new LinkedHashMap<>(request.getBody());
        //上传文件
        if (request.isFormData() && request.hasBin()) {
            try {
                formData(connection, body);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        //非上传文件
        String parameters = HttpClientUtils.createWithParameters(request.getHeader().get(HTTP_HEADER_CONTENT_TYPE), body);
        try (OutputStream outputStream = connection.getOutputStream();
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
            outputStreamWriter.write(parameters);
            outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 文件 & 表单
     *
     * @param connection 链接
     * @param body       请求条件
     */
    private void formData(HttpURLConnection connection, Map<String, Object> body) throws IOException {
        String boundary = "---------------------------";
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        OutputStream out = new DataOutputStream(connection.getOutputStream());
        Map<String, Object> textMap = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
        Map<String, File> fileNames = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
        Map<String, byte[]> fileMap = new HashMap<>(DEFAULT_INITIAL_CAPACITY);

        analysisParams(body, textMap, fileNames, fileMap);

        intoOutStream(boundary, out, textMap, fileNames, fileMap);

        byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
        out.write(endData);
        out.flush();
        out.close();
    }

    /**
     * 分析参数
     *
     * @param body      请求体
     * @param textMap   文本参数
     * @param fileNames 文件名
     * @param fileMap   文件参数
     */
    private void analysisParams(Map<String, Object> body, Map<String, Object> textMap, Map<String, File> fileNames, Map<String, byte[]> fileMap) {
        for (Map.Entry<String, Object> entry : body.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (null == value) {
                textMap.put(key, null);
                continue;
            }
            if (value instanceof byte[]) {
                fileMap.put(key, (byte[]) value);
                continue;
            }
            if (value instanceof File) {
                try {
                    fileMap.put(key, Files.readAllBytes(Paths.get(((File) value).getAbsolutePath())));
                    fileNames.put(key, ((File) value));
                } catch (IOException ignored) {
                }
                continue;
            }
            if (value instanceof InputStream) {
                try {
                    fileMap.put(key, IoUtils.toByteArray((InputStream) value));
                } catch (IOException ignored) {
                }
                continue;
            }

            if (value instanceof Path) {
                try {
                    fileMap.put(key, Files.readAllBytes((Path) value));
                    fileNames.put(key, ((Path) value).toFile());
                } catch (IOException ignored) {
                }
                continue;
            }
            textMap.put(key, value);
        }
    }

    /**
     * 赋值
     *
     * @param boundary  boundary
     * @param out       输出流
     * @param textMap   文本参数
     * @param fileNames 文件名
     * @param fileMap   文件
     */
    @SneakyThrows
    private void intoOutStream(String boundary, OutputStream out, Map<String, Object> textMap, Map<String, File> fileNames, Map<String, byte[]> fileMap) {
        StringBuilder strBuf = new StringBuilder();
        for (Map.Entry<String, Object> entry : textMap.entrySet()) {
            strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
            strBuf.append(entry.getValue());
        }

        out.write(strBuf.toString().getBytes());
        for (Map.Entry<String, byte[]> entry : fileMap.entrySet()) {
            String key = entry.getKey();
            File file = fileNames.get(key);

            String contentType = null;
            if (contentType == null || "".equals(contentType)) {
                contentType = "application/octet-stream";
            }

            String fileBuf = "\r\n" + "--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + file.getName() + "\"\r\n" +
                    "Content-Type:" + contentType + "\r\n\r\n";
            out.write(fileBuf.getBytes());

            try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
                int bytes;
                byte[] bufferOut = new byte[1024];
                while ((bytes = in.read(bufferOut)) != -1) {
                    out.write(bufferOut, 0, bytes);
                }
            }

        }
    }

    /**
     * 设置配置
     *
     * @param connection connection
     */
    private void doAnalysisRequestConfig(URLConnection connection) {
        long timeout = request.getConnectTimeout();
        // 设置一个指定的超时值（以毫秒为单位）
        if (timeout > 0L) {
            connection.setConnectTimeout(((Long) timeout).intValue());
        }

        long readTimeout = request.getReadTimeout();
        // 将读超时设置为指定的超时，以毫秒为单位。
        if (readTimeout > 0L) {
            connection.setReadTimeout(((Long) readTimeout).intValue());
        }

        if (log.isDebugEnabled()) {
            log.debug("链接配置设置完成!!");
            log.debug("=======================================================");
            log.debug("connectTimeout: {}", timeout);
            log.debug("readTimeout: {}", readTimeout);
            log.debug("=======================================================");
        }
    }

}
