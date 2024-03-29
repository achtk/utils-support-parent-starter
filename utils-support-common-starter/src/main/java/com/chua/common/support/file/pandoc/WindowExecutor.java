package com.chua.common.support.file.pandoc;

import com.chua.common.support.utils.CmdUtils;

import java.io.File;

/**
 * win环境执行器
 * @author CH
 */
public class WindowExecutor implements Executor{
    private File file;

    public WindowExecutor(File file) {
        this.file = file;
    }

    @Override
    public void execute(String inputFile, String outputFile) {
        CmdUtils.exec(String.format("%s %s -o %s", file.getAbsolutePath(), inputFile, outputFile));
    }
}
