package com.chua.sshd.support.client;

import com.chua.common.support.constant.Projects;
import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.ThreadUtils;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * ssh客户端
 *
 * @author CH
 * @since 2023/09/19
 */
@Slf4j
public class SshClient extends AbstractClient<SshClient> {

    private final ExecutorService service = ThreadUtils.newProcessorThreadExecutor("ssh-client-monitor");

    private final Map<String, Consumer<String>> consumerMap = new ConcurrentHashMap<>();
    private Session session;
    private OutputStreamWriter writer;
    private BufferedReader reader;
    private ChannelShell shell;

    public SshClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void connectClient() {
        try {

            // 创建JSCH
            JSch ssh = new JSch();
            session = ssh.getSession(clientOption.username(), netAddress.getHost(), netAddress.getPort(22));
            session.setPassword(clientOption.password());

            // 这里是 屏蔽掉验证
            session.setConfig("StrictHostKeyChecking", "no");
//            session.setTimeout();
            session.connect(clientOption.connectionTimeoutMillis());

            // 打开shell，windows可以用 exec
            shell = (ChannelShell) session.openChannel("shell");
            shell.connect();

            writer = new OutputStreamWriter(shell.getOutputStream(), Projects.defaultCharset());
            reader = new BufferedReader(new InputStreamReader(shell.getInputStream(), Projects.defaultCharset()));
            service.execute(() -> {
                StringBuffer buffer = new StringBuffer();
                try {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        for (Consumer<String> consumer : consumerMap.values()) {
                            consumer.accept(line);
                        }
                    }
                } catch (IOException e) {
                    log.error("解析脚本出错：" + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SshClient getClient() {
        return this;
    }

    @Override
    public void close() {
        shell.disconnect();
        session.disconnect();
        IoUtils.closeQuietly(writer);
        IoUtils.closeQuietly(reader);
        ThreadUtils.closeQuietly(service);
    }

    @Override
    public void closeClient(SshClient client) {
        client.close();
    }

    /**
     * 添加侦听器
     *
     * @param consumer 消息
     * @param uid      uid
     */
    public void addListener(String uid, Consumer<String> consumer) {
        consumerMap.put(uid, consumer);
    }

    /**
     * 删除侦听器
     */
    public void removeListener(String uid) {
        consumerMap.remove(uid);
    }

    /**
     * 发送
     *
     * @param message 消息
     */
    public void send(String message) {
        try {
            writer.write(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 正在连接
     *
     * @return boolean
     */
    public boolean isConnect() {
        return null != session && session.isConnected();
    }
}
