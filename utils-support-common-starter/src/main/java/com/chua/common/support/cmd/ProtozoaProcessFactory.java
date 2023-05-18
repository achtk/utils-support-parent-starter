package com.chua.common.support.cmd;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.lang.cmd.ProcessFactory;
import com.chua.common.support.lang.watchdog.ExecuteWatchdog;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.ThreadUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.chua.common.support.constant.CommonConstant.OS_NAME;

/**
 * 原生
 *
 * @author CH
 * @since 2021-12-07
 */
@Spi({"exec", "jdk"})
public class ProtozoaProcessFactory implements ProcessFactory {
    private static final Pattern COMMANDS_PATTERN = Pattern.compile("[,\\s+]");
    private static final String CMD = "cmd";
    private static final String BIN = "/bin/sh";

    protected int timeout;
    protected Charset charset = OS_NAME.toUpperCase().contains("WINDOWS") ? Charset.forName("GBK") : StandardCharsets.UTF_8;
    protected boolean handleQuoting;
    protected Map<String, String> params = new LinkedHashMap<>();
    protected List<String> args = new LinkedList<>();
    protected String directory;

    @Override
    public ProcessFactory directory(String directory) {
        this.directory = directory;
        return this;
    }

    @Override
    public ProcessFactory handleQuoting(boolean handleQuoting) {
        this.handleQuoting = handleQuoting;
        return this;
    }

    @Override
    public ProcessFactory argument(String argument) {
        args.add(argument);
        return this;
    }

    @Override
    public ProcessFactory substitution(String key, String value) {
        params.put(key, value);
        return this;
    }

    @Override
    public ProcessFactory timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public ProcessFactory charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    @Override
    public ProcessStatus exec(String cmd) {
        ProcessStatus processStatus = new ProcessStatus();
        processStatus.setCode(-1);

        Process process = null;
        String w = "WINDOWS";
        if (OS_NAME.toUpperCase().contains(w)) {
            process = new Java13CommandLauncher().createProcess(cmd);
        } else {
            process = new Os2CommandLauncher().createProcess(cmd);
        }

        ExecuteWatchdog executeWatchdog = null;
        if (timeout > 0) {
            executeWatchdog = new ExecuteWatchdog(timeout);
            executeWatchdog.monitor(process);
        }
        int exitValue = ThreadUtils.INVALID_EXITVALUE;

        try {
            exitValue = process.waitFor();
        } catch (final InterruptedException e) {
            process.destroy();
            processStatus.setOutput(e.getMessage());

            return processStatus;
        } finally {
            Thread.interrupted();
        }

        if (null != executeWatchdog) {
            executeWatchdog.stop();
            try {
                executeWatchdog.checkException();
            } catch (Exception e) {
                processStatus.setOutput(e.getMessage());
                return processStatus;
            }
        }
        try {
            processStatus.setOutput(IoUtils.toString(1 == exitValue ? process.getInputStream() : process.getErrorStream(), charset));
            processStatus.setCode(0);

        } catch (IOException e) {
            processStatus.setOutput(e.getMessage());
        }
        return processStatus;
    }

    public static interface ProcessCommandLauncher {
        /**
         * 創建進程
         *
         * @param cmd cmd
         * @return 進程
         */
        Process createProcess(String cmd);
    }

    class Java13CommandLauncher implements ProcessCommandLauncher {

        @Override
        public Process createProcess(String cmd) {
            try {
                Object args1 = getArgs(cmd);
                if (args1 instanceof String) {
                    return Runtime.getRuntime().exec(args1.toString(), getEnvVars(), new File(getDirectory()));
                }
                return Runtime.getRuntime().exec((String[]) args1, getEnvVars(), new File(getDirectory()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class Os2CommandLauncher implements ProcessCommandLauncher {

        @Override
        public Process createProcess(String cmd) {
            try {
                Object args1 = getArgs(cmd);
                String[] args = args1 instanceof String ? new String[]{args1.toString()} : (String[]) args1;
                String[] newArgs = new String[args.length + 2];
                if (FileUtils.isWindows()) {
                    newArgs[0] = "cmd.exe";
                    newArgs[1] = "/c";
                } else {
                    newArgs[0] = "/bin/sh";
                    newArgs[1] = "-c";
                }

                System.arraycopy(args, 0, newArgs, 2, args.length);

                return Runtime.getRuntime().exec(newArgs,
                        getEnvVars(), new File(getDirectory()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 环境变量
     *
     * @return 环境变量
     */
    private String[] getEnvVars() {
        if (null == params || params.isEmpty()) {
            return new String[0];
        }
        return Joiner.on(",").withKeyValueSeparator("=").join(params).split(",");
    }

    private String getDirectory() {
        if (!StringUtils.isNullOrEmpty(directory)) {
            return directory;
        }

        if (FileUtils.isWindows()) {
            return "C:";
        }
        return "";
    }


    private Object getArgs(String cmd) {
        if (this.args.isEmpty()) {
            return cmd;
        }
        final List<String> commands = new LinkedList<>();
        if (null != cmd) {
            commands.add(cmd);
        }
        commands.addAll(this.args);

        final List<String> newArgs = new LinkedList<>();
        for (String arg : commands) {
            if (arg.startsWith("${") && arg.endsWith(CommonConstant.SYMBOL_RIGHT_BIG_PARENTHESES)) {
                if (params.containsKey(arg)) {
                    newArgs.add(params.get(arg));
                    continue;
                }
            }

            newArgs.add(arg);
        }
        return newArgs.toArray(new String[0]);
    }

}
