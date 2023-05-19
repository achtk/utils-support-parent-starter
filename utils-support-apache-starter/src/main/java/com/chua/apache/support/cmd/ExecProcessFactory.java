package com.chua.apache.support.cmd;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.cmd.ProtozoaProcessFactory;
import com.chua.common.support.lang.cmd.ProcessFactory;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * apache
 *
 * @author CH
 * @since 2021-12-07
 */
@Spi(value = "exec", order = 1)
public final class ExecProcessFactory extends ProtozoaProcessFactory implements ProcessFactory {

    @Override
    public ProcessStatus exec(String cmd) {
        ProcessStatus processStatus = new ProcessStatus();

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(stdout);
        CommandLine cl = CommandLine.parse(cmd);
        for (String arg : args) {
            cl.addArgument(arg, handleQuoting);
        }

        cl.setSubstitutionMap(params);

        DefaultExecutor exec = new DefaultExecutor();
        if (timeout > 0) {
            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
            exec.setWatchdog(watchdog);
        }

        if (FileUtils.isWindows() && StringUtils.isNullOrEmpty(directory)) {
            directory = "C:";
        }
        exec.setWorkingDirectory(new File(directory));
        exec.setStreamHandler(psh);
        try {
            exec.execute(cl);
        } catch (Exception e) {
            processStatus.setCode(-1);
            processStatus.setOutput(e.getLocalizedMessage());
        }
        processStatus.setCode(0);
        processStatus.setOutput(stdout.toString());
        return processStatus;
    }
}
