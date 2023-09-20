package com.chua.sshd.support.command;

import com.chua.common.support.shell.ShellMapping;
import com.chua.common.support.shell.ShellParam;
import com.chua.common.support.shell.ShellResult;
import com.chua.common.support.utils.StringUtils;

/**
 * ssh命令
 *
 * @author CH
 */
public class SshCommand {

    @ShellMapping(value = {"ssh"}, describe = "ssh")
    public ShellResult ssh(
            @ShellParam(value = "host", describe = "地址", defaultValue = "127.0.0.1") String host,
            @ShellParam(value = "port", shortName = "P", describe = "端口", defaultValue = "22") int port,
            @ShellParam(value = "user", describe = "用户", defaultValue = "root") String user,
            @ShellParam(value = "password", describe = "密码") String password
            ) {

        if(StringUtils.isEmpty(password)) {
            return ShellResult.ask("请输入密码:", true);
        }
        return ShellResult.error("");
    }
}
