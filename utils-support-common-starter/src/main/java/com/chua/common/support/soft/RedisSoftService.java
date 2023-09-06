package com.chua.common.support.soft;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.net.NetAddress;
import com.chua.common.support.os.OS;
import com.chua.common.support.utils.CmdUtils;
import com.chua.common.support.utils.IoUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * 服务
 * @author CH
 */
@Slf4j
@Spi("redis")
public class RedisSoftService implements SoftService{

    private final Soft soft;
    private final String version;
    private final String installPath;
    private String pid;

    public RedisSoftService(Soft soft, String version) {
        this.soft = soft;
        this.version = version;
        this.installPath = Paths.get(soft.getInstallPath(), OS.getCurrent().name() + "/redis-" + version).toAbsolutePath().toString();
        this.check();
    }

    @Override
    public void run() {
        NetAddress netAddress = NetAddress.of(installPath);
        List<String> cmd = new LinkedList<>();
        cmd.add(netAddress.getHost() + ":");
        cmd.add("cd " + installPath);
        cmd.add("redis-server.exe redis.windows.conf");
        try (FileOutputStream fos = new FileOutputStream("redis-start.bat")) {
            IoUtils.writeLines(cmd, null, fos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CmdUtils.exec("redis-start.bat");
    }

    @Override
    public void start() {
        if(null != pid) {
            return;
        }
        soft.getExecutorService().execute(this);
    }

    @Override
    public boolean check() {
        if(OS.getCurrent() == OS.WINDOWS) {
            String exec = CmdUtils.exec("tasklist | findstr redis");
            String[] split = exec.split("\r\n");
            for (String s : split) {
                String[] split1 = s.split("\\s+");
                if("console".equalsIgnoreCase(split1[2])) {
                    this.pid = split1[1];
                    return true;
                }

            }
        }
        return false;
    }

    @Override
    public String pid() {
        return pid;
    }

    @Override
    public boolean stop() {
        if(null == pid) {
            log.info("redis({})未启动", version);
            return true;
        }
        log.info(CmdUtils.exec("taskkill /f /pid " + pid));
        return true;
    }

}
