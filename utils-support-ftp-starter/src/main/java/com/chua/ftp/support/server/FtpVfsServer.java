package com.chua.ftp.support.server;

import com.chua.common.support.protocol.server.AbstractServer;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.SneakyThrows;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import java.io.File;
import java.util.Optional;

/**
 * ftp
 *
 * @author CH
 */
public class FtpVfsServer extends AbstractServer {

    private static final String ANONYMOUS = "anonymous";
    protected FtpServerFactory ftpServerFactory;
    private FtpServer ftpServer;

    protected FtpVfsServer(ServerOption serverOption, String... args) {
        super(serverOption);
    }


    @SneakyThrows
    @Override
    public void run() {
        ftpServer = ftpServerFactory.createServer();
        ftpServer.start();
    }

    @Override
    public void shutdown() {
        if (null != ftpServer) {
            ftpServer.stop();
        }
    }

    @Override
    public void afterPropertiesSet() {
        this.ftpServerFactory = new FtpServerFactory();
        String username = Optional.ofNullable(request.getString("username")).orElse(ANONYMOUS);
        String password = request.getString("password");
        Object userProfile = request.getObject("user.profile");
        if (null != userProfile && userProfile instanceof File) {
            PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
            userManagerFactory.setFile((File) userProfile);
            ftpServerFactory.setUserManager(userManagerFactory.createUserManager());
            return;
        }

        BaseUser baseUser = new BaseUser();
        baseUser.setName(username);
        if (!StringUtils.isNullOrEmpty(password)) {
            baseUser.setPassword(password);
        }
        baseUser.setHomeDirectory(request.getString("store"));
        try {
            ftpServerFactory.getUserManager().save(baseUser);
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }
}
