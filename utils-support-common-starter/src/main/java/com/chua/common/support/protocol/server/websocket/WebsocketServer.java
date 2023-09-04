package com.chua.common.support.protocol.server.websocket;


import com.chua.common.support.objects.bean.BeanObject;
import com.chua.common.support.protocol.server.AbstractServer;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.parameter.WebSocketParameterResolver;
import com.chua.common.support.protocol.server.request.WebsocketRequest;
import com.chua.common.support.protocol.websocket.WebSocket;
import com.chua.common.support.protocol.websocket.handshake.ClientHandshake;
import com.chua.common.support.protocol.websocket.server.WebSocketServer;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * websocket
 *
 * @author CH
 */
public class WebsocketServer extends AbstractServer {

    private VfsWebsocketServer vfsWebsocketServer;

    protected WebsocketServer(ServerOption serverOption, String... args) {
        super(serverOption);
    }

    @Override
    public void afterPropertiesSet() {
        super.register(new WebSocketParameterResolver());
    }

    @Override
    public void run() {
       this.vfsWebsocketServer = new VfsWebsocketServer(new InetSocketAddress(getHost(), getPort()));
        vfsWebsocketServer.start();
    }

    @Override
    public void shutdown() {
        try {
            vfsWebsocketServer.stop();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    final class VfsWebsocketServer extends WebSocketServer {


        private final Map<WebSocket, BeanObject> cache = new ConcurrentHashMap<>();

        public VfsWebsocketServer(InetSocketAddress inetSocketAddress) {
            super(inetSocketAddress);
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            BeanObject beanObject = getMapping(CONNECT);
            cache.put(conn, beanObject);
            beanObject.newInvoke(it -> getValue(it, new WebsocketRequest(conn, handshake, CONNECT, null))).invoke();
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            BeanObject beanObject = getMapping(DISCONNECT);
            cache.remove(conn);
            beanObject.newInvoke(it -> getValue(it, new WebsocketRequest(conn, null, DISCONNECT, null))).invoke();
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            BeanObject beanObject = cache.get(conn);
            if (null != beanObject) {
                //nothing
            }
            String resourceDescriptor = StringUtils.removeSuffixContains(conn.getResourceDescriptor(), "?");
            BeanObject beanObject1 = getMapping(resourceDescriptor);
            if (null == beanObject1) {
                return;
            }

            beanObject1.newInvoke(it -> getValue(it,
                    new WebsocketRequest(conn, null, resourceDescriptor, message,
                            MapUtils.asMap(StringUtils.removePrefixContains(conn.getResourceDescriptor(), "?"),
                                    "&", "=")))).invoke();

        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            BeanObject beanObject = getMapping(ERROR);
            cache.remove(conn);
            beanObject.newInvoke(it -> getValue(it, new WebsocketRequest(conn, null, ERROR, null))).invoke();
        }

        @Override
        public void onStart() {

        }
    }
}
