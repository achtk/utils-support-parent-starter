package com.chua.server.support.server.socketio;

import com.chua.common.support.objects.bean.BeanObject;
import com.chua.common.support.protocol.server.AbstractServer;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.annotations.Mapping;
import com.chua.common.support.utils.StringUtils;
import com.chua.server.support.server.parameter.SocketIoParameterResolver;
import com.chua.server.support.server.request.SocketIoRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Predicate;

/**
 * SocketIo
 *
 * @author CH
 */
public class SocketIoServer extends AbstractServer {

    /**
     * 基础配置
     */
    private Configuration configuration;
    private SocketIOServer ioServer;

    protected SocketIoServer(ServerOption serverOption, String... args) {
        super(serverOption);
    }


    @Override
    public void run() {
        this.ioServer = new SocketIOServer(configuration);
        String[] namespaces = request.getStringArray("namespace");
        if (null != namespaces) {
            for (String namespace : namespaces) {
                ioServer.addNamespace(namespace);
            }
        }


        BeanObject connect = getMapping(CONNECT);
        BeanObject disconnect = getMapping(DISCONNECT);

        ioServer.addConnectListener(client -> {
            if (null == connect) {
                return;
            }

            connect.newInvoke(parameterDescribe -> super.getValue(parameterDescribe, new SocketIoRequest(client, CONNECT))).invoke();
        });

        ioServer.addDisconnectListener(client -> {
            if (null == disconnect) {
                return;
            }

            disconnect.newInvoke(parameterDescribe -> super.getValue(parameterDescribe, new SocketIoRequest(client, DISCONNECT))).invoke();
        });

        Map<String, BeanObject> mappingByMethodParameterType = getMappingByMethodParameterType(Mapping.class);
        for (Map.Entry<String, BeanObject> entry : mappingByMethodParameterType.entrySet()) {
            String key = entry.getKey();
            if (key.equals(CONNECT) || key.equals(DISCONNECT)) {
                continue;
            }
            ioServer.addEventListener(key, String.class, (client, data, ackSender) -> {
                disconnect.newInvoke(parameterDescribe -> super.getValue(parameterDescribe, new SocketIoRequest(client, key))).invoke();
            });
        }
        ioServer.startAsync();
    }

    @Override
    public void shutdown() {
        if (null != ioServer) {
            ioServer.stop();
        }
    }

    @Override
    public void afterPropertiesSet() {
        this.configuration = new Configuration();
        super.register(new SocketIoParameterResolver());

        String host = getHost();
        int port = getPort();

        String keyStorePassword = request.getString( "key-store-password");
        InputStream keyStore = (InputStream) request.getObject( "keyStore");

        String origin = request.getString( "origin");
        String version = request.getString( "version");
        String context = request.getString( "context");
        String sslProtocol = request.getString( "sslProtocol");
        Predicate<Object> authorization = (Predicate<Object>) request.getObject( "authorization");

        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setTcpNoDelay(request.getBooleanValue( "tcpNoDelay", true));
        socketConfig.setSoLinger(0);

        configuration.setSocketConfig(socketConfig);
        if (!StringUtils.isNullOrEmpty(host)) {
            configuration.setHostname(host);
        }

        configuration.setPort(port);
        configuration.setBossThreads(request.getIntValue("max-total", 100));
        configuration.setWorkerThreads(request.getIntValue("max-idle", 100));
        configuration.setAllowCustomRequests(request.getBooleanValue("allowCustomRequests", true));
        configuration.setUpgradeTimeout(request.getIntValue("upgradeTimeout", 10000));
        configuration.setPingTimeout(request.getIntValue("pingTimeout", 60000));
        configuration.setPingInterval(request.getIntValue("pingInterval", 25000));
        configuration.setWebsocketCompression(request.getBooleanValue("websocketCompression", true));

        if (null != keyStorePassword) {
            configuration.setKeyStorePassword(keyStorePassword);
        }

        if (null != keyStore) {
            configuration.setKeyStore(keyStore);
        }

        if (null != authorization) {
            configuration.setAuthorizationListener(authorization::test);
        }

        if (null != origin) {
            configuration.setOrigin(origin);
        }

        if (null != origin) {
            configuration.setContext(context);
        }

        if (null != version) {
            configuration.setAddVersionHeader(true);
        }

        if (null != sslProtocol) {
            configuration.setSSLProtocol(sslProtocol);
        }
    }
}
