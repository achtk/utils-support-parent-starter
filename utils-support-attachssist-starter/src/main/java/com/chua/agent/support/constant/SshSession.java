package com.chua.agent.support.constant;

import com.alibaba.json.JSON;
import com.chua.agent.support.ws.SimpleWsServer;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.org.java_websocket.WebSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * session
 * @author CH
 */
public class SshSession implements AutoCloseable{

    private JSch jSch;

    private WebSocket webSocket;

    private Channel channel;
    Session session = null;
    private final Map<String, String> param = new LinkedHashMap<>();

    public SshSession(WebSocket webSocket, String resourceDescriptor) {
        this.webSocket = webSocket;
        this.jSch = new JSch();
        this.webSocket = webSocket;
        String substring = resourceDescriptor.substring(resourceDescriptor.indexOf("?") + 1);

        String[] split = substring.split("&");
        for (String s : split) {
            try {
                String[] split1 = s.split("=");
                param.put(split1[0], URLDecoder.decode(split1[1], "UTF-8"));
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void close() throws Exception {
        webSocket.close();
        if(null != session && session.isConnected()) {
            session.disconnect();
        }

        if(null != channel && !channel.isClosed()) {
            channel.disconnect();
        }
    }

    public boolean isMatch(WebSocket webSocket) {
        return this.webSocket == webSocket;
    }

    public void onMessage(String s) {
        WebSshData webSshData = null;
        try {
            webSshData = JSON.parseObject(s, WebSshData.class);
        } catch (Exception ignored) {
        }

        if(null == webSshData) {
            return;
        }

        if(Constant.WEBSSH_OPERATE_CONNECT.equals(webSshData.getOperate())) {
            WebSshData finalWebSshData = webSshData;
            SimpleWsServer.executorService.execute(() -> {
                try {
                    connectToSsh(finalWebSshData);
                } catch (Exception e) {
                    try {
                        close();
                    } catch (Exception ignored) {
                    }
                }
            });
            return;
        }

        if(Constant.WEBSSH_OPERATE_COMMAND.equals(webSshData.getOperate())) {
            try {
                transToSsh(webSshData.getCommand());
            } catch (Exception ignored) {
            }
        }
    }


    /**
     *  使用jsch连接终端
     */
    private void connectToSsh(WebSshData webSshData) throws Exception {

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        //获取jsch的会话
        session = jSch.getSession(webSshData.getUsername(), webSshData.getHost(), webSshData.getPort());
        session.setConfig(config);
        //设置密码
        session.setPassword(webSshData.getPassword());
        //连接  超时时间30s
        session.connect(30000);

        //开启shell通道
        Channel channel = session.openChannel("shell");
        if(channel instanceof ChannelShell) {
            ((ChannelShell) channel).setPtyType("xterm");
        }
        //通道连接 超时时间3s
        channel.connect(3000);

        //设置channel
        this.channel = channel;

        //转发消息
        transToSsh("\r");

        //读取终端返回的信息流
        try(InputStream inputStream = channel.getInputStream()){
            //循环读取
            byte[] buffer = new byte[1024];
            int i = 0;
            //如果没有数据来，线程会一直阻塞在这个地方等待数据。
            while ((i = inputStream.read(buffer)) != -1) {
                SimpleWsServer.send(webSocket, Arrays.copyOfRange(buffer, 0, i));
            }

        } finally {
            //断开连接后关闭会话
            close();
        }

    }

    /**
     * 将消息转发到终端
     */
    public void transToSsh(String command) throws IOException {
        if (channel != null) {
            OutputStream outputStream = channel.getOutputStream();
            outputStream.write(command.getBytes());
            outputStream.flush();
        }
    }


}
