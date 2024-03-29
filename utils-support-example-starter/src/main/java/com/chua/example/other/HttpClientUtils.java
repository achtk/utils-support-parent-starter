package com.chua.example.other;


import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;


/**
 * http部分工具
 *
 * @author CH
 * @since 1.0
 */
public class HttpClientUtils {

    private static final CharSequence APPLICATION_JSON = "application/json";
    private static final CharSequence TEXT_XML = "text/xml";
    public static final String SYMBOL_QUESTION = "?";
    /**
     * 创建默认 HostnameVerifier
     * @return HostnameVerifier
     */
    public static HostnameVerifier createDefaultHostnameVerifier() {
        return (s, sslsession) -> true;
    }

    /**
     * 创建 默认SslContext
     *
     * @return SSLContext
     * @throws NoSuchProviderException  NoSuchProviderException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws KeyManagementException   KeyManagementException
     */
    public static SSLContext createDefaultSslContext() throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        return createSslContext("SSL", "SunJSSE");
    }

    /**
     * 创建SslContext
     *
     * @param protocol 协议
     * @param provider 生产者
     * @return SSLContext
     * @throws NoSuchProviderException  NoSuchProviderException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws KeyManagementException   KeyManagementException
     */
    public static SSLContext createSslContext(final String protocol, final String provider) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
        return createSslContext(protocol, provider, new TrustAllCerts());
    }

    /**
     * 创建SslContext
     *
     * @param protocol 协议
     * @param provider 生产者
     * @return SSLContext
     * @throws NoSuchProviderException  NoSuchProviderException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws KeyManagementException   KeyManagementException
     */
    public static SSLContext createSslContext(final String protocol, final String provider, final X509TrustManager x509TrustManager) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslcontext;
        if (null == provider) {
            sslcontext = SSLContext.getInstance(protocol);
        } else {
            sslcontext = SSLContext.getInstance(protocol, provider);
        }
        sslcontext.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
        return sslcontext;
    }

    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     *
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory createSslSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL","SunJSSE");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
        }
        return ssfFactory;
    }

    /**
     * 创建带参数的url
     *
     * @param url    url
     * @param params 消息体
     * @return url
     */
    public static String createUrlWithParameters(String url, Map<String, Object> params) {
        if (null == url) {
            return "";
        }
        String urlParameter = createWithParameters(null, params);
        return url + SYMBOL_QUESTION + urlParameter;
    }

    /**
     * 创建带参数的url
     *
     * @param contentType contentType
     * @param params      消息体
     * @return url
     */
    public static String createWithParameters(Object contentType, Map<String, Object> params) {
        if (null != contentType && contentType.toString().contains(APPLICATION_JSON)) {
            return JSON.toJSONString(params);
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (null == value) {
                continue;
            }
            if (value instanceof Iterable) {
                params.put(entry.getKey(), Joiner.on(",").join((Iterable<?>) value));
            }

            if (value instanceof Map) {
                params.put(entry.getKey(), Joiner.on("&").withKeyValueSeparator("=").join((Map<?, ?>) value));
            }
        }
        return join(params, "=", "&");
    }

    /**
     * join.
     * <pre>
     *     join({1:1}, "=", "&") = "1=1"
     *     join({1: 1, 2: 2}, "=", "&") = "1=1&2=2"
     * </pre>
     *
     * @param bodyer            数据
     * @param keyValueDelimiter key-value分隔符
     * @param itemDelimiter     数据分隔符
     * @return String.
     */
    public static String join(Map<String, Object> bodyer, String keyValueDelimiter, String itemDelimiter) {
        if (null == bodyer || bodyer.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : bodyer.entrySet()) {
            stringBuilder.append(itemDelimiter).append(entry.getKey()).append(keyValueDelimiter).append(entry.getValue());
        }

        return stringBuilder.substring(itemDelimiter.length());
    }

    /**
     * 用于信任所有证书
     */
    public static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
