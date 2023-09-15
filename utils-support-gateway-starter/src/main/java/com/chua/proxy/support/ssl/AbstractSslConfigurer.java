package com.chua.proxy.support.ssl;


import com.chua.proxy.support.config.HttpClientProperties;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
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
import java.util.ArrayList;
import java.util.Collection;

/**
 * Base class to configure SSL for component T. Returns an instance S with the resulting
 * configuration (can be the same as T).
 *
 * @author Abel Salgado Romero
 */
public abstract class AbstractSslConfigurer<T, S> {

    protected final Log logger = LogFactory.getLog(this.getClass());

    private final HttpClientProperties.Ssl ssl;

    protected AbstractSslConfigurer(HttpClientProperties.Ssl sslProperties) {
        this.ssl = sslProperties;
    }

    abstract public S configureSsl(T client) throws SSLException;

    protected HttpClientProperties.Ssl getSslProperties() {
        return ssl;
    }

    protected X509Certificate[] getTrustedX509CertificatesForTrustManager() {

        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            ArrayList<Certificate> allCerts = new ArrayList<>();
            for (String trustedCert : ssl.getTrustedX509Certificates()) {
                try {
                    URL url = AbstractSslConfigurer.class.getResource(trustedCert);
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

    protected KeyManagerFactory getKeyManagerFactory() {

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

        try {
            KeyStore store = ssl.getKeyStoreProvider() != null
                    ? KeyStore.getInstance(ssl.getKeyStoreType(), ssl.getKeyStoreProvider())
                    : KeyStore.getInstance(ssl.getKeyStoreType());
            try {
                URL url = AbstractSslConfigurer.class.getResource(ssl.getKeyStore());
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

}
