package com.chua.ssh.support.operate;

import com.chua.common.support.constant.Projects;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * sftp
 *
 * @author CH
 */
@Slf4j
public class Ssh {

    private final Session session;

    public Ssh(Session session) {
        this.session = session;
    }

    /**
     * 执行相关的命令（交互式）
     *
     * @param command 命令
     * @return 结果
     */
    public List<String> execute(String command) {
        List<String> stdout = new LinkedList<>();
        ChannelExec channel = null;
        try {
            //建立交互式通道
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.connect();
            InputStream in = channel.getInputStream();
            //获取输入
            BufferedReader input = new BufferedReader(new InputStreamReader(in, !Projects.isWindows() ? "UTF-8" : "GBK"));
            String line;
            while ((line = input.readLine()) != null) {
                stdout.add(line);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (channel != null) {
                //关闭通道
                channel.disconnect();
            }
        }
        return stdout;
    }
}
