package com.chua.common.support.mapping.invoke.hik.util;

import com.chua.common.support.http.*;
import com.chua.common.support.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_AND;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_EQUALS;
import static com.chua.common.support.constant.NameConstant.*;
import static com.chua.common.support.http.HttpConstant.*;
import static com.chua.common.support.mapping.invoke.hik.constant.Constants.*;


/**
 * Http工具类
 *
 * @author HIK, CH
 * @since 2023/09/06
 */
public class HikHttpUtil {
    /**
     * http获取
     * HTTP GET
     *
     * @param host                 主办
     * @param path                 路径
     * @param connectTimeout       连接超时
     * @param headers              消息头
     * @param querys               查询
     * @param signHeaderPrefixList 签名消息头前缀列表
     * @param appKey               应用程序密钥
     * @param appSecret            应用程序机密
     * @return {@link HikResponse}
     * @throws Exception 例外
     */
    public static HikResponse httpGet(String host,
                                      String path,
                                      int connectTimeout,
                                      Map<String, String> headers,
                                      Map<String, String> querys,
                                      List<String> signHeaderPrefixList,
                                      String appKey,
                                      String appSecret) throws Exception {
        headers = initialBasicHeader(GET, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);

        HttpClientBuilder httpClient = HttpClient.get();
        HikResponse r = null;

        try {
            HttpClientBuilder builder = httpClient.connectTimout(getTimeout(connectTimeout))
                    .url(initUrl(host, path, querys));

            for (Map.Entry<String, String> e : headers.entrySet()) {
                builder.header(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
            }
            HttpClientInvoker invoker = builder.newInvoker();
            HttpResponse rp = invoker.execute();
            r = convert(rp);

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return r;
    }


    /**
     * httpimg获取
     *
     * @param host                 主机
     * @param path                 路径
     * @param connectTimeout       连接超时
     * @param headers              消息头
     * @param querys               查询
     * @param signHeaderPrefixList 签名消息头前缀列表
     * @param appKey               应用程序密钥
     * @param appSecret            应用程序机密
     * @return {@link HikResponse}
     * @throws Exception 例外
     */
    public static HikResponse httpImgGet(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        headers = initialBasicHeader(GET, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);

        HttpClientBuilder httpClient = HttpClient.get();
        HikResponse r = null;

        try {
            HttpClientBuilder builder = httpClient.connectTimout(getTimeout(connectTimeout))
                    .url(initUrl(host, path, querys));

            for (Map.Entry<String, String> e : headers.entrySet()) {
                builder.header(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
            }
            HttpClientInvoker invoker = builder.newInvoker();
            HttpResponse rp = invoker.execute();
            r = convertImg(rp);

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return r;
    }

    /**
     * HTTP POST表单
     *
     * @param host                 主机
     * @param path                 路径
     * @param connectTimeout       连接超时
     * @param headers              消息头
     * @param querys               查询
     * @param bodys                消息体
     * @param signHeaderPrefixList 签名消息头前缀列表
     * @param appKey               应用程序密钥
     * @param appSecret            应用程序机密
     * @return {@link HikResponse}
     * @throws Exception 例外
     */
    public static HikResponse httpPost(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, Map<String, String> bodys, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        if (headers == null) {
            headers = new HashMap<>();
        }

        headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_FORM);

        headers = initialBasicHeader(POST, path, headers, querys, bodys, signHeaderPrefixList, appKey, appSecret);

        HttpClientBuilder builder = HttpClient.post()
                .connectTimout(getTimeout(connectTimeout))
                .url(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            builder.header(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        builder.isForm().body(bodys);
        return convert(builder.newInvoker().execute());
    }

    /**
     * img文章
     * HTTP POST表单
     *
     * @param host                 主机
     * @param path                 路径
     * @param connectTimeout       连接超时
     * @param headers              消息头
     * @param querys               查询
     * @param bodys                消息体
     * @param signHeaderPrefixList 签名消息头前缀列表
     * @param appKey               应用程序密钥
     * @param appSecret            应用程序机密
     * @return {@link HikResponse}
     * @throws Exception 例外
     */
    public static HikResponse httpImgPost(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, Map<String, String> bodys, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        if (headers == null) {
            headers = new HashMap<>();
        }

        headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_FORM);

        headers = initialBasicHeader(POST, path, headers, querys, bodys, signHeaderPrefixList, appKey, appSecret);

        HttpClientBuilder builder = HttpClient.post()
                .connectTimout(getTimeout(connectTimeout))
                .url(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            builder.header(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        builder.isForm().body(bodys);
        return convertImg(builder.newInvoker().execute());
    }


    /**
     * http帖子
     * Http POST 字符
     *
     * @param host                 主机
     * @param path                 路径
     * @param connectTimeout       连接超时
     * @param headers              消息头
     * @param querys               查询
     * @param body                 消息体
     * @param signHeaderPrefixList 签名消息头前缀列表
     * @param appKey               应用程序密钥
     * @param appSecret            应用程序机密
     * @return {@link HikResponse}
     * @throws Exception 例外
     */
    public static HikResponse httpPost(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, String body, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {

        String contentType = headers.get(HTTP_HEADER_CONTENT_TYPE);
        //postString发鿁content-type为表单的请求，请求的body字符串必须为key-value组成的串，类似a=1&b=2这种形式
        if (CONTENT_TYPE_FORM.equals(contentType)) {
            Map<String, String> paramMap = strToMap(body);

            //这个base64的字符串经过url编码，签名时先解砿(这个是针对大数据某个请求包含url编码的参敿)，对某个请求包含的参数的特殊处理
            String modelDatas = paramMap.get("modelDatas");
            if (StringUtils.isNotBlank(modelDatas)) {
                paramMap.put("modelDatas", URLDecoder.decode(modelDatas));
            }

            headers = initialBasicHeader(POST, path, headers, querys, paramMap, signHeaderPrefixList, appKey, appSecret);
        } else {
            headers = initialBasicHeader(POST, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);
        }

        HttpClientBuilder builder = HttpClient.post()
                .connectTimout(getTimeout(connectTimeout))
                .url(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            builder.header(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        builder.body(body);
        return convert(builder.newInvoker().execute());
    }


    /**
     * img文章
     * Http POST 字符丿
     *
     * @param host                 主机
     * @param path                 路径
     * @param connectTimeout       连接超时
     * @param headers              消息头
     * @param querys               查询
     * @param body                 消息体
     * @param signHeaderPrefixList 签名消息头前缀列表
     * @param appKey               应用程序密钥
     * @param appSecret            应用程序机密
     * @return {@link HikResponse}
     * @throws Exception 例外
     */
    public static HikResponse httpImgPost(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, String body, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {

        String contentType = headers.get(HTTP_HEADER_CONTENT_TYPE);
        if (CONTENT_TYPE_FORM.equals(contentType)) {
            //postString发鿁content-type为表单的请求，请求的body字符串必须为key-value组成的串，类似a=1&b=2这种形式
            Map<String, String> paramMap = strToMap(body);

            String modelDatas = paramMap.get("modelDatas");
            //这个base64的字符串经过url编码，签名时先解砿(这个是针对大数据某个请求包含url编码的参敿)，对某个请求包含的参数的特殊处理
            if (StringUtils.isNotBlank(modelDatas)) {
                paramMap.put("modelDatas", URLDecoder.decode(modelDatas));
            }

            headers = initialBasicHeader(POST, path, headers, querys, paramMap, signHeaderPrefixList, appKey, appSecret);
        } else {
            headers = initialBasicHeader(POST, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);
        }

        HttpClientBuilder builder = HttpClient.post()
                .connectTimout(getTimeout(connectTimeout))
                .url(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            builder.header(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        builder.body(body);

        return convertImg(builder.newInvoker().execute());
    }

    /**
     * http帖子
     * HTTP POST 字节数组
     *
     * @param host                 主机
     * @param path                 路径
     * @param connectTimeout       连接超时
     * @param headers              消息头
     * @param querys               查询
     * @param bodys                消息体
     * @param signHeaderPrefixList 签名消息头前缀列表
     * @param appKey               应用程序密钥
     * @param appSecret            应用程序机密
     * @return {@link HikResponse}
     * @throws Exception 例外
     */
    public static HikResponse httpPost(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, byte[] bodys, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        headers = initialBasicHeader(POST, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);


        HttpClientBuilder builder = HttpClient.post()
                .connectTimout(getTimeout(connectTimeout))
                .url(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            builder.header(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        builder.body(bodys);

        return convert(builder.newInvoker().execute());
    }

    /**
     * http put
     * HTTP PUT 字符丿
     *
     * @param host                 主机
     * @param path                 路径
     * @param connectTimeout       连接超时
     * @param headers              消息头
     * @param querys               查询
     * @param body                 消息体
     * @param signHeaderPrefixList 签名消息头前缀列表
     * @param appKey               应用程序密钥
     * @param appSecret            应用程序机密
     * @return {@link HikResponse}
     * @throws Exception 例外
     */
    public static HikResponse httpPut(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, String body, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        headers = initialBasicHeader(PUT, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);

        HttpClientBuilder builder = HttpClient.put()
                .connectTimout(getTimeout(connectTimeout))
                .url(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            builder.header(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        builder.body(body);

        return convert(builder.newInvoker().execute());
    }

    /**
     * http put
     * http put
     * HTTP PUT字节数组
     *
     * @param host                 主机
     * @param path                 路径
     * @param connectTimeout       连接超时
     * @param headers              消息头
     * @param querys               查询
     * @param bodys                消息体
     * @param signHeaderPrefixList 签名消息头前缀列表
     * @param appKey               应用程序密钥
     * @param appSecret            应用程序机密
     * @return {@link HikResponse}
     * @throws Exception 例外
     */
    public static HikResponse httpPut(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, byte[] bodys, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        headers = initialBasicHeader(PUT, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);

        HttpClientBuilder builder = HttpClient.put()
                .connectTimout(getTimeout(connectTimeout))
                .url(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            builder.header(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        builder.body(bodys);

        return convert(builder.newInvoker().execute());
    }

    /**
     * http删除
     * HTTP DELETE
     *
     * @param host                 主机
     * @param path                 路径
     * @param connectTimeout       连接超时
     * @param headers              消息头
     * @param querys               查询
     * @param signHeaderPrefixList 签名消息头前缀列表
     * @param appKey               应用程序密钥
     * @param appSecret            应用程序机密
     * @return {@link HikResponse}
     * @throws Exception 例外
     */
    public static HikResponse httpDelete(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, List<String> signHeaderPrefixList, String appKey, String appSecret)
            throws Exception {
        headers = initialBasicHeader(DELETE, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);


        HttpClientBuilder builder = HttpClient.delete()
                .connectTimout(getTimeout(connectTimeout))
                .url(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            builder.header(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        return convert(builder.newInvoker().execute());
    }


    /**
     * 初始化url
     *
     * @param path   路径
     * @param querys 查询
     * @param host   主机
     * @return {@link String}
     * @throws UnsupportedEncodingException 不支持编码异常
     */
    public static String initUrl(String host, String path, Map<String, String> querys) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(host);
        if (!StringUtils.isBlank(path)) {
            sbUrl.append(path);
        }
        if (null != querys) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append(SPE3);
                }
                if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!StringUtils.isBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!StringUtils.isBlank(query.getValue())) {
                        sbQuery.append(SPE4);
                        sbQuery.append(URLEncoder.encode(query.getValue(), ENCODING));
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append(SPE5).append(sbQuery);
            }
        }

        return sbUrl.toString();
    }


    /**
     * 最初基本消息头
     * 初始化基硿Header
     *
     * @param method               方法
     * @param path                 路径
     * @param headers              消息头
     * @param querys               查询
     * @param bodys                消息体
     * @param signHeaderPrefixList 签名消息头前缀列表
     * @param appKey               应用程序密钥
     * @param appSecret            应用程序机密
     * @return {@link Map}<{@link String}, {@link String}>
     */
    private static Map<String, String> initialBasicHeader(String method, String path,
                                                          Map<String, String> headers,
                                                          Map<String, String> querys,
                                                          Map<String, String> bodys,
                                                          List<String> signHeaderPrefixList,
                                                          String appKey, String appSecret) {
        if (headers == null) {
            headers = new HashMap<>();
        }

        headers.put(X_CA_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        headers.put(X_CA_NONCE, UUID.randomUUID().toString());
        headers.put(X_CA_KEY, appKey);
        headers.put(X_CA_SIGNATURE,
                HikSignUtil.sign(appSecret, method, path, headers, querys, bodys, signHeaderPrefixList));

        return headers;
    }

    /**
     * 获取超时
     * 读取超时时间
     *
     * @param timeout 超时
     * @return int
     */
    private static int getTimeout(int timeout) {
        if (timeout == 0) {
            return DEFAULT_TIMEOUT;
        }

        return timeout;
    }

    /**
     * 转换
     *
     * @param response 回答
     * @return {@link HikResponse}
     * @throws IOException IOException
     */
    private static HikResponse convert(HttpResponse response) throws IOException {
        HikResponse res = new HikResponse();

        if (null != response) {
            res.setStatusCode(response.code());
            response.httpHeader().forEach((k, v) -> {
                res.setHeader(k, MessageDigestUtil.iso88591ToUtf8(v));
            });

            res.setContentType(res.getHeader("Content-Type"));
            res.setRequestId(res.getHeader("X-Ca-Request-Id"));
            res.setErrorMessage(res.getHeader("X-Ca-Error-Message"));
            res.setBody(response.content(String.class));
        } else {
            //服务器无回应
            res.setStatusCode(500);
            res.setErrorMessage("No Response");
        }

        return res;
    }


    private static HikResponse convertImg(HttpResponse response) throws IOException {
        HikResponse res = new HikResponse();
        String newUrl;

        if (null != response) {
            if (302 == response.code()) {
                HttpHeader httpHeader = response.httpHeader();
                newUrl = httpHeader.getFirst("location");
                HttpClientInvoker clientInvoker = HttpClient.get().url(newUrl).newInvoker();
                HttpResponse response1 = clientInvoker.execute();
                response = response1;
            }

            response.httpHeader().forEach((k, v) -> {
                res.setHeader(k, MessageDigestUtil.iso88591ToUtf8(v));
            });

            res.setContentType(res.getHeader("Content-Type"));
            res.setRequestId(res.getHeader("X-Ca-Request-Id"));
            res.setErrorMessage(res.getHeader("X-Ca-Error-Message"));
            res.setResponse(response);
        } else {
            res.setStatusCode(500);
            res.setErrorMessage("No Response");
        }

        return res;
    }


    /**
     * 以str读取流
     * 将流转换为字符串
     *
     * @param is 是
     * @return {@link String}
     * @throws IOException IOException
     */
    public static String readStreamAsStr(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        WritableByteChannel dest = Channels.newChannel(bos);
        ReadableByteChannel src = Channels.newChannel(is);
        ByteBuffer bb = ByteBuffer.allocate(4096);

        while (src.read(bb) != -1) {
            bb.flip();
            dest.write(bb);
            bb.clear();
        }
        src.close();
        dest.close();

        return bos.toString(ENCODING);
    }

    /**
     * 将图像读取为str
     * 将流转换为字符串
     *
     * @param src src
     * @return {@link String}
     * @throws IOException IOException
     */
    public static String readImageAsStr(byte[] src) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length == 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 在stream2字符串中
     *
     * @param src src
     * @return {@link String}
     * @throws IOException IOException
     */
    public static String inStream2String(InputStream src) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;
        while ((len = src.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return baos.toString();
    }

    /**
     * str映射
     *
     * @param str str
     * @return {@link Map}<{@link String},{@link String}>
     */
    private static Map<String, String> strToMap(String str) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            String[] params = str.split(SYMBOL_AND);
            for (String param : params) {
                String[] a = param.split(SYMBOL_EQUALS);
                map.put(a[0], a[1]);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }


}
