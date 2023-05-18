package com.chua.agent.support.ws;

import com.chua.agent.support.Agent;
import com.chua.agent.support.constant.SshSession;
import com.chua.agent.support.span.span.Span;
import com.org.java_websocket.WebSocket;
import com.org.java_websocket.handshake.ClientHandshake;
import com.org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;


/**
 * ws
 *
 * @author CH
 * @since 2021-08-27
 */
public class SimpleWsServer extends WebSocketServer {

    public static Map<String, WebSocket> traceRoom = new ConcurrentHashMap<>();
    public static Map<String, WebSocket> sqlRoom = new ConcurrentHashMap<>();
    public static Map<String, WebSocket> logRoom = new ConcurrentHashMap<>();
    public static Map<String, SshSession> sshRoom = new ConcurrentHashMap<>();
    public static Map<String, WebSocket> vmRoom = new ConcurrentHashMap<>();
    public static final ExecutorService executorService = Executors.newCachedThreadPool();

    public SimpleWsServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        Agent.log(Level.INFO, "websocket Server start at port: {}", port);
    }

    /**
     * 发送消息
     *
     * @param log 数据
     */
    public static void sendLog(String log) {
        for (WebSocket webSocket : logRoom.values()) {
            try {
                webSocket.send(log);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 发送消息
     *
     * @param log 数据
     */
    public static void sendVm(String log) {
        for (WebSocket webSocket : vmRoom.values()) {
            try {
                webSocket.send(log);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 发送消息
     *
     * @param exitSpan 数据
     */
    public static void send(Span exitSpan, String point) {
        if ("trace".equalsIgnoreCase(point)) {
            try {
                for (WebSocket webSocket : traceRoom.values()) {
                    try {
                        webSocket.send(exitSpan.toJson());
                        Agent.logResolver.register(exitSpan);
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception ignored) {
            }
        } else if ("sql".equalsIgnoreCase(point)) {
            for (WebSocket webSocket : sqlRoom.values()) {
                try {
                    webSocket.send(exitSpan.toJson());
                    Agent.logResolver.register(exitSpan);
                } catch (Exception ignored) {
                }
            }
        } else if ("vm".equalsIgnoreCase(point)) {
            for (WebSocket webSocket : vmRoom.values()) {
                try {
                    webSocket.send(exitSpan.toJson());
                    Agent.logResolver.register(exitSpan);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void send(WebSocket webSocket, byte[] copyOfRange) {
        if (webSocket == null) {
            return;
        }

        webSocket.send(new String(copyOfRange, StandardCharsets.UTF_8));
    }


    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        String resourceDescriptor = clientHandshake.getResourceDescriptor();
        if ("/trace".equals(resourceDescriptor)) {
            traceRoom.put(webSocket.getRemoteSocketAddress().toString(), webSocket);
        } else if (resourceDescriptor.endsWith("/sql")) {
            sqlRoom.put(webSocket.getRemoteSocketAddress().toString(), webSocket);
        } else if (resourceDescriptor.endsWith("/log")) {
            logRoom.put(webSocket.getRemoteSocketAddress().toString(), webSocket);
        } else if (resourceDescriptor.endsWith("/vm")) {
            vmRoom.put(webSocket.getRemoteSocketAddress().toString(), webSocket);
        } else if (resourceDescriptor.contains("/ssh")) {
            sshRoom.put(webSocket.getRemoteSocketAddress().toString(), new SshSession(webSocket, resourceDescriptor));
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        String address = webSocket.getRemoteSocketAddress().toString();
        closeQuality(address);
        closeQuality(webSocket);
    }

    private void closeQuality(SshSession sshSession) {
        if (null == sshSession) {
            return;
        }

        try {
            sshSession.close();
        } catch (Exception ignored) {
        }

    }

    private void closeQuality(WebSocket webSocket1) {
        if (null == webSocket1) {
            return;
        }

        webSocket1.close();
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        for (Map.Entry<String, SshSession> entry : sshRoom.entrySet()) {
            SshSession sshSession = entry.getValue();
            if (sshSession.isMatch(webSocket)) {
                sshSession.onMessage(s);
            }
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        closeQuality(webSocket);
        String address = webSocket.getRemoteSocketAddress().toString();
        closeQuality(address);
    }

    private void closeQuality(String address) {
        WebSocket webSocket1 = traceRoom.get(address);
        closeQuality(webSocket1);
        WebSocket webSocket2 = sqlRoom.get(address);
        closeQuality(webSocket2);

        WebSocket webSocket3 = logRoom.get(address);
        closeQuality(webSocket3);

        WebSocket webSocket31 = vmRoom.get(address);
        closeQuality(webSocket31);

        SshSession sshSession = sshRoom.get(address);
        closeQuality(sshSession);
    }

    @Override
    public void onStart() {
        setConnectionLostTimeout(100);
    }
}
