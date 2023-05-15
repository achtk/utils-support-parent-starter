package com.chua.common.support.utils;

import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.lang.watchdog.ExecuteWatchdog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

/**
 * cmd工具
 *
 * @author CH
 * @version 1.0.0
 */
public class CmdUtils {

    private static final Pattern COMMANDS_PATTERN = Pattern.compile("[,\\s+]");
    private static final String CMD = "cmd";
    private static final String BIN = "/bin/sh";

    /**
     * 创建进程<br>
     * 命令带参数时参数可作为其中一个参数，也可以将命令和参数组合为一个字符串传入
     *
     * @param cmd 命令
     * @return {@link Process}
     */
    public static Process createProcess(String cmd) {
        return createProcess(cmd, true);
    }

    /**
     * 创建进程<br>
     * 命令带参数时参数可作为其中一个参数，也可以将命令和参数组合为一个字符串传入
     *
     * @param cmd     命令
     * @param wrapper 是否需要单引号
     * @return {@link Process}
     */
    public static Process createProcess(String cmd, boolean wrapper) {
        if (StringUtils.isEmpty(cmd)) {
            throw new NullPointerException("Command is empty !");
        }
        cmd = cmd.trim();
        int limit = 3;
        if (!cmd.startsWith(CMD) && FileUtils.isWindows()) {
            cmd = "cmd /c " + cmd;
        }

        String startCmd = " start ";
        if (FileUtils.isWindows() && cmd.indexOf(startCmd) > -1) {
            limit = 4;
        }

        if (!cmd.startsWith(BIN) && FileUtils.isLinux()) {
            cmd = "/bin/sh -c";
            if (wrapper) {
                cmd += "'" + cmd + "'";
            } else {
                cmd += cmd;
            }
        }
        final List<String> commands = Splitter.on(COMMANDS_PATTERN).trimResults().omitEmptyStrings().limit(limit).splitToList(cmd);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            String root;
            if (FileUtils.isWindows()) {
                root = "C:";
                processBuilder.directory(new File(root));
            }
            return processBuilder.redirectErrorStream(true).start();
        } catch (IOException e) {
            try {
                return Runtime.getRuntime().exec(cmd);
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

    /**
     * 执行命令<br>
     * 命令带参数时参数可作为其中一个参数，也可以将命令和参数组合为一个字符串传入
     *
     * @param command 命令
     * @return {@link Process}
     */
    public static String exec(String command) {
        return exec(command, File.separatorChar == '/' ? StandardCharsets.UTF_8 : Charset.forName("GBK"));
    }

    /**
     * 执行命令<br>
     * 命令带参数时参数可作为其中一个参数，也可以将命令和参数组合为一个字符串传入
     *
     * @param command 命令
     * @param timeout 超时
     * @return {@link Process}
     */
    public static String exec(String command, int timeout) {
        return exec(command, File.separatorChar == '/' ? StandardCharsets.UTF_8 : Charset.forName("GBK"), timeout);
    }

    /**
     * 执行命令<br>
     * 命令带参数时参数可作为其中一个参数，也可以将命令和参数组合为一个字符串传入
     *
     * @param command 命令
     * @param charset 编码
     * @return {@link Process}
     */
    public static String exec(String command, Charset charset) {
        InputStream in = null;
        Process process = createProcess(command);
        try {
            in = process.getInputStream();
            return IoUtils.toString(in, charset);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(in);
            process.destroy();
        }
        return null;
    }

    /**
     * 执行命令<br>
     * 命令带参数时参数可作为其中一个参数，也可以将命令和参数组合为一个字符串传入
     *
     * @param command 命令
     * @param charset 编码
     * @param timeout 超时
     * @return {@link Process}
     */
    public static String exec(String command, Charset charset, int timeout) {
        Process process = createProcess(command);
        ExecuteWatchdog executeWatchdog = new ExecuteWatchdog(timeout);
        executeWatchdog.setProcessNotStarted();
        executeWatchdog.monitor(process);
        int exitValue = CommonConstant.INVALID_EXITVALUE;

        try {
            exitValue = process.waitFor();
        } catch (final InterruptedException e) {
            process.destroy();
        } finally {
            Thread.interrupted();
        }
        executeWatchdog.stop();
        try {
            executeWatchdog.checkException();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return IoUtils.toString(process.getInputStream(), charset);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 执行命令<br>
     * 命令带参数时参数可作为其中一个参数，也可以将命令和参数组合为一个字符串传入
     *
     * @param command 命令
     */
    public static void execProcess(String command) {
        try {
            createProcess(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
