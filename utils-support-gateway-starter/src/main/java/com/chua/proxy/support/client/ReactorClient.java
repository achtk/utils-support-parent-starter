package com.chua.proxy.support.client;

import com.chua.common.support.utils.StringUtils;
import com.chua.proxy.support.config.HttpClientProperties;
import com.chua.proxy.support.ssl.HttpClientSslConfigurer;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.Http2SslContextSpec;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpResponseDecoderSpec;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.SslProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;

import static com.chua.proxy.support.config.HttpClientProperties.Pool.PoolType.DISABLED;
import static com.chua.proxy.support.config.HttpClientProperties.Pool.PoolType.FIXED;

/**
 * client
 * @author CH
 */
@Slf4j
public class ReactorClient {

    private final HttpClientProperties properties;
    private final HttpClientSslConfigurer sslConfigurer;

    public ReactorClient(HttpClientProperties properties, HttpClientSslConfigurer sslConfigurer) {
        this.properties = properties;
        this.sslConfigurer = sslConfigurer;
    }

    /**
     * 创建实例
     *
     * @return {@link HttpClient}
     */
    public HttpClient createInstance() {
        // configure pool resources
        ConnectionProvider connectionProvider = buildConnectionProvider(properties);

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .httpResponseDecoder(this::httpResponseDecoder);

        if (properties.getHttp2().isEnabled()) {
            httpClient = httpClient.protocol(HttpProtocol.HTTP11, HttpProtocol.H2);
        }

        if (properties.getConnectTimeout() != null) {
            httpClient = httpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout());
        }

        httpClient = configureProxy(httpClient);

        httpClient = configureSsl(httpClient);

        if (properties.isWiretap()) {
            httpClient = httpClient.wiretap(true);
        }

        if (properties.isCompression()) {
            httpClient = httpClient.compress(true);
        }

