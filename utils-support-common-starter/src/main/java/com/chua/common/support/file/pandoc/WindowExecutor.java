package com.chua.common.support.file.pandoc;

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
}
