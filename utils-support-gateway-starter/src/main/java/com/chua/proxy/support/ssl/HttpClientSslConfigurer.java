package com.chua.proxy.support.ssl;

import com.chua.proxy.support.config.HttpClientProperties;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.Http2SslContextSpec;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.SslProvider;

import java.security.cert.X509Certificate;

/**
 * @author CH
 */
public class HttpClientSslConfigurer extends AbstractSslConfigurer<HttpClient, HttpClient> {

    private final HttpClientProperties serverProperties;

    public HttpClientSslConfigurer(HttpClientProperties.Ssl sslProperties, HttpClientProperties serverProperties) {
        super(sslProperties);
        this.serverProperties = serverProperties;
    }

    public HttpClient configureSsl(HttpClient client) {
        final HttpClientProperties.Ssl ssl = getSslProperties();

        if ((ssl.getKeyStore() != null && ssl.getKeyStore().length() > 0)
                || getTrustedX509CertificatesForTrustManager().length > 0 || ssl.isUseInsecureTrustManager()) {
            client = client.secure(sslContextSpec -> {
                // configure ssl
                configureSslContext(ssl, sslContextSpec);
            });
        }
        return client;
    }

    protected void configureSslContext(HttpClientProperties.Ssl ssl, SslProvider.SslContextSpec sslContextSpec) {
        SslProvider.ProtocolSslContextSpec clientSslContext = (serverProperties.getHttp2().isEnabled())
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
                logger.error(e);
            }
        });

        sslContextSpec.sslContext(clientSslContext).handshakeTimeout(ssl.getHandshakeTimeout())
                .closeNotifyFlushTimeout(ssl.getCloseNotifyFlushTimeout())
                .closeNotifyReadTimeout(ssl.getCloseNotifyReadTimeout());
    }

}
