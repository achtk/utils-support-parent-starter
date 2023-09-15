package com.chua.proxy.support.config;

import lombok.Data;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.SslProvider;
import reactor.netty.transport.ProxyProvider;

import javax.net.ssl.KeyManagerFactory;
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
import java.util.List;

/**
 * @author CH
 */
@Data
public class HttpClientProperties {

    /** The connect timeout in millis, the default is 30s. */
    private Integer connectTimeout;

    /** The response timeout. */
    private Duration responseTimeout;

    /** The max response header size. */
    private Integer maxHeaderSize;

    /** The max initial line length. */
    private Integer maxInitialLineLength;

    /** Pool configuration for Netty HttpClient. */
    private Pool pool = new Pool();

    /** Proxy configuration for Netty HttpClient. */
    private Proxy proxy = new Proxy();

    /** SSL configuration for Netty HttpClient. */
    private Ssl ssl = new Ssl();

    /** Websocket configuration for Netty HttpClient. */
    private Websocket websocket = new Websocket();

    /** Enables wiretap debugging for Netty HttpClient. */
    private boolean wiretap;

    /** Enables compression for Netty HttpClient. */
    private boolean compression;

    private Http2 http2;

    public Http2 getHttp2() {
        return this.http2;
    }
    @Data
    public static class Pool {

        /** Type of pool for HttpClient to use, defaults to ELASTIC. */
        private PoolType type = PoolType.ELASTIC;

        /** The channel pool map name, defaults to proxy. */
        private String name = "proxy";

        /**
         * Only for type FIXED, the maximum number of connections before starting pending
         * acquisition on existing ones.
         */
        private Integer maxConnections = ConnectionProvider.DEFAULT_POOL_MAX_CONNECTIONS;

        /** Only for type FIXED, the maximum time in millis to wait for acquiring. */
        private Long acquireTimeout = ConnectionProvider.DEFAULT_POOL_ACQUIRE_TIMEOUT;

        /**
         * Time in millis after which the channel will be closed. If NULL, there is no max
         * idle time.
         */
        private Duration maxIdleTime = null;

        /**
         * Duration after which the channel will be closed. If NULL, there is no max life
         * time.
         */
        private Duration maxLifeTime = null;

        /**
         * Perform regular eviction checks in the background at a specified interval.
         * Disabled by default ({@link Duration#ZERO})
         */
        private Duration evictionInterval = Duration.ZERO;

        /**
         * Enables channel pools metrics to be collected and registered in Micrometer.
         * Disabled by default.
         */
        private boolean metrics = false;

        public enum PoolType {

            /**
             * Elastic pool type.
             */
            ELASTIC,

            /**
             * Fixed pool type.
             */
            FIXED,

            /**
             * Disabled pool type.
             */
            DISABLED

        }

    }

    @Data
    public static class Proxy {

        /** proxyType for proxy configuration of Netty HttpClient. */
        private ProxyProvider.Proxy type = ProxyProvider.Proxy.HTTP;

        /** Hostname for proxy configuration of Netty HttpClient. */
        private String host;

        /** Port for proxy configuration of Netty HttpClient. */
        private Integer port;

        /** Username for proxy configuration of Netty HttpClient. */
        private String username;

        /** Password for proxy configuration of Netty HttpClient. */
        private String password;

        /**
         * Regular expression (Java) for a configured list of hosts. that should be
         * reached directly, bypassing the proxy
         */
        private String nonProxyHostsPattern;


    }

    @Data
    public static class Ssl {

        /**
         * Installs the netty InsecureTrustManagerFactory. This is insecure and not
         * suitable for production.
         */
        private boolean useInsecureTrustManager = false;

        /** Trusted certificates for verifying the remote endpoint's certificate. */
        private List<String> trustedX509Certificates = new ArrayList<>();

        // use netty default SSL timeouts
        /** SSL handshake timeout. Default to 10000 ms */
        private Duration handshakeTimeout = Duration.ofMillis(10000);

        /** SSL close_notify flush timeout. Default to 3000 ms. */
        private Duration closeNotifyFlushTimeout = Duration.ofMillis(3000);

        /** SSL close_notify read timeout. Default to 0 ms. */
        private Duration closeNotifyReadTimeout = Duration.ZERO;

        /** The default ssl configuration type. Defaults to TCP. */
        @Deprecated
        private SslProvider.DefaultConfigurationType defaultConfigurationType = SslProvider.DefaultConfigurationType.TCP;

        /** Keystore path for Netty HttpClient. */
        private String keyStore;

        /** Keystore type for Netty HttpClient, default is JKS. */
        private String keyStoreType = "JKS";

        /** Keystore provider for Netty HttpClient, optional field. */
        private String keyStoreProvider;

        /** Keystore password. */
        private String keyStorePassword;

        /** Key password, default is same as keyStorePassword. */
        private String keyPassword;


        @Deprecated
        public X509Certificate[] getTrustedX509CertificatesForTrustManager() {
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                ArrayList<Certificate> allCerts = new ArrayList<>();
                for (String trustedCert : getTrustedX509Certificates()) {
                    try {
                        URL url = HttpClientProperties.class.getResource(trustedCert);
                        Collection<? extends Certificate> certs = certificateFactory
                                .generateCertificates(url.openStream());
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

        @Deprecated
        public KeyManagerFactory getKeyManagerFactory() {
            try {
                if (getKeyStore() != null && getKeyStore().length() > 0) {
                    KeyManagerFactory keyManagerFactory = KeyManagerFactory
                            .getInstance(KeyManagerFactory.getDefaultAlgorithm());
                    char[] keyPassword = getKeyPassword() != null ? getKeyPassword().toCharArray() : null;

                    if (keyPassword == null && getKeyStorePassword() != null) {
                        keyPassword = getKeyStorePassword().toCharArray();
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

        @Deprecated
        public KeyStore createKeyStore() {
            try {
                KeyStore store = getKeyStoreProvider() != null
                        ? KeyStore.getInstance(getKeyStoreType(), getKeyStoreProvider())
                        : KeyStore.getInstance(getKeyStoreType());
                try {
                    URL url = HttpClientProperties.class.getResource(getKeyStore());
                    store.load(url.openStream(),
                            getKeyStorePassword() != null ? getKeyStorePassword().toCharArray() : null);
                }
                catch (Exception e) {
                    throw new RuntimeException("Could not load key store ' " + getKeyStore() + "'", e);
                }

                return store;
            }
            catch (KeyStoreException | NoSuchProviderException e) {
                throw new RuntimeException("Could not load KeyStore for given type and provider", e);
            }
        }

    }

    @Data
    public static class Websocket {

        /** Max frame payload length. */
        private Integer maxFramePayloadLength;

        /** Proxy ping frames to downstream services, defaults to true. */
        private boolean proxyPing = true;


    }

}
