package com.chua.example.ftp;

import com.chua.common.support.protocol.ftp.server.FtpConnection;
import com.chua.common.support.protocol.ftp.server.FtpServer;
import com.chua.common.support.protocol.ftp.server.api.FtpListener;
import com.chua.common.support.protocol.ftp.server.impl.FtpNativeFileSystem;
import com.chua.common.support.protocol.ftp.server.impl.FtpNoOpAuthenticator;
import com.chua.common.support.utils.ThreadUtils;

import java.io.File;
import java.io.IOException;

public class FtpServerExample {

    public static void main(String[] args) throws IOException {
        FtpServer ftpServer = new FtpServer(new FtpNoOpAuthenticator(new FtpNativeFileSystem(new File("Z://"))));
        ftpServer.addListener(new FtpListener() {
            @Override
            public void onConnected(FtpConnection con) {
                System.out.println();
            }

            @Override
            public void onDisconnected(FtpConnection con) {
                System.out.println();
            }
        });
        ftpServer.listen(8888);
        while (true) {
            ThreadUtils.sleepSecondsQuietly(10);
        }
    }
}
