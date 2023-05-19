package com.chua.common.support.file.pandoc;

import com.chua.common.support.utils.CmdUtils;

/**
 * 环境执行器
 * @author CH
 */
public class EnvironmentExecutor implements Executor{
    @Override
    public void execute(String inputFile, String outputFile) {
        CmdUtils.exec(String.format("pandoc %s -o %s", inputFile, outputFile));
    }
}
