/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

package com.chua.common.support.protocol.websocket.server;

import com.chua.common.support.protocol.websocket.SSLSocketChannel2;
import com.chua.common.support.protocol.websocket.WebSocketAdapter;
import com.chua.common.support.protocol.websocket.WebSocketImpl;
import com.chua.common.support.protocol.websocket.WebSocketServerFactory;
import com.chua.common.support.protocol.websocket.drafts.Draft;
import com.chua.common.support.utils.ThreadUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author Administrator
 */
public class DefaultSslWebSocketServerFactory implements WebSocketServerFactory {

    protected SSLContext sslcontext;
    protected ExecutorService exec;

    public DefaultSslWebSocketServerFactory(SSLContext sslContext) {
        this(sslContext, ThreadUtils.newSingleThreadScheduledExecutor());
    }

    public DefaultSslWebSocketServerFactory(SSLContext sslContext, ExecutorService exec) {
        if (sslContext == null || exec == null) {
            throw new IllegalArgumentException();
        }
        this.sslcontext = sslContext;
        this.exec = exec;
    }

    @Override
    public ByteChannel wrapChannel(SocketChannel channel, SelectionKey key) throws IOException {
        SSLEngine e = sslcontext.createSSLEngine();
        /*
         * See https://github.com/TooTallNate/Java-WebSocket/issues/466
         *
         * We remove TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256 from the enabled ciphers since it is just available when you patch your java installation directly.
         * E.g. firefox requests this cipher and this causes some dcs/instable connections
         */
        List<String> ciphers = new ArrayList<>(Arrays.asList(e.getEnabledCipherSuites()));
        ciphers.remove("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
        e.setEnabledCipherSuites(ciphers.toArray(new String[ciphers.size()]));
        e.setUseClientMode(false);
        return new SSLSocketChannel2(channel, e, exec, key);
    }

    @Override
    public WebSocketImpl createWebSocket(WebSocketAdapter a, Draft d) {
        return new WebSocketImpl(a, d);
    }

    @Override
    public WebSocketImpl createWebSocket(WebSocketAdapter webSocketAdapter, List<Draft> d) {
        return new WebSocketImpl(webSocketAdapter, d);
    }

    @Override
    public void close() {
        exec.shutdown();
    }
}