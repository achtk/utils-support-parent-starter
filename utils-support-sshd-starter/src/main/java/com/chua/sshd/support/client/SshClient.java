package com.chua.sshd.support.client;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.ThreadUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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
public class SshClient extends AbstractClient<Session> {

    private Connection connection;
    private Session session;
    private BufferedReader stdout;
    public PrintWriter printWriter;
    private BufferedReader stderr;
    private final ExecutorService service = ThreadUtils.newProcessorThreadExecutor("ssh-client-monitor");

    private final Map<String, Consumer<String>> consumerMap = new ConcurrentHashMap<>();
    public SshClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void connectClient() {
        try {
            //根据主机名先获取一个远程连接
            connection = new Connection(netAddress.getHost(), netAddress.getPort(22));
            //发起连接
            connection.connect();
            //认证账号密码
            boolean authenticateWithPassword = connection.authenticateWithPassword(clientOption.username(), clientOption.password());
            //如果账号密码有误抛出异常
            if (!authenticateWithPassword) {
                throw new RuntimeException("Authentication failed. Please check hostName, userName and passwd");
            }
            //开启一个会话
            session = connection.openSession();
            session.requestDumbPTY();
            session.startShell();
            //获取标准输出
            stdout = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStdout()), StandardCharsets.UTF_8));
            //获取标准错误输出
            stderr = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStderr()), StandardCharsets.UTF_8));
            //获取标准输入
            printWriter = new PrintWriter(session.getStdin());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Session getClient() {
        return session;
    }

    @Override
    public void close() {
        ThreadUtils.closeQuietly(service);
        IoUtils.closeQuietly(stdout);
        IoUtils.closeQuietly(stderr);
        IoUtils.closeQuietly(printWriter);
        session.close();
        connection.close();
    }

    @Override
    public void closeClient(Session client) {
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
     *
     * @param consumer 消息
     */
    public void removeListener(String uid, Consumer<String> consumer) {
        consumerMap.remove(uid);
    }
    /**
     * 发送
     *
     * @param message 消息
     */
    public void send(String message) {
        printWriter.write(message);
    }
}
