package com.chua.common.support.resource.repository.handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * classpath
 *
 * @author CH
 */
public class ClassPathStreamHandler extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return new ClassPathURLConnection(u);
    }

    final class ClassPathURLConnection extends URLConnection {

        /**
         * Constructs a URL connection to the specified URL. A connection to
         * the object referenced by the URL is not created.
         *
         * @param url the specified URL.
         */
        protected ClassPathURLConnection(URL url) {
            super(url);
        }

        @Override
        public void connect() throws IOException {

        }

        @Override
        public InputStream getInputStream() throws IOException {
            return super.getInputStream();
        }
    }
}
