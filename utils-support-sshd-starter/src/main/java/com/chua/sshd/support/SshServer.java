package com.chua.sshd.support;

import com.chua.common.support.collection.ImmutableCollection;
import com.chua.common.support.protocol.server.AbstractServer;
import com.chua.common.support.protocol.server.ServerOption;
import org.apache.sshd.common.cipher.BuiltinCiphers;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.server.ServerBuilder;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.server.subsystem.SubsystemFactory;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * server
 *
 * @author CH
 */
public class SshServer extends AbstractServer {

    org.apache.sshd.server.SshServer server;

    protected SshServer(ServerOption serverOption, String... args) {
        super(serverOption);
    }

    @Override
    public void afterPropertiesSet() {
        server = ServerBuilder.builder().build();
        server.setHost(getHost());
        server.setPort(getPort());
        VirtualFileSystemFactory fileSystemFactory = new VirtualFileSystemFactory();
        //该处参数为path  而不是String
        fileSystemFactory.setDefaultHomeDir(Paths.get(request.getString("store", ".")));
        server.setSubsystemFactories(ImmutableCollection.<SubsystemFactory>builder().add(new SftpSubsystemFactory()).newArrayList());
        server.setFileSystemFactory(fileSystemFactory);
        server.setShellFactory(new ProcessShellFactory("/bin/sh", "-i", "-l"));
        server.setCommandFactory(new ScpCommandFactory());
        server.setPasswordAuthenticator((username, password, session) ->
                request.getString("username").equals(username) && request.getString("password").equals(password));
        server.setCipherFactories(Arrays.asList(BuiltinCiphers.aes256ctr, BuiltinCiphers.aes192ctr, BuiltinCiphers.aes128ctr));
        server.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE, TimeUnit.SECONDS, 3);
    }

    @Override
    protected void shutdown() {
        try {
            server.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void run() {
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