        return httpClient;
    }

    protected HttpClient configureSsl(HttpClient httpClient) {
        if (sslConfigurer != null) {
            return sslConfigurer.configureSsl(httpClient);
        }

        HttpClientProperties.Ssl ssl = properties.getSsl();
        if ((ssl.getKeyStore() != null && ssl.getKeyStore().length() > 0)
                || getTrustedX509CertificatesForTrustManager().length > 0 || ssl.isUseInsecureTrustManager()) {
            httpClient = httpClient.secure(sslContextSpec -> {
                // configure ssl
                configureSslContext(ssl, sslContextSpec);
            });
        }
        return httpClient;
    }

    protected void configureSslContext(HttpClientProperties.Ssl ssl, SslProvider.SslContextSpec sslContextSpec) {
        SslProvider.ProtocolSslContextSpec clientSslContext = (properties.getHttp2().isEnabled())
                ? Http2SslContextSpec.forClient() : Http11SslContextSpec.forClient();
        clientSslContext.configure(sslContextBuilder -> {
            X509Certificate[] trustedX509Certificates = getTrustedX509CertificatesForTrustManager();
            if (trustedX509Certificates.length > 0) {
                setTrustManager(sslContextBuilder, trustedX509Certificates);
            }
            else if (ssl.isUseInsecureTrustManager()) {
                setTrustManager(sslContextBuilder, InsecureTrustManagerFactory.INSTANCE);
            }

            try {
                sslContextBuilder.keyManager(getKeyManagerFactory());
            }
            catch (Exception e) {
                log.error("", e);
            }
        });

        sslContextSpec.sslContext(clientSslContext).handshakeTimeout(ssl.getHandshakeTimeout())
                .closeNotifyFlushTimeout(ssl.getCloseNotifyFlushTimeout())
                .closeNotifyReadTimeout(ssl.getCloseNotifyReadTimeout());
    }

    protected KeyManagerFactory getKeyManagerFactory() {
        HttpClientProperties.Ssl ssl = properties.getSsl();
        try {
            if (ssl.getKeyStore() != null && ssl.getKeyStore().length() > 0) {
                KeyManagerFactory keyManagerFactory = KeyManagerFactory
                        .getInstance(KeyManagerFactory.getDefaultAlgorithm());
                char[] keyPassword = ssl.getKeyPassword() != null ? ssl.getKeyPassword().toCharArray() : null;

                if (keyPassword == null && ssl.getKeyStorePassword() != null) {
                    keyPassword = ssl.getKeyStorePassword().toCharArray();
                }

                keyManagerFactory.init(this.createKeyStore(), keyPassword);

                return keyManagerFactory;
            }

            return null;
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    protected KeyStore createKeyStore() {
        HttpClientProperties.Ssl ssl = properties.getSsl();
        try {
            KeyStore store = ssl.getKeyStoreProvider() != null
                    ? KeyStore.getInstance(ssl.getKeyStoreType(), ssl.getKeyStoreProvider())
                    : KeyStore.getInstance(ssl.getKeyStoreType());
            try {
                URL url = ReactorClient.class.getResource(ssl.getKeyStore());
                store.load(url.openStream(),
                        ssl.getKeyStorePassword() != null ? ssl.getKeyStorePassword().toCharArray() : null);
            }
            catch (Exception e) {
                throw new RuntimeException("Could not load key store ' " + ssl.getKeyStore() + "'", e);
            }

            return store;
        }
        catch (KeyStoreException | NoSuchProviderException e) {
            throw new RuntimeException("Could not load KeyStore for given type and provider", e);
        }
    }


    protected void setTrustManager(SslContextBuilder sslContextBuilder, X509Certificate... trustedX509Certificates) {
        sslContextBuilder.trustManager(trustedX509Certificates);
    }

    protected void setTrustManager(SslContextBuilder sslContextBuilder, TrustManagerFactory factory) {
        sslContextBuilder.trustManager(factory);
    }
    protected X509Certificate[] getTrustedX509CertificatesForTrustManager() {
        HttpClientProperties.Ssl ssl = properties.getSsl();

        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            ArrayList<Certificate> allCerts = new ArrayList<>();
            for (String trustedCert : ssl.getTrustedX509Certificates()) {
                try {
                    URL url = ReactorClient.class.getResource(trustedCert);
                    Collection<? extends Certificate> certs = certificateFactory.generateCertificates(url.openStream());
                    allCerts.addAll(certs);
                }
                catch (IOException e) {
                    throw new RuntimeException("Could not load certificate '" + trustedCert + "'", e);
                }
            }
            return allCerts.toArray(new X509Certificate[allCerts.size()]);
        }
        catch (CertificateException e1) {
            throw new RuntimeException("Could not load CertificateFactory X.509", e1);
        }
    }
    /**
     * 配置代理
     *
     * @param httpClient http客户端
     * @return {@link HttpClient}
     */
    protected HttpClient configureProxy(HttpClient httpClient) {
        // configure proxy if proxy host is set.
        if (StringUtils.isNotEmpty(properties.getProxy().getHost())) {
            HttpClientProperties.Proxy proxy = properties.getProxy();

//            httpClient = httpClient.proxy(proxySpec -> {
//                configureProxyProvider(proxy, proxySpec);
//            });
        }
        return httpClient;
    }

    /**
     * http响应解码器
     *
     * @param spec 规格
     * @return {@link HttpResponseDecoderSpec}
     */
    protected HttpResponseDecoderSpec httpResponseDecoder(HttpResponseDecoderSpec spec) {
        if (properties.getMaxHeaderSize() != null) {
            // cast to int is ok, since @Max is Integer.MAX_VALUE
            spec.maxHeaderSize(properties.getMaxHeaderSize());
        }
        if (properties.getMaxInitialLineLength() != null) {
            // cast to int is ok, since @Max is Integer.MAX_VALUE
            spec.maxInitialLineLength(properties.getMaxInitialLineLength());
        }
        return spec;
    }
    /**
     * 构建联系提供者
     *
     * @param properties 特性
     * @return {@link ConnectionProvider}
     */
    protected ConnectionProvider buildConnectionProvider(HttpClientProperties properties) {
        HttpClientProperties.Pool pool = properties.getPool();

        ConnectionProvider connectionProvider;
        if (pool.getType() == DISABLED) {
            connectionProvider = ConnectionProvider.newConnection();
        }
        else {
            // create either Fixed or Elastic pool
            ConnectionProvider.Builder builder = ConnectionProvider.builder(pool.getName());
            if (pool.getType() == FIXED) {
                builder.maxConnections(pool.getMaxConnections()).pendingAcquireMaxCount(-1)
                        .pendingAcquireTimeout(Duration.ofMillis(pool.getAcquireTimeout()));
            }
            else {
                // Elastic
                builder.maxConnections(Integer.MAX_VALUE).pendingAcquireTimeout(Duration.ofMillis(0))
                        .pendingAcquireMaxCount(-1);
            }

            if (pool.getMaxIdleTime() != null) {
                builder.maxIdleTime(pool.getMaxIdleTime());
            }
            if (pool.getMaxLifeTime() != null) {
                builder.maxLifeTime(pool.getMaxLifeTime());
            }
            builder.evictInBackground(pool.getEvictionInterval());
            builder.metrics(pool.isMetrics());
            connectionProvider = builder.build();
        }
        return connectionProvider;
    }
}
